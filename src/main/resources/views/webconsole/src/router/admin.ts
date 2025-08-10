import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/user-manager',
      name: 'user-manager',
      component: () => import('@/views/user/UserManager.vue'),
      meta: {
        breadcrumb: {
          title: '用户列表'
        }
      }
    },
    {
      path: '/group-manager',
      name: 'group-manager',
      component: () => import('@/views/user/UserGroupManager.vue'),
      meta: {
        breadcrumb: {
          title: '用户组'
        }
      }
    },
    {
      path: '/permission-manager',
      name: 'permission-manager',
      component: () => import('@/views/user/PermissionManager.vue'),
      meta: {
        breadcrumb: {
          title: '权限管理'
        }
      }
    },
    {
      path: '/session-manager',
      name: 'session-manager',
      component: () => import('@/views/user/SessionManager.vue'),
      meta: {
        breadcrumb: {
          title: '会话管理'
        }
      }
    },
    {
      path: '/config-manager',
      name: 'config-manager',
      component: () => import('@/views/core/ConfigManager.vue'),
      meta: {
        breadcrumb: {
          title: '配置管理'
        }
      }
    },
    {
      path: '/application-maintain',
      name: 'application-maintain',
      component: () => import('@/views/core/ApplicationMaintain.vue'),
      meta: {
        breadcrumb: {
          title: '维护中心'
        }
      }
    },
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
    {
      path: '/relay-server-manager',
      name: 'relay-server-manager',
      component: () => import('@/views/admin/RelayServerManager.vue'),
      meta: {
        breadcrumb: {
          title: '中继通道配置'
        }
      }
    },
    {
      path: '/ep-doc-manager',
      name: 'ep-doc-manager',
      component: () => import('@/views/admin/EpDocManager.vue'),
      meta: {
        breadcrumb: {
          title: '端点文档配置'
        }
      }
    }
  ],
})

export default router 