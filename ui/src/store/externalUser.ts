import { defineStore } from 'pinia'

// 外部用户的信息
interface ExternalInfo {
  info: ExternalSecurityResponse
}

// 用户缓存 必须有初始值 这个有点恶心
const externalInfoStore = defineStore('external_info', {
  state: (): ExternalInfo => ({
    info: {}
  }),
  getters: {},
  actions: {},
  // 同时支持localstorage sessionStorage
  persist: {
    enabled: true,
    strategies: [{ storage: localStorage }, { storage: sessionStorage }]
  }
})

export default externalInfoStore
