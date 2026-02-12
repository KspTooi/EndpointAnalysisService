import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class AuthRouteRegister extends GenricRouteRegister {
  /**
   * 注册认证相关路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "auth",
        path: "login",
        name: "login",
        component: () => import("@/views/auth/UserLogin.vue"),
        meta: { breadcrumb: "用户登录", layout: "blank" },
      }),
    ];
  }
}
