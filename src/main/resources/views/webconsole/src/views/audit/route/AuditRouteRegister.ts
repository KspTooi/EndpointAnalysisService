import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class AuditRouteRegister extends GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      {
        biz: "audit",
        path: "audit-login-rcd",
        name: "audit-login-rcd",
        component: () => import("@/views/audit/AuditLoginRcd.vue"),
        meta: { breadcrumb: "登录审计日志" },
      },
      {
        biz: "audit",
        path: "audit-error-rcd",
        name: "audit-error-rcd",
        component: () => import("@/views/audit/AuditErrorRcd.vue"),
        meta: { breadcrumb: "系统错误记录" },
      },
    ];
  }
}
