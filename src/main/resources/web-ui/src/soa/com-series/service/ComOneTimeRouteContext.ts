import GenricRouteService from "@/soa/genric-route/service/GenricRouteService";
import ComMenuService from "@/soa/com-series/service/ComMenuService.ts";
import { ElMessage } from "element-plus";

const ONE_TIME_CONTEXT_PREFIX = "one-time-context";

export default {
  /**
   * 使用一次性路由上下文
   *
   * 一次性路由上下文用于跳转到一个已知的菜单项，并携带一个一次性上下文
   *
   */
  useOneTimeRouteContext() {
    //获取GRS服务
    const { getRouteByNameOrPath } = GenricRouteService.useGenricRoute();

    //获取菜单服务
    const { getMenuByPath, openMenu } = ComMenuService.useMenuService();

    /**
     * 跳转到一个已知的菜单项，并携带一个一次性上下文
     * @param path 菜单项路径
     * @param query 一次性上下文
     */
    const redirect = (path: string, query?: any): void => {
      //通过路径在GRC中查找匹配的路由
      const grsRoute = getRouteByNameOrPath(path);
      if (!grsRoute) {
        console.error(`无法通过路径 ${path} 找到对应的路由!`);
        ElMessage.error(`无法通过路径 ${path} 找到对应的路由!`);
        return;
      }

      //通过路由获取菜单项
      const menuItem = getMenuByPath(grsRoute.buildPath());
      if (!menuItem) {
        console.error(`无法通过路径 ${grsRoute.buildPath()} 找到对应的菜单项!`);
        ElMessage.error(`无法通过路径 ${grsRoute.buildPath()} 找到对应的菜单项!`);
        return;
      }

      //参数序列化到localStorage
      if (query !== null && query !== undefined) {
        localStorage.setItem(ONE_TIME_CONTEXT_PREFIX, JSON.stringify(query));
      }

      //直接打开菜单
      openMenu(menuItem);
    };

    /**
     * 获取一次性上下文
     * @returns 一次性上下文 如果一次性上下文不存在，则返回null
     */
    const getQuery = (): any => {
      //先获取localStorage中的一次性上下文
      const query = localStorage.getItem(ONE_TIME_CONTEXT_PREFIX);

      if (!query) {
        return null;
      }

      try {
        //读后删
        localStorage.removeItem(ONE_TIME_CONTEXT_PREFIX);
        return JSON.parse(query);
      } catch (error) {
        console.error("反序列化一次性上下文失败:", error);
        ElMessage.error("反序列化一次性上下文失败:" + error);
        return null;
      }
    };

    return {
      //跳转到一个已知的菜单项，并携带一个一次性上下文
      redirect,

      //获取一次性上下文
      getQuery,
    };
  },
};
