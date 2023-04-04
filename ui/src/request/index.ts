import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios'
import axios, { AxiosError, AxiosResponse } from 'axios'
import type {
  CreateRequestConfig,
  RequestConfig,
  RequestInterceptors
} from './types'
import userStore from '@/store/user'

class Request {
  // axios 实例
  instance: AxiosInstance
  // 拦截器对象
  interceptorsObj?: RequestInterceptors<AxiosResponse>
  // * 存放取消请求控制器Map
  abortControllerMap: Map<string, AbortController>

  constructor(config: CreateRequestConfig) {
    //定义默认的参数
    axios.defaults.withCredentials = true
    axios.defaults.maxRedirects = 0
    axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest'
    axios.defaults.headers['Access-Control-Allow-Origin'] = '*'
    // axios.defaults.headers['Content-Type'] = 'application/json'
    this.instance = axios.create(config)
    // * 初始化存放取消请求控制器Map
    this.abortControllerMap = new Map()
    this.interceptorsObj = config.interceptors
    // 拦截器执行顺序 接口请求 -> 实例请求 -> 全局请求 -> 实例响应 -> 全局响应 -> 接口响应
    this.instance.interceptors.request.use(
      (res: InternalAxiosRequestConfig) => {
        const controller = new AbortController()
        const url = res.url || ''
        res.signal = controller.signal
        this.abortControllerMap.set(url, controller)
        return res
      },
      (err: any) => err
    )

    // 使用实例拦截器
    this.instance.interceptors.request.use(
      this.interceptorsObj?.requestInterceptors,
      this.interceptorsObj?.requestInterceptorsCatch
    )
    this.instance.interceptors.response.use(
      this.interceptorsObj?.responseInterceptors,
      this.interceptorsObj?.responseInterceptorsCatch
    )
    // 全局响应拦截器保证最后执行
    this.instance.interceptors.response.use(
      // 因为我们接口的数据都在res.data下，所以我们直接返回res.data
      (res: AxiosResponse) => {
        const storeUser = userStore()
        const url = res.config.url || ''
        this.abortControllerMap.delete(url)
        if (res.data.code === 302) {
          storeUser.user = res.data
          window.location.href = res.data.msg
          return
        }
        return res.data
      },
      (err: any) => {
        if (err.response) {
          if (err.response.status === 403 || err.response.status === 403) {
            window.location.href = '/'
            return
          }
        }
        Promise.reject(err)
        //throw err
      }
    )
  }
  request<T>(config: RequestConfig<T>): Promise<T> {
    return new Promise((resolve, reject) => {
      // 如果我们为单个请求设置拦截器，这里使用单个请求的拦截器
      if (config.interceptors?.requestInterceptors) {
        config = config.interceptors.requestInterceptors(config as any)
      }
      this.instance
        .request<any, T>(config)
        .then((res) => {
          //如果本身是错误的话也进入reject
          if (res instanceof AxiosError) {
            reject(res)
          } else {
            // 只有不是错误的时候才进来
            // 如果我们为单个响应设置拦截器，这里使用单个响应的拦截器
            if (config.interceptors?.responseInterceptors) {
              res = config.interceptors.responseInterceptors(res)
            }
            // 警惕这里也可能是系统抛出来的错误
            resolve(res)
          }
        })
        .catch((err: any) => {
          //这里一定是错误
          reject(err)
        })
      // .finally(() => {})
    })
  }
  /**
   * 取消全部请求
   */
  cancelAllRequest() {
    for (const [, controller] of this.abortControllerMap) {
      controller.abort()
    }
    this.abortControllerMap.clear()
  }
  /*
   * 取消指定的请求
   * @param url 待取消的请求URL
   */
  cancelRequest(url: string | string[]) {
    const urlList = Array.isArray(url) ? url : [url]
    for (const _url of urlList) {
      this.abortControllerMap.get(_url)?.abort()
      this.abortControllerMap.delete(_url)
    }
  }
}

export default Request
export { RequestConfig, RequestInterceptors }
