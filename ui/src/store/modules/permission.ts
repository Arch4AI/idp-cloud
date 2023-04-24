import { defineStore } from 'pinia'
import { RouteRecordRaw } from 'vue-router'

import router, { fixedRouterList } from '@/router'
import { store } from '@/store'
import { PAGE_NOT_FOUND_ROUTE } from '@/utils/route/constant'

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    whiteListRouters: ['/login', '/home'],
    routers: [] as any[],
    removeRoutes: [],
    asyncRoutes: [] as any[]
  }),
  actions: {
    async initRoutes() {
      const accessedRouters = this.asyncRoutes
      // 在菜单展示全部路由
      this.routers = [...fixedRouterList, ...accessedRouters]
    },
    async buildAsyncRoutes() {
      try {
        this.asyncRoutes = [PAGE_NOT_FOUND_ROUTE]
        await this.initRoutes()
        return this.asyncRoutes
      } catch (error) {
        throw new Error("Can't build routes")
      }
    },
    async restoreRoutes() {
      this.removeRoutes.forEach((item: RouteRecordRaw) => {
        router.addRoute(item)
      })
    }
  }
})

export function getPermissionStore() {
  return usePermissionStore(store)
}
