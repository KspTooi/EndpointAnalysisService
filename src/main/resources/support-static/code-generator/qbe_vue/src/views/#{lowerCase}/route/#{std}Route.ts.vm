import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class ${model.std}RouteRegister extends GenricRouteRegister {
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "${model.bizDomain}",
        path: "${model.camelCase}",
        name: "${model.camelCase}",
        component: () => import("@/views/${model.bizDomain}/${model.camelCase}/${model.std}.vue"),
        meta: { breadcrumb: "${schema.modelRemark}" },
      }),
    ];
  }
}
