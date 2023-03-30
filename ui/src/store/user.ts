import { getMe } from '@/api/user/user'
import { defineStore } from 'pinia'

// 用户状态
interface UserState {
  user: LocalUser
}

// 用户缓存 必须有初始值 这个有点恶心
const userStore = defineStore('UserSate', {
  state: (): UserState => ({
    user:{}
  }),
  getters: {
  },
  actions: {
    // 检查是否已经登录 这个接口可以根据当前的sessionId或者remeberMe获取到登录用户信息
    async checkLogin(){
      const res = await getMe()
      if(res.code == 200){
        this.user = res.data
      }else{
        this.user = {}
        console.log(res.msg)
      }
    }
  },
  // 同时支持localstorage sessionStorage
  persist: {
    enabled: true,
    strategies:[
      {storage: localStorage},
      {storage: sessionStorage}
    ]
  }
})

export default userStore
