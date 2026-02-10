import GenricRouteService from "@/soa/genric-route/service/GenricRouteService.ts";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister.ts";

/**
 * QT路由注册
 */
export default class QtRouteRegister extends GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      {
        biz: "qt",
        path: "taskGroup",
        name: "QtTaskGroup",
        component: () => import("@/views/qt/QtTaskGroup.vue"),
        breadcrumb: "任务分组",
      },
      {
        biz: "qt",
        path: "task",
        name: "QtTask",
        component: () => import("@/views/qt/QtTask.vue"),
        breadcrumb: "任务调度管理",
      },
    ];
  }
}
