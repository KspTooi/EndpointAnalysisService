import GenricRouteService from "@/soa/genric-route/service/GenricRouteService";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";

const routes: RouteEntryPo[] = [
  {
    biz: "core",
    path: "user-manager",
    name: "user-manager",
    component: () => import("@/views/core/UserManager.vue"),
    breadcrumb: "用户列表",
  },
  {
    biz: "core",
    path: "group-manager",
    name: "group-manager",
    component: () => import("@/views/core/UserGroupManager.vue"),
    breadcrumb: "用户组",
  },
  {
    biz: "core",
    path: "permission-manager",
    name: "permission-manager",
    component: () => import("@/views/core/PermissionManager.vue"),
    breadcrumb: "权限管理",
  },
  {
    biz: "core",
    path: "session-manager",
    name: "session-manager",
    component: () => import("@/views/core/SessionManager.vue"),
    breadcrumb: "会话管理",
  },
  {
    biz: "core",
    path: "config-manager",
    name: "config-manager",
    component: () => import("@/views/core/ConfigManager.vue"),
    breadcrumb: "配置管理",
  },
  {
    biz: "core",
    path: "application-maintain",
    name: "application-maintain",
    component: () => import("@/views/core/ApplicationMaintain.vue"),
    breadcrumb: "维护中心",
  },
  {
    biz: "core",
    path: "menu-manager",
    name: "menu-manager",
    component: () => import("@/views/core/MenuManager.vue"),
    breadcrumb: "菜单管理",
  },
  {
    biz: "core",
    path: "endpoint-manager",
    name: "endpoint-manager",
    component: () => import("@/views/core/EndpointManager.vue"),
    breadcrumb: "端点管理",
  },
  {
    biz: "core",
    path: "company-manager",
    name: "company-manager",
    component: () => import("@/views/core/CompanyManager.vue"),
    breadcrumb: "公司管理",
  },
  {
    biz: "core",
    path: "company-member-manager",
    name: "company-member-manager",
    component: () => import("@/views/core/CompanyMember.vue"),
    breadcrumb: "公司成员管理",
  },
];

export default {
  /**
   * 注册核心模块路由
   */
  useCoreRoute() {
    const { addRoutes } = GenricRouteService.useGenricRoute();
    addRoutes(routes);
  },
};
