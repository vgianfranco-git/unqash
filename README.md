# unqash
Repositorio de EIS para proyecto UNQash

## Estructura del repo

- `api/` — backend (Spring Boot + Postgres)
- `web/` — frontend (React + TypeScript + Vite + Tailwind)

## Requisitos

- Docker Desktop
- Java 21 + un IDE con soporte Maven (IntelliJ o Eclipse)
- Node.js + npm

## 1. Base de datos (Postgres vía Docker)

Desde `api/`:

```
docker compose up -d
```

Levanta Postgres en `localhost:5432` (db/user/pass: `unqash`/`unqash`/`unqash`). Verificar que esté corriendo con `docker ps`.

## 2. Backend (Spring Boot)

Abrir la carpeta `api/` como proyecto Maven en tu IDE y correr `UnqashApplication.java`:

- **IntelliJ**: abrir `api/`, esperar a que indexe, y darle **Run** sobre `UnqashApplication.java`.
- **Eclipse**: `File → Import → Maven → Existing Maven Projects` apuntando a `api/`, y correr `UnqashApplication.java` con **Run As → Java Application** (o **Spring Boot App** si tenés Spring Tools 4).

Al arrancar, Flyway crea las tablas y carga los tipos de cobro solo. Queda escuchando en `http://localhost:8080`. Se puede probar con:

```
curl http://localhost:8080/tipos-cobro
```

## 3. Frontend (React + Vite)

Desde `web/`:

```
npm install
npm run dev
```

Levanta en `http://localhost:5173` (o el siguiente puerto libre). Lee la URL del backend desde `web/.env` (`VITE_API_URL=http://localhost:8080`).

## 4. Ver los datos persistidos

Con un cliente gráfico (DBeaver, IntelliJ Database, etc.), conectar con:

- Host: `localhost`
- Puerto: `5432`
- Base de datos: `unqash`
- Usuario: `unqash`
- Contraseña: `unqash`

O por consola, sin instalar nada, desde el propio contenedor:

```
docker exec api-postgres-1 psql -U unqash -d unqash -c "SELECT * FROM UNQASH_VENTA;"
```

## Orden recomendado para arrancar de cero

1. `docker compose up -d` (en `api/`)
2. Run `UnqashApplication` desde IntelliJ
3. `npm run dev` (en `web/`)
