import uniq from 'lodash/uniq'
import { loginByExternal } from '@/api/login/login'
import externalInfoStore from '@/store/externalUser'
import { getUserStore } from '@/store/user'
import { MessagePlugin } from 'tdesign-vue-next'
import {
  createRouter,
  createWebHistory,
  LocationQuery,
  RouteRecordRaw,
  useRoute
} from 'vue-router'
import { ExternalLoginReq, ExternalSecurityResponse } from '@/api/login/types'

// 导入modules相关固定路由
const fixedModules = import.meta.glob('./modules/**/**.ts', { eager: true })
const defaultRouterList: Array<RouteRecordRaw> = [
  {
    path: '/',
    redirect: '/dashboard/base'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/pages/home/index.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/register/index.vue')
  }
]

// 存放固定路由
export const fixedRouterList: Array<RouteRecordRaw> =
  mapModuleRouterList(fixedModules)

export const allRoutes = [...fixedRouterList, ...defaultRouterList]

// 固定路由模块转换为路由
export function mapModuleRouterList(
  modules: Record<string, unknown>
): Array<RouteRecordRaw> {
  const routerList: Array<RouteRecordRaw> = []
  Object.keys(modules).forEach((key) => {
    const mod = modules[key].default || {}
    const modList = Array.isArray(mod) ? [...mod] : [mod]
    routerList.push(...modList)
  })
  return routerList
}

// 导出路由
const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_APP_BASE_URL),
  routes: allRoutes
})

// 全局这个是路由守卫
router.beforeEach(async (to, from, next) => {
  //这里可以判断是不是已经有用户登录了 没有的话直接进行跳转
  // 这里判断用户是否登录 否则有些页面无法进入
  // 还可以在这里判断角色权限 如果这个用户的权限不对 则直接跳转到默认的界面即可
  //直接下一步
  const storeUser = getUserStore()
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

export const getRoutesExpanded = () => {
  const expandedRoutes: Array<any> | null | undefined = []

  fixedRouterList.forEach((item) => {
    if (item.meta && item.meta.expanded) {
      expandedRoutes.push(item.path)
    }
    if (item.children && item.children.length > 0) {
      item.children
        .filter((child) => child.meta && child.meta.expanded)
        .forEach((child: RouteRecordRaw) => {
          expandedRoutes.push(item.path)
          expandedRoutes.push(`${item.path}/${child.path}`)
        })
    }
  })
  return uniq(expandedRoutes)
}

export const getActive = (maxLevel = 3): string => {
  const route = useRoute()
  if (!route.path) {
    return ''
  }
  return route.path
    .split('/')
    .filter((_item: string, index: number) => index <= maxLevel && index > 0)
    .map((item: string) => `/${item}`)
    .join('')
}

export default router
