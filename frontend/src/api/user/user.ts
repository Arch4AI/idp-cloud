import apiRequest from '@/request/request'
import { LocalUser } from './types'

// 获取当前用户
export const getMe = () => {
  return apiRequest<object, LocalUser>({
    url: '/user/me',
    method: 'get'
  })
}
