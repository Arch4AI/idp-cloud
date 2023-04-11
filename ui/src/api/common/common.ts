import apiRequest from '@/request/request'

// 获取验证码
export const getCaptcha = (data: Captcha) => {
  return apiRequest<Captcha, Captcha>({
    url: '/anonymous/captcha',
    method: 'post',
    data
  })
}
