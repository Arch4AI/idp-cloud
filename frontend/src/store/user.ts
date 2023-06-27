import { getMe } from '@/api/user/user'
import { defineStore } from 'pinia'
import { logout } from '@/api/logout/logout'
import { store } from '@/store'
import { useRouter } from 'vue-router'
import { LocalUser } from '@/api/user/types'
// import { LangName } from "@/locales";
// import {useLocale} from "@/locales/useLocal";

// 用户状态
interface UserState {
  user: LocalUser
}

// const { changeLocale } = useLocale();

// 用户缓存 必须有初始值 这个有点恶心
const userStore = defineStore('UserSate', {
  state: (): UserState => ({
    user: {}
  }),
  getters: {},
  actions: {
    // 检查是否已经登录 这个接口可以根据当前的sessionId或者remeberMe获取到登录用户信息
    async checkLogin() {
      const res = await getMe()
      const router = useRouter()

      console.log(window.location.pathname)
      if (res.code == 200) {
        this.user = res.data
        // changeLocale(<LangName>this.user.language)
      } else {
        this.user = {}
      }
    },
    async logout() {
      await logout()
    }
  },
  // 同时支持localstorage sessionStorage
  persist: {
    enabled: true,
    strategies: [{ storage: localStorage }, { storage: sessionStorage }]
  }
})

export function getUserStore() {
  return userStore(store)
}
