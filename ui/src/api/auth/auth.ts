import apiRequest from "@/request/request"

// 获取所有的内容
export const listActiveProviders = ()=>{
    return apiRequest<null, Array<ExternalAuthSource>>({
        url: '/auth/providers',
        method: 'get',
      })
}

// 获取登录信息
export const getProviderRedirectUri = (id:string)=>{
    return apiRequest<null, ExternalLoginUri>({
        url: '/auth/providers/'+id,
        method: 'get',
      })
}