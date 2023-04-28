// 发送短信
export interface SmsSendReq {
  phone: string
}

// 发送短信回复的
export interface SmsSendResp {
  phone: string
  // code: string
  expireTime: Date
}

// 短信校验
export interface SmsCheckReq {
  phone: string
  code: string
}
