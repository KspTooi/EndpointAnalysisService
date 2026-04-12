import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";


export default class QfModelDeployRcdRouteRegister extends GenricRouteRegister {
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "qf",
        path: "qfModelDeployRcd",
        name: "qfModelDeployRcd",
        component: () => import("@/views/qf/qfModelDeployRcd/QfModelDeployRcd.vue"),
        meta: { breadcrumb: "流程模型部署历史" },
      }),
    ];
  }
}
