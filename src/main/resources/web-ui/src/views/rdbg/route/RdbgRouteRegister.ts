import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class RdbgRouteRegister extends GenricRouteRegister {
  /**
   * 注册调试路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "rdbg",
        path: "user-request-manager",
        name: "user-request-manager",
        component: () => import("@/views/rdbg/UserRequestView.vue"),
        meta: { breadcrumb: "端点调试工作台" },
      }),
      RouteEntryPo.build({
        biz: "rdbg",
        path: "user-request-env-manager",
        name: "user-request-env-manager",
        component: () => import("@/views/rdbg/UserRequestEnvManager.vue"),
        meta: { breadcrumb: "用户请求环境管理" },
      }),
      RouteEntryPo.build({
        biz: "rdbg",
        path: "simple-filter-manager",
        name: "simple-filter-manager",
        component: () => import("@/views/rdbg/SimpleFilterManager.vue"),
        meta: { breadcrumb: "基本过滤器" },
      }),
      RouteEntryPo.build({
        biz: "rdbg",
        path: "workspace",
        name: "rdbg-workspace",
        component: () => import("@/views/rdbg/RdbgWorkSpace.vue"),
        meta: { breadcrumb: "请求调试器" },
      }),
    ];
  }
}
