import apiRequest from '@/request/request'

// 发送短信
export const sendSms = (data: SmsSendReq) => {
  return apiRequest<SmsSendReq, SmsSendResp>({
    url: '/sms/send',
    method: 'post',
    data
  })
}

//检查验证码是否正常
export const checkCode = (data: SmsCheckReq) => {
  return apiRequest<SmsCheckReq, string>({
    url: '/sms/check',
    method: 'post',
    data
  })
}
