import { Authoritity } from '../user/types'

// 账号密码登录
export interface PasswordLoginReq {
  userName: string
  password: string
  sourceId?: string //第三方登录的源ID
  uuid?: string //第三方登录返回的uuid
}

// 短信登录请求
export interface PhoneLoginReq {
  phone: string
  code: string
  sourceId?: string //第三方登录的源ID
  uuid?: string //第三方登录返回的uuid
}

// 用户注册
export interface UserRegisterReq {
  userName: string
  nickName: string
  password: string
  email: string
  phone: string
  gender?: string
  language?: string
  code?: string
  confirmPassword?: string
  sourceId?: string //第三方登录的源ID
  uuid?: string //第三方登录返回的uuid
}

// 更新用户信息 非必须都填
export interface UserProfileReq {
  userName?: string
  nickName?: string
  password?: string
  email?: string
  phone?: string
  gender?: number
  language?: string
  sourceId?: string //第三方的时候
  uuid?: string //第三方的时候必传
}

// 外部登录请求
export interface ExternalLoginReq {
  state: string
  code: string

  [key: string]: string //未知属性
}

// 登录成功返回的信息
export interface LoginResp {
  loginType: string
  authorities: Authoritity[]
  userId: string
  userName: string
  nickName: string
  gender: number
  phone: string
  email: string
  enabled: boolean
  createTime: Date
  updateTime: Date
  language: string
}

// 这个是第三方登录没有绑定用户返回的信息对应code=403
export interface ExternalSecurityResponse {
  sourceId?: string //对应源地址
  uuid?: string //对应用户uuid
  type?: string //对应类型 clientType
}
