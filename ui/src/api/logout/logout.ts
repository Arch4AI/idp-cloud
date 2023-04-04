import apiRequest from '@/request/request'

// 登出
export const logout = () => {
  return apiRequest<string, string>({
    url: '/logout',
    method: 'get',
    interceptors: {
      //单独的拦截器 在登出的地方清除sessionStroage localStorage
      responseInterceptors(res) {
        sessionStorage.clear()
        localStorage.clear()
        return res
      }
    }
  })
}
