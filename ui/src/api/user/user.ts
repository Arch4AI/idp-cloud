import apiRequest from '@/request/request'

// 获取当前用户
export const getMe = () => {
  return apiRequest<Object, LocalUser>({
    url: '/user/me',
    method: 'get'
  })
}
