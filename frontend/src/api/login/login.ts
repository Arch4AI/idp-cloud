import apiRequest from '@/request/request'
import {
  ExternalLoginReq,
  PasswordLoginReq,
  PhoneLoginReq,
  UserProfileReq,
  UserRegisterReq
} from './types'
import { LocalUser } from '../user/types'

// 这个是账号密码登录
export const loginByPwd = (data: PasswordLoginReq) => {
  return apiRequest<PasswordLoginReq, LocalUser>({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 手机登录
export const loginByPhone = (data: PhoneLoginReq) => {
  return apiRequest<PhoneLoginReq, LocalUser>({
    url: '/phone/login',
    method: 'post',
    data
  })
}

// 第三方登录 这里需要做很多事情
export const loginByExternal = (data: ExternalLoginReq) => {
  return apiRequest<ExternalLoginReq, LocalUser>({
    url: '/external/login',
    method: 'post',
    data
  })
}

// 用户注册 注册完成后自动完成登录
export const register = (data: UserRegisterReq) => {
  return apiRequest<UserRegisterReq, LocalUser>({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 补充信息或者是修改用户信息 如果是补充信息会自动实现登录
export const updateProfile = (data: UserProfileReq) => {
  return apiRequest<UserProfileReq, LocalUser>({
    url: '/user/profile',
    method: 'post',
    data
  })
}
