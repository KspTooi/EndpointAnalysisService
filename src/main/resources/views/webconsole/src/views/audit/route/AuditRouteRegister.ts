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
        breadcrumb: "登录审计日志",
      },
    ];
  }
}
