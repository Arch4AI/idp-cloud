// 发送短信
interface smsSendReq {
  phone: string
}

// 短信校验
interface smsCheckReq {
  phone: string
  code: string
}
