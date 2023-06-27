import Layout from '@/layouts/blank.vue'
import { t } from '@/locales'

export default [
  {
    path: '/result',
    name: 'result',
    component: Layout,
    redirect: '/result/success',
    meta: { title: 'result.home', icon: 'check-circle' },
    children: [
      {
        path: '403',
        name: 'Result403',
        component: () => import('@/pages/result/403/index.vue'),
        meta: { title: '无权限' }
      },
      {
        path: '404',
        name: 'Result404',
        component: () => import('@/pages/result/404/index.vue'),
        meta: { title: '访问页面不存在页' }
      },
      {
        path: '500',
        name: 'Result500',
        component: () => import('@/pages/result/500/index.vue'),
        meta: { title: '服务器出错页' }
      }
    ]
  }
]
