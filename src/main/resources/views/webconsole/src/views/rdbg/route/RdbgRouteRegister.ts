import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister.ts";

export default class RdbgRouteRegister extends GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      {
        biz: "rdbg",
        path: "user-request-manager",
        name: "user-request-manager",
        component: () => import("@/views/rdbg/UserRequestView.vue"),
        breadcrumb: "端点调试工作台",
      },
      {
        biz: "rdbg",
        path: "user-request-env-manager",
        name: "user-request-env-manager",
        component: () => import("@/views/rdbg/UserRequestEnvManager.vue"),
        breadcrumb: "用户请求环境管理",
      },
      {
        biz: "rdbg",
        path: "simple-filter-manager",
        name: "simple-filter-manager",
        component: () => import("@/views/rdbg/SimpleFilterManager.vue"),
        breadcrumb: "基本过滤器",
      },
      {
        biz: "rdbg",
        path: "workspace",
        name: "rdbg-workspace",
        component: () => import("@/views/rdbg/RdbgWorkSpace.vue"),
        breadcrumb: "请求调试器",
      },
    ];
  }
}
