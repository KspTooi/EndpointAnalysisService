import { createRouter, createWebHashHistory, useRoute, useRouter, type Router } from "vue-router";
import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";
import { type App, ref } from "vue";
import { useTabStore } from "@/store/TabHolder";
import RouteNotFound from "@/soa/route-not-found/RouteNotFound.vue";
import NoPermission from "@/soa/no-permission/NoPermission.vue";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

//是否已初始化
let hasInitialized = false;

//路由表
const routes = ref<RouteEntryPo[]>([]);

//路由注册器
const routeRegistries = ref<GenricRouteRegister[]>([]);

/**
 * Vue路由
 */
const vueRouter = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/:pathMatch(.*)*",
      name: "NotFound",
      component: RouteNotFound,
      meta: {
        layout: "blank",
      },
    },
    {
      path: "/no-permission",
      name: "no-permission",
      component: NoPermission,
      meta: {
        layout: "blank",
      },
    },
  ],
});

// 路由守卫
vueRouter.beforeEach((to, from, next) => {
  // 仅在访问根路径时尝试恢复标签页，其他路径直接放行
  if (to.path !== "/") {
    return next();
  }

  const tabStore = useTabStore();
  const activeTab = tabStore.tabs.find((t) => t.id === tabStore.activeTabId);

  // 优先恢复当前激活标签，但排除根路径和登录页，避免自跳转/无意义跳转
  if (activeTab && activeTab.path !== "/" && activeTab.path !== "/auth/login" && activeTab.path !== to.path) {
    return next(activeTab.path);
  }

  // 激活标签不可用时，回退到最近访问的业务标签（同样排除根路径和登录页）
  const fallbackTab = [...tabStore.tabs].reverse().find((t) => t.path !== "/" && t.path !== "/auth/login");

  // 防止重定向到当前目标，避免产生循环跳转
  if (fallbackTab && fallbackTab.path !== to.path) {
    return next(fallbackTab.path);
  }

  // 无可恢复标签时停留在根路径
  return next();
});

export default {
  /**
   * 获取Vue路由
   * @returns Vue路由
   */
  getVueRouter(): Router {
    return vueRouter;
  },

  /**
   * 使用全局路由服务
   */
  useGenricRoute() {
    /**
     * 初始化路由服务
     * @param app 应用实例
     */
    const initialize = (app: App) => {
      if (hasInitialized) {
        return;
      }
      hasInitialized = true;

      //注册路由注册器
      routeRegistries.value.forEach((register: GenricRouteRegister) => {
        //注册路由
        addRoutes(register.doRegister());

        //注册前置守卫
        if (register.doBeforeEach()) {
          vueRouter.beforeEach(register.doBeforeEach());
        }

        //注册后置守卫
        if (register.doAfterEach()) {
          console.log("注册后置守卫", register.doAfterEach());
          vueRouter.afterEach(register.doAfterEach);
        }
      });

      app.use(vueRouter);
    };

    const addRoute = (entry: RouteEntryPo | GenricRouteRegister) => {
      //如果是路由注册器 则注册到路由注册器列表
      if (entry instanceof GenricRouteRegister) {
        routeRegistries.value.push(entry);
        return;
      }

      //校验路由条目
      entry.validate();

      let hasConflict = false;

      for (const route of routes.value) {
        //查找同名路由
        if (route.name === entry.name) {
          //删除Vue路由
          vueRouter.removeRoute(route.name);

          //更新路由条目
          route.biz = entry.biz;
          route.path = entry.path;
          route.name = entry.name;
          route.component = entry.component;
          route.meta = entry.meta;
          hasConflict = true;
          break;
        }
      }

      const breadcrumbTitle = entry.meta.breadcrumb;

      //如果无冲突 同时更新路由表+Vue路由
      if (!hasConflict) {
        //更新路由表
        routes.value.push(entry);

        //添加Vue路由
        vueRouter.addRoute({
          path: entry.buildPath(),
          name: entry.name,
          component: entry.component,
          meta: {
            keepAlive: entry.meta.keepAlive,
            breadcrumb: breadcrumbTitle,
            layout: entry.meta.layout,
          },
        });
      }

      //路由有名称冲突 只更新Vue路由,不更新内置路由表
      if (hasConflict) {
        vueRouter.addRoute({
          path: entry.buildPath(),
          name: entry.name,
          component: entry.component,
          meta: {
            keepAlive: entry.meta.keepAlive,
            breadcrumb: breadcrumbTitle,
            layout: entry.meta.layout,
          },
        });
      }
    };

    /**
     * 批量注册路由
     * @param entries 路由条目数组
     */
    const addRoutes = (entries: RouteEntryPo[]) => {
      entries.forEach((entry) => addRoute(entry));
    };

    /**
     * 删除路由
     * @param name 路由名称
     */
    const removeRoute = (name: string) => {
      vueRouter.removeRoute(name);
      routes.value = routes.value.filter((route) => route.name !== name);
    };

    /**
     * 获取路由表
     * @returns 路由表的副本 对副本操作不会改变原始路由表
     */
    const getRoutes = (): RouteEntryPo[] => {
      const result: RouteEntryPo[] = [];

      //遍历路由表
      for (const item of routes.value) {
        const po = new RouteEntryPo();
        po.biz = item.biz;
        po.path = item.path;
        po.name = item.name;
        po.component = item.component;
        po.meta = item.meta;
        result.push(po);
      }

      return result;
    };

    return {
      initialize,
      addRoute,
      addRoutes,
      removeRoute,
      getRoutes,
    };
  },
};
