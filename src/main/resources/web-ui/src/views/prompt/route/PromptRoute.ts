import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

export default class PromptRouteRegister extends GenricRouteRegister {
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "ep",
        path: "prompt",
        name: "prompt",
        component: () => import("@/views/ep/prompt/Prompt.vue"),
        meta: { breadcrumb: "提示词" },
      }),
    ];
  }
}
