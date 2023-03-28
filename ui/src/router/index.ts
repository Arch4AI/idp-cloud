import { RouteRecordRaw, createRouter, createWebHistory } from 'vue-router'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/pages/login/Index.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/pages/home/Index.vue')
  }
]

// 导出路由
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 这个是路由守卫
router.beforeEach((to, from, next) => {
  //这里可以判断是不是已经有用户登录了 没有的话直接进行跳转
  // 这里判断用户是否登录 否则有些页面无法进入
  // 还可以在这里判断角色权限 如果这个用户的权限不对 则直接跳转到默认的界面即可
  //直接下一步
  next()
})

// 路由跳转之后
router.afterEach(() => {
  // 跳转完成后直接把滚动条回到顶部
  window.scroll(0, 0)
})

export default router
