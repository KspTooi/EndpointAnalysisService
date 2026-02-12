import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";

export default class RelayRouteRegister extends GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      {
        biz: "relay",
        path: "request-manager",
        name: "request-manager",
        component: () => import("@/views/relay/RequestManager.vue"),
        meta: { breadcrumb: "中继请求记录" },
      },
      {
        biz: "relay",
        path: "replay-request-manager",
        name: "replay-request-manager",
        component: () => import("@/views/relay/ReplayRequestManager.vue"),
        meta: { breadcrumb: "简单请求重放" },
      },
      {
        biz: "relay",
        path: "relay-server-manager",
        name: "relay-server-manager",
        component: () => import("@/views/relay/RelayServerManager.vue"),
        meta: { breadcrumb: "中继通道配置" },
      },
      {
        biz: "relay",
        path: "route-server",
        name: "route-server",
        component: () => import("@/views/relay/RouteServerManager.vue"),
        meta: { breadcrumb: "路由服务器管理" },
      },
      {
        biz: "relay",
        path: "route-group",
        name: "route-group",
        component: () => import("@/views/relay/RouteRuleManager.vue"),
        meta: { breadcrumb: "路由策略组管理" },
      },
    ];
  }
}
