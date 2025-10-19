import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/user-manager",
      name: "user-manager",
      component: () => import("@/views/user/UserManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户列表",
        },
      },
    },
    {
      path: "/group-manager",
      name: "group-manager",
      component: () => import("@/views/user/UserGroupManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户组",
        },
      },
    },
    {
      path: "/permission-manager",
      name: "permission-manager",
      component: () => import("@/views/user/PermissionManager.vue"),
      meta: {
        breadcrumb: {
          title: "权限管理",
        },
      },
    },
    {
      path: "/session-manager",
      name: "session-manager",
      component: () => import("@/views/user/SessionManager.vue"),
      meta: {
        breadcrumb: {
          title: "会话管理",
        },
      },
    },
    {
      path: "/config-manager",
      name: "config-manager",
      component: () => import("@/views/admin/ConfigManager.vue"),
      meta: {
        breadcrumb: {
          title: "配置管理",
        },
      },
    },
    {
      path: "/application-maintain",
      name: "application-maintain",
      component: () => import("@/views/admin/ApplicationMaintain.vue"),
      meta: {
        breadcrumb: {
          title: "维护中心",
        },
      },
    },
    {
      path: "/request-manager",
      name: "request-manager",
      component: () => import("@/views/admin/RequestManager.vue"),
      meta: {
        breadcrumb: {
          title: "中继请求记录",
        },
      },
    },
    {
      path: "/replay-request-manager",
      name: "replay-request-manager",
      component: () => import("@/views/admin/ReplayRequestManager.vue"),
      meta: {
        breadcrumb: {
          title: "简单请求重放",
        },
      },
    },
    {
      path: "/user-request-manager",
      name: "user-request-manager",
      component: () => import("@/views/admin/UserRequestView.vue"),
      meta: {
        breadcrumb: {
          title: "端点调试工作台",
        },
      },
    },
    //基本过滤器
    {
      path: "/simple-filter-manager",
      name: "simple-filter-manager",
      component: () => import("@/views/admin/SimpleFilterManager.vue"),
      meta: {
        breadcrumb: {
          title: "基本过滤器",
        },
      },
    },
    //中继通道配置
    {
      path: "/relay-server-manager",
      name: "relay-server-manager",
      component: () => import("@/views/admin/RelayServerManager.vue"),
      meta: {
        breadcrumb: {
          title: "中继通道配置",
        },
      },
    },
    {
      path: "/ep-doc-manager",
      name: "ep-doc-manager",
      component: () => import("@/views/admin/EpDocManager.vue"),
      meta: {
        breadcrumb: {
          title: "端点文档配置",
        },
      },
    },
    {
      path: "/ep-doc-viewer",
      name: "ep-doc-viewer",
      component: () => import("@/views/admin/EpDocViewer.vue"),
      meta: {
        breadcrumb: {
          title: "端点文档查看器",
        },
      },
    },
    {
      path: "/menu-manager",
      name: "menu-manager",
      component: () => import("@/views/core/MenuManager.vue"),
      meta: {
        breadcrumb: {
          title: "菜单管理",
        },
      },
    },
    {
      path: "/route-server",
      name: "route-server",
      component: () => import("@/views/route/RouteServerManager.vue"),
      meta: {
        breadcrumb: {
          title: "路由服务器管理",
        },
      },
    },
    {
      path: "/route-group",
      name: "route-group",
      component: () => import("@/views/route/RouteRuleManager.vue"),
      meta: {
        breadcrumb: {
          title: "路由策略组管理",
        },
      },
    },
  ],
});

export default router;
