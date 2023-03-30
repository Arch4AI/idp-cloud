import apiRequest from '@/request/request'

// 发送短信
export const sendSms = (data: SmsSendReq) => {
  return apiRequest<SmsSendReq, SmsSendResp>({
    url: '/sms/send',
    method: 'post',
    data
  })
}
