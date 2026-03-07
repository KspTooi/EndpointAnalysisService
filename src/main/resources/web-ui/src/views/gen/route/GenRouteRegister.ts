import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

/**
 * 代码生成路由注册
 */
export default class GenRouteRegister extends GenricRouteRegister {
  /**
   * 注册代码生成路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "gen",
        path: "data-source-manager",
        name: "data-source-manager",
        component: () => import("@/views/gen/DataSource.vue"),
        meta: { breadcrumb: "数据源管理" },
      }),
      RouteEntryPo.build({
        biz: "gen",
        path: "ty-schema-manager",
        name: "ty-schema-manager",
        component: () => import("@/views/gen/TymSchema.vue"),
        meta: { breadcrumb: "类型映射方案管理" },
      }),
    ];
  }
}
