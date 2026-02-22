import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class DocumentRouteRegister extends GenricRouteRegister {
  /**
   * 注册文档路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "document",
        path: "ep-std-word-manager",
        name: "ep-std-word-manager",
        component: () => import("@/views/document/EpStdWordManager.vue"),
        meta: { breadcrumb: "标准词管理" },
      }),
      RouteEntryPo.build({
        biz: "document",
        path: "ep-site-manager",
        name: "ep-site-manager",
        component: () => import("@/views/document/EpSiteManager.vue"),
        meta: { breadcrumb: "站点管理" },
      }),
    ];
  }
}
