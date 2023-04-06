import { loginByExternal } from '@/api/login/login'
import externalInfoStore from '@/store/externalUser'
import userStore from '@/store/user'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  createRouter,
  createWebHistory,
  LocationQuery,
  RouteRecordRaw
} from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    redirect: '/login'
    //component: () => import('@/pages/home/Index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/login/Index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/register/Index.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/pages/dashboard/Index.vue'),
    beforeEnter: (to, from, next) => {
      //这个可以使用 局部的路由守卫
      next()
    }
  }
]

// 导出路由
const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_APP_BASE_URL),
  routes
})

// 全局这个是路由守卫
router.beforeEach(async (to, from, next) => {
  //这里可以判断是不是已经有用户登录了 没有的话直接进行跳转
  // 这里判断用户是否登录 否则有些页面无法进入
  // 还可以在这里判断角色权限 如果这个用户的权限不对 则直接跳转到默认的界面即可
  //直接下一步
  const storeUser = userStore()
  if (to.query) {
    const query: LocationQuery = to.query
    // 仅仅针对oauth2.0对接的情况 一定会有这2个东西 其余的都是附带的
    if (query['state'] && query['code']) {
      //这里说明可以进来了
      const req: ExternalLoginReq = query as ExternalLoginReq
      const res = await loginByExternal(req)
      if (res.code === 200) {
        storeUser.user = res.data
        next('dashboard')
        return
      } else if (res.code === 403) {
        // 说明没有登录 需要将信息给到绑定的页面去进行绑定 //TODO
        const resp: ExternalSecurityResponse =
          res.data as ExternalSecurityResponse
        const stroeExternalInfo = externalInfoStore()
        stroeExternalInfo.info = resp
        next('login')
        return
      } else {
        MessagePlugin.error(res.msg)
        next('login')
        return
      }
    }
  }
  next()
})

// 路由跳转之后
router.afterEach(() => {
  // 跳转完成后直接把滚动条回到顶部
  window.scroll(0, 0)
})

export default router
