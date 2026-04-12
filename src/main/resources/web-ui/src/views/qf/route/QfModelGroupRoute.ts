import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";


export default class QfModelGroupRouteRegister extends GenricRouteRegister {
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "qf",
        path: "qfModelGroup",
        name: "qfModelGroup",
        component: () => import("@/views/qf/qfModelGroup/QfModelGroup.vue"),
        meta: { breadcrumb: "流程模型分组" },
      }),
    ];
  }
}
