import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";

/**
 * 路由注册器
 */
export default abstract class GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public abstract doRegister(): RouteEntryPo[];
}
