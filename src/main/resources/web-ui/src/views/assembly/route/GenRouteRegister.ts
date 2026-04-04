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
        biz: "assembly",
        path: "data-source-manager",
        name: "data-source-manager",
        component: () => import("@/views/assembly/DataSource.vue"),
        meta: { breadcrumb: "数据源管理" },
      }),
      RouteEntryPo.build({
        biz: "assembly",
        path: "ty-schema-manager",
        name: "ty-schema-manager",
        component: () => import("@/views/assembly/TymSchema.vue"),
        meta: { breadcrumb: "类型映射方案管理" },
      }),
      // 类型映射方案字段管理（通常由 TymSchema 通过 CDRC 跳转进入）
      RouteEntryPo.build({
        biz: "assembly",
        path: "tym-schema-field-manager",
        name: "tym-schema-field-manager",
        component: () => import("@/views/assembly/TymSchemaField.vue"),
        meta: { breadcrumb: "类型映射方案字段管理" },
      }),
      RouteEntryPo.build({
        biz: "assembly",
        path: "scm-manager",
        name: "scm-manager",
        component: () => import("@/views/assembly/Scm.vue"),
        meta: { breadcrumb: "SCM管理" },
      }),
      RouteEntryPo.build({
        biz: "assembly",
        path: "out-schema-manager",
        name: "out-schema-manager",
        component: () => import("@/views/assembly/OutSchema.vue"),
        meta: { breadcrumb: "输出方案管理" },
      }),
      RouteEntryPo.build({
        biz: "assembly",
        path: "out-model-origin-manager",
        name: "out-model-origin-manager",
        component: () => import("@/views/assembly/OutModelOrigin.vue"),
        meta: { breadcrumb: "输出方案原始模型管理" },
      }),
      RouteEntryPo.build({
        biz: "assembly",
        path: "out-model-poly-manager",
        name: "out-model-poly-manager",
        component: () => import("@/views/assembly/OutModelPoly.vue"),
        meta: { breadcrumb: "输出方案聚合模型管理" },
      }),
    ];
  }
}
