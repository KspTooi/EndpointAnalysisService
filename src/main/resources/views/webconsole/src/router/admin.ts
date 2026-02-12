import { useTabStore } from "@/store/TabHolder";
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
      component: () => import("@/views/auth/UserGroupManager.vue"),
      meta: {
        breadcrumb: {
          title: "用户组",
        },
      },
    },
    {
      path: "/permission-manager",
      name: "permission-manager",
      component: () => import("@/views/auth/PermissionManager.vue"),
      meta: {
        breadcrumb: {
          title: "权限管理",
        },
      },
    },
    {
      path: "/session-manager",
      name: "session-manager",
      component: () => import("@/views/auth/SessionManager.vue"),
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
      component: () => import("@/views/rdbg/UserRequestView.vue"),
      meta: {
        breadcrumb: {
          title: "端点调试工作台",
        },
      },
    },
    {
      path: "/user-request-env-manager",
      name: "user-request-env-manager",
      component: () => import("@/views/rdbg/UserRequestEnvManager.vue"),
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
      component: () => import("@/views/rdbg/SimpleFilterManager.vue"),
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
    {
      path: "/company-manager",
      name: "company-manager",
      component: () => import("@/views/core/CompanyManager.vue"),
      meta: {
        breadcrumb: {
          title: "公司管理",
        },
      },
    },
    {
      path: "/company-member-manager",
      name: "company-member-manager",
      component: () => import("@/views/core/CompanyMember.vue"),
      meta: {
        breadcrumb: {
          title: "公司成员管理",
        },
      },
    },
    {
      path: "/drive",
      name: "drive",
      component: () => import("@/views/drive/Drive.vue"),
      meta: {
        breadcrumb: {
          title: "团队云盘",
        },
      },
    },
    {
      path: "/drive/preview/pdf",
      name: "drive-preview-pdf",
      component: () => import("@/views/drive/PdfRenderer.vue"),
      meta: { breadcrumb: { title: "PDF预览" } },
    },
    {
      path: "/drive/preview/photo",
      name: "drive-preview-photo",
      component: () => import("@/views/drive/PhotoRenderer.vue"),
      meta: { breadcrumb: { title: "图片预览" } },
    },
    {
      path: "/drive/preview/video",
      name: "drive-preview-video",
      component: () => import("@/views/drive/VideoRenderer.vue"),
      meta: { breadcrumb: { title: "媒体播放" } },
    },
    {
      path: "/drive/preview/word",
      name: "drive-preview-word",
      component: () => import("@/views/drive/WordRenderer.vue"),
      meta: { breadcrumb: { title: "Word预览" } },
    },
    {
      path: "/drive/preview/excel",
      name: "drive-preview-excel",
      component: () => import("@/views/drive/ExcelRenderer.vue"),
      meta: { breadcrumb: { title: "Excel预览" } },
    },
    {
      path: "/drive/preview/code",
      name: "drive-preview-code",
      component: () => import("@/views/drive/CodeRenderer.vue"),
      meta: { breadcrumb: { title: "代码预览" } },
    },
    {
      path: "/rdbg-workspace",
      name: "rdbg-workspace",
      component: () => import("@/views/rdbg/RdbgWorkSpace.vue"),
      meta: { breadcrumb: { title: "请求调试器" } },
    },
  ],
});

// 路由守卫
router.beforeEach((to, from, next) => {
  // 当访问根路径 '/' 时进行检测
  if (to.path === "/") {
    const tabStore = useTabStore();

    //如果有激活的标签页
    if (tabStore.activeTabId) {
      // 在标签列表中查找该标签的具体信息
      const activeTab = tabStore.tabs.find((t) => t.id === tabStore.activeTabId);

      // 如果找到了激活的标签且其路径不是当前根路径，则跳转
      if (activeTab && activeTab.path !== "/") {
        return next(activeTab.path);
      }
    }
  }

  // 其他情况或没有激活标签时，继续正常访问
  next();
});

export default router;
