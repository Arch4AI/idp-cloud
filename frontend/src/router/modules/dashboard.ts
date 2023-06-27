import Layout from '@/layouts/index.vue'

export default [
  {
    path: '/dashboard',
    component: Layout,
    redirect: '/dashboard/base',
    name: 'dashboard',
    meta: {
      title: 'home.dashboard',
      icon: 'dashboard',
      orderNo: 0
    },
    children: [
      {
        path: 'base',
        name: 'DashboardBase',
        component: () => import('@/pages/dashboard/base/index.vue'),
        meta: {
          title: 'home.dashboardBase'
        }
      }
    ]
  }
]
