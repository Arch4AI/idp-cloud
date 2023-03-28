import apiRequest from '@/request/request'

// 这个是账号密码登录
export const loginByPwd = (data: PasswordLoginReq) => {
  return apiRequest<PasswordLoginReq, LoginResp>({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 手机登录
export const loginByPhone = (data: PhoneLoginReq) => {
  return apiRequest<PhoneLoginReq, LoginResp>({
    url: '/phone/login',
    method: 'post',
    data
  })
}

// 第三方登录 这里需要做很多事情
export const loginByExternal = (data: ExternalLoginReq) => {
  return apiRequest<ExternalLoginReq, LoginResp>({
    url: '/external/login',
    method: 'post',
    data
  })
}

// 用户注册 注册完成后自动完成登录
export const register = (data: UserRegisterReq) => {
  return apiRequest<UserRegisterReq, LoginResp>({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 补充信息或者是修改用户信息 如果是补充信息会自动实现登录
export const updateProfile = (data: UserProfileReq) => {
  return apiRequest<UserProfileReq, LoginResp>({
    url: '/user/profile',
    method: 'post',
    data
  })
}
