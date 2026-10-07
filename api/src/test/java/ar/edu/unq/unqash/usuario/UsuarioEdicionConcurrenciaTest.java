package ar.edu.unq.unqash.usuario;

import ar.edu.unq.unqash.persistencia.UsuarioEntity;
import ar.edu.unq.unqash.persistencia.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:usuarios-edicion-concurrencia;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
})
@Import(UsuarioEdicionConcurrenciaTest.CoordinacionConfig.class)
class UsuarioEdicionConcurrenciaTest {

    private static final UUID GESTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final UUID USUARIO_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private static final LocalDateTime FECHA_ALTA = LocalDateTime.of(2026, 10, 2, 10, 0);
    private static final Coordinador COORDINADOR = new Coordinador();

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        COORDINADOR.metodo = null;
        usuarioRepository.deleteAll();
        usuarioRepository.save(new UsuarioEntity(GESTOR_ID, "Gestor", "Admin", "40123450",
                "gestor@example.test", "1140123450", "hash-gestor", FECHA_ALTA, "SUPER_ADMIN", true));
        usuarioRepository.save(new UsuarioEntity(USUARIO_ID, "Original", "Usuario", "40123451",
                "usuario@example.test", "1140123451", new BCryptPasswordEncoder().encode("OriginalSegura1!"),
                FECHA_ALTA, "ADMIN_ALTA", true));
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void unaEdicionConDatosObsoletosNoRestauraElHashNiElRolDeOtraEdicion() throws Exception {
        CountDownLatch usuarioLeido = new CountDownLatch(1);
        CountDownLatch continuar = new CountDownLatch(1);
        COORDINADOR.pausar("findById", USUARIO_ID, usuarioLeido, continuar);

        try (var executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "edicion-pausada"))) {
            Future<MvcResult> obsoleta = executor.submit(() -> editar(datos("Obsoleto", "40123451",
                    "usuario@example.test", "1140123451", "\"contrasena\":\"\"")));
            try {
                assertThat(usuarioLeido.await(10, TimeUnit.SECONDS)).isTrue();
                assertThat(editar(datos("Nuevo", "40123451", "usuario@example.test", "1140123451",
                        "\"esGestor\":false,\"contrasena\":\"NuevaSegura1!\""))
                        .getResponse().getStatus()).isEqualTo(200);
            } finally {
                continuar.countDown();
            }
            assertThat(obsoleta.get(10, TimeUnit.SECONDS).getResponse().getStatus()).isEqualTo(409);
        }

        UsuarioEntity guardado = usuarioRepository.findById(USUARIO_ID).orElseThrow();
        assertThat(guardado.getApellido()).isEqualTo("Nuevo");
        assertThat(guardado.getEsGestor()).isFalse();
        assertThat(new BCryptPasswordEncoder().matches("NuevaSegura1!", guardado.getContrasenaHash())).isTrue();
        assertThat(guardado.getFechaHoraAlta()).isEqualTo(FECHA_ALTA);
        assertThat(guardado.getUsuarioAlta()).isEqualTo("ADMIN_ALTA");
    }

    @ParameterizedTest
    @CsvSource({"dni, DNI ya registrado", "email, email ya registrado", "telefono, teléfono ya registrado"})
    void unaColisionDespuesDeValidarUnicidadDevuelveElErrorDeDuplicado(String campo, String detalle) throws Exception {
        CountDownLatch unicidadVerificada = new CountDownLatch(1);
        CountDownLatch continuar = new CountDownLatch(1);
        switch (campo) {
            case "dni" -> COORDINADOR.pausar("existsByDniAndIdNot", "40234567", unicidadVerificada, continuar);
            case "email" -> COORDINADOR.pausar("existsByEmailAndIdNot", "nuevo@example.test", unicidadVerificada, continuar);
            case "telefono" -> COORDINADOR.pausar("existsByTelefonoAndIdNot", "1198765432", unicidadVerificada, continuar);
        }

        try (var executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "edicion-pausada"))) {
            Future<MvcResult> edicion = executor.submit(() -> editar(datos("Actualizado", "40234567",
                    "nuevo@example.test", "1198765432", "\"esGestor\":false")));
            try {
                assertThat(unicidadVerificada.await(10, TimeUnit.SECONDS)).isTrue();
                usuarioRepository.saveAndFlush(new UsuarioEntity(UUID.randomUUID(), "Competidor", "Usuario",
                        campo.equals("dni") ? "40234567" : "40987654",
                        campo.equals("email") ? "nuevo@example.test" : "competidor@example.test",
                        campo.equals("telefono") ? "1198765432" : "1155555555", "hash-competidor",
                        FECHA_ALTA, "OTRO_ADMIN", false));
            } finally {
                continuar.countDown();
            }
            assertThatCode(() -> {
                var respuesta = edicion.get(10, TimeUnit.SECONDS).getResponse();
                assertThat(respuesta.getStatus()).isEqualTo(400);
                assertThat(respuesta.getContentAsString()).contains(detalle);
            }).doesNotThrowAnyException();
        }

        UsuarioEntity original = usuarioRepository.findById(USUARIO_ID).orElseThrow();
        assertThat(original.getApellido()).isEqualTo("Original");
        assertThat(original.getDni()).isEqualTo("40123451");
        assertThat(original.getEmail()).isEqualTo("usuario@example.test");
        assertThat(original.getTelefono()).isEqualTo("1140123451");
        assertThat(original.getEsGestor()).isTrue();
        assertThat(usuarioRepository.count()).isEqualTo(3);
    }

    private MvcResult editar(String datos) throws Exception {
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute("usuarioId", GESTOR_ID.toString());
        return mockMvc.perform(put("/usuarios/{idUsuario}", USUARIO_ID).session(sesion)
                .contentType(MediaType.APPLICATION_JSON).content(datos)).andReturn();
    }

    private String datos(String apellido, String dni, String email, String telefono, String opcionales) {
        return """
                {"apellido":"%s","nombre":"Usuario","dni":"%s","email":"%s","telefono":"%s",%s}
                """.formatted(apellido, dni, email, telefono, opcionales);
    }

    private static class Coordinador {
        private volatile String metodo;
        private Object primerArgumento;
        private CountDownLatch consultaTerminada;
        private CountDownLatch continuar;

        void pausar(String metodo, Object primerArgumento, CountDownLatch consultaTerminada, CountDownLatch continuar) {
            this.primerArgumento = primerArgumento;
            this.consultaTerminada = consultaTerminada;
            this.continuar = continuar;
            this.metodo = metodo;
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class CoordinacionConfig {
        @Bean
        static BeanPostProcessor coordinarRepositorio() {
            return new BeanPostProcessor() {
                @Override
                public Object postProcessAfterInitialization(Object bean, String beanName) {
                    if (!(bean instanceof UsuarioRepository)) {
                        return bean;
                    }
                    ProxyFactory proxy = new ProxyFactory(bean);
                    proxy.addAdvice((MethodInterceptor) invocation -> {
                        Object resultado = invocation.proceed();
                        if (invocation.getMethod().getName().equals(COORDINADOR.metodo)
                                && Thread.currentThread().getName().equals("edicion-pausada")
                                && COORDINADOR.primerArgumento.equals(invocation.getArguments()[0])) {
                            if (COORDINADOR.metodo.startsWith("exists")) {
                                assertThat(resultado).isEqualTo(false);
                            }
                            COORDINADOR.consultaTerminada.countDown();
                            assertThat(COORDINADOR.continuar.await(10, TimeUnit.SECONDS)).isTrue();
                        }
                        return resultado;
                    });
                    return proxy.getProxy();
                }
            };
        }
    }
}
