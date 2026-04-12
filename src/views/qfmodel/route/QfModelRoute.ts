import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";


export default class QfModelRouteRegister extends GenricRouteRegister {
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "qf",
        path: "qfModel",
        name: "qfModel",
        component: () => import("@/views/qf/qfModel/QfModel.vue"),
        meta: { breadcrumb: "流程模型" },
      }),
    ];
  }
}
