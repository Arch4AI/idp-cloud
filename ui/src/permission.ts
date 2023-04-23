import 'nprogress/nprogress.css' // progress bar style

import NProgress from 'nprogress' // progress bar

import { MessagePlugin } from 'tdesign-vue-next'
import { RouteRecordName, RouteRecordRaw } from 'vue-router'

import router from '@/router'
import { getPermissionStore } from '@/store'
import userStore from '@/store/user'
import { PAGE_NOT_FOUND_ROUTE } from './utils/route/constant'

NProgress.configure({ showSpinner: false })

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const permissionStore = getPermissionStore()
  const { whiteListRouters } = permissionStore

  const storeUser = userStore()
  await storeUser.checkLogin()
  const user = storeUser.user
  debugger
  if (user.enabled) {
    if (to.path === '/login') {
      next()
      return
    }

    const { asyncRoutes } = permissionStore
    if (asyncRoutes && asyncRoutes.length === 0) {
      const routeList = await permissionStore.buildAsyncRoutes()
      routeList.forEach((item: RouteRecordRaw) => {
        router.addRoute(item)
      })

      if (to.name === PAGE_NOT_FOUND_ROUTE.name) {
        // 动态添加路由后，此处应当重定向到fullPath，否则会加载404页面内容
        next({ path: to.fullPath, replace: true, query: to.query })
      } else {
        const redirect = decodeURIComponent(
          (from.query.redirect || to.path) as string
        )
        const path =
          to.path === redirect ? { ...to, replace: true } : { path: redirect }
        next(path)
        return
      }
    }

    try {
      if (router.hasRoute(<RouteRecordName>to.name)) {
        next()
      } else {
        next(`/`)
      }
    } catch (error) {
      await MessagePlugin.error("Can't build router")
      next({
        path: '/login',
        query: { redirect: encodeURIComponent(to.fullPath) }
      })
      NProgress.done()
    }
    NProgress.done()
  } else {
    /* white list router */
    if (whiteListRouters.indexOf(to.path) !== -1) {
      next()
    } else {
      next({
        path: '/login',
        query: { redirect: encodeURIComponent(to.fullPath) }
      })
    }
    NProgress.done()
  }
})

