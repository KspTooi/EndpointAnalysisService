import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/user-manager",
      name: "user-manager",
      component: () => import("@/views/core/UserManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户列表",
        },
      },
    },
    {
      path: "/group-manager",
      name: "group-manager",
      component: () => import("@/views/core/UserGroupManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户组",
        },
      },
    },
    {
      path: "/permission-manager",
      name: "permission-manager",
      component: () => import("@/views/core/PermissionManager.vue"),
      meta: {
        breadcrumb: {
          title: "权限管理",
        },
      },
    },
    {
      path: "/session-manager",
      name: "session-manager",
      component: () => import("@/views/core/SessionManager.vue"),
      meta: {
        breadcrumb: {
          title: "会话管理",
        },
      },
    },
    {
      path: "/config-manager",
      name: "config-manager",
      component: () => import("@/views/core/ConfigManager.vue"),
      meta: {
        breadcrumb: {
          title: "配置管理",
        },
      },
    },
    {
      path: "/application-maintain",
      name: "application-maintain",
      component: () => import("@/views/core/ApplicationMaintain.vue"),
      meta: {
        breadcrumb: {
          title: "维护中心",
        },
      },
    },
    {
      path: "/request-manager",
      name: "request-manager",
      component: () => import("@/views/relay/RequestManager.vue"),
      meta: {
        breadcrumb: {
          title: "中继请求记录",
        },
      },
    },
    {
      path: "/replay-request-manager",
      name: "replay-request-manager",
      component: () => import("@/views/relay/ReplayRequestManager.vue"),
      meta: {
        breadcrumb: {
          title: "简单请求重放",
        },
      },
    },
    {
      path: "/user-request-manager",
      name: "user-request-manager",
      component: () => import("@/views/requestdebug/UserRequestView.vue"),
      meta: {
        breadcrumb: {
          title: "端点调试工作台",
        },
      },
    },
    {
      path: "/user-request-env-manager",
      name: "user-request-env-manager",
      component: () => import("@/views/requestdebug/UserRequestEnvManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户请求环境管理",
        },
      },
    },
    //基本过滤器
    {
      path: "/simple-filter-manager",
      name: "simple-filter-manager",
      component: () => import("@/views/requestdebug/SimpleFilterManager.vue"),
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
      component: () => import("@/views/relay/RelayServerManager.vue"),
      meta: {
        breadcrumb: {
          title: "中继通道配置",
        },
      },
    },
    {
      path: "/ep-doc-manager",
      name: "ep-doc-manager",
      component: () => import("@/views/document/EpDocManager.vue"),
      meta: {
        breadcrumb: {
          title: "端点文档配置",
        },
      },
    },
    {
      path: "/ep-doc-viewer",
      name: "ep-doc-viewer",
      component: () => import("@/views/document/EpDocViewer.vue"),
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
      component: () => import("@/views/relay/RouteServerManager.vue"),
      meta: {
        breadcrumb: {
          title: "路由服务器管理",
        },
      },
    },
    {
      path: "/route-group",
      name: "route-group",
      component: () => import("@/views/relay/RouteRuleManager.vue"),
      meta: {
        breadcrumb: {
          title: "路由策略组管理",
        },
      },
    },
    {
      path: "/endpoint-manager",
      name: "endpoint-manager",
      component: () => import("@/views/core/EndpointManager.vue"),
      meta: {
        breadcrumb: {
          title: "端点管理",
        },
      },
    },
  ],
});

export default router;
