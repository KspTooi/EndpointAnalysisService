import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";
import type { NavigationGuardWithThis, NavigationHookAfter } from "vue-router";

/**
 * 路由注册器
 */
export default abstract class GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public abstract doRegister(): RouteEntryPo[];

  /**
   * 通过重写该方法来注册前置守卫 如果不需要前置守卫 则返回null
   * @returns 前置守卫
   */
  protected doBeforeEach(): NavigationGuardWithThis<undefined> {
    return null;
  }

  /**
   * 通过重写该方法来注册后置守卫 如果不需要后置守卫 则返回null
   * @returns 后置守卫
   */
  protected doAfterEach(): NavigationHookAfter {
    return null;
  }
}
