// 发送短信
interface SmsSendReq {
  phone: string
}

// 发送短信回复的
interface SmsSendResp {
  phone: string
  code: string
  expireTime: Date
}

// 短信校验
interface SmsCheckReq {
  phone: string
  code: string
}
