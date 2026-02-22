import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

/**
 * QT路由注册
 */
export default class QtRouteRegister extends GenricRouteRegister {
  /**
   * 注册QT任务路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "qt",
        path: "taskGroup",
        name: "QtTaskGroup",
        component: () => import("@/views/qt/QtTaskGroup.vue"),
        meta: { breadcrumb: "任务分组" },
      }),
      RouteEntryPo.build({
        biz: "qt",
        path: "task",
        name: "QtTask",
        component: () => import("@/views/qt/QtTask.vue"),
        meta: { breadcrumb: "任务调度管理" },
      }),
      RouteEntryPo.build({
        biz: "qt",
        path: "taskRcd",
        name: "QtTaskRcd",
        component: () => import("@/views/qt/QtTaskRcd.vue"),
        meta: { breadcrumb: "任务调度日志" },
      }),
    ];
  }
}
