import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'request-manager',
      component: () => import('@/views/admin/RequestManager.vue'),
      meta: {
        breadcrumb: {
          title: '请求管理'
        }
      }
    },
  ],
})

export default router 