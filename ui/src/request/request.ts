import Request from './'
import { AxiosResponse } from 'axios'
import type { RequestConfig } from './types'

export interface Response<T> {
  code: number
  msg: string
  data: T
}

// 重写返回类型
interface ApiRequestConfig<T, R> extends RequestConfig<Response<R>> {
  data?: T
}

const request = new Request({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 1000 * 60 * 5,
  headers: {
    'Content-Type': 'application/json;charset=utf-8;'
  },
  interceptors: {
    // 请求拦截器
    requestInterceptors: (config) => config,
    // 响应拦截器
    responseInterceptors: (result: AxiosResponse) => {
      return result
    }
  }
})

/**
 * @description: 函数的描述
 * @generic D 请求参数
 * @generic T 响应结构
 * @param {ApiRequestConfig} config 不管是GET还是POST请求都使用data
 * @returns {Promise}
 */
const apiRequest = <D = any, T = any>(config: ApiRequestConfig<D, T>) => {
  const { method = 'GET' } = config
  if (method === 'get' || method === 'GET') {
    config.params = config.data
  }
  return request.request<Response<T>>(config)
}

export default apiRequest
