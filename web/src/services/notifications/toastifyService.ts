import { toast } from 'react-toastify'
import type { NotificationService } from './notificationService'

export const toastifyService: NotificationService = {
  success: (message) => toast.success(message),
  error: (message) => toast.error(message),
}
