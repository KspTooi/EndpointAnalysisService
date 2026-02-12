import { createRouter, createWebHashHistory, useRoute, useRouter } from "vue-router";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";
import { type App, ref } from "vue";
import { useTabStore } from "@/store/TabHolder";
import RouteNotFound from "@/soa/route-not-found/RouteNotFound.vue";
import GenricRouteRegister from "./GenricRouteRegister";

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
    },
  ],
});

// 路由守卫
vueRouter.beforeEach((to, from, next) => {
  // 当访问根路径 '/' 时进行检测
  if (to.path === "/") {
    const tabStore = useTabStore();

    //如果有激活的标签页
    if (tabStore.activeTabId) {
      // 在标签列表中查找该标签的具体信息
      const activeTab = tabStore.tabs.find((t) => t.id === tabStore.activeTabId);

      // 如果找到了激活的标签且其路径不是当前根路径，则跳转
      if (activeTab && activeTab.path !== "/") {
        return next(activeTab.path);
      }
    }
  }

  // 其他情况或没有激活标签时，继续正常访问
  next();
});

function buildPath(entry: RouteEntryPo): string {
  if (entry.biz == null) {
    return "/" + entry.path;
  }

  return "/" + entry.biz + "/" + entry.path;
}

export default {
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
      routeRegistries.value.forEach((register) => {
        //注册路由
        addRoutes(register.doRegister());
      });

      app.use(vueRouter);
    };

    const addRoute = (entry: RouteEntryPo | GenricRouteRegister) => {
      //如果是路由注册器 则注册到路由注册器列表
      if (entry instanceof GenricRouteRegister) {
        routeRegistries.value.push(entry);
        return;
      }

      //如果是路由条目 处理路由条目
      if (entry.path == null || entry.component == null) {
        throw new Error("path和component不能为空");
      }

      //path不允许包含/
      if (entry.path.includes("/")) {
        throw new Error("path不允许包含/");
      }

      //Name为空时使用path
      if (entry.name == null) {
        entry.name = entry.path;
      }

      //meta.breadcrumb为空时使用path
      if (entry.meta == null) {
        entry.meta = {};
      }
      if (entry.meta.breadcrumb == null) {
        entry.meta.breadcrumb = entry.path;
      }

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

      const breadcrumbTitle = entry.meta.breadcrumb ?? entry.path;

      //如果无冲突 同时更新路由表+Vue路由
      if (!hasConflict) {
        //更新路由表
        routes.value.push(entry);

        //添加Vue路由
        vueRouter.addRoute({
          path: buildPath(entry),
          name: entry.name,
          component: entry.component,
          meta: {
            keepAlive: entry.meta.keepAlive,
            breadcrumb: breadcrumbTitle,
            layout: entry.meta.layout,
          },
        });
      }

      //有冲突 只更新Vue路由
      if (hasConflict) {
        vueRouter.addRoute({
          path: buildPath(entry),
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

    const removeRoute = (name: string) => {
      vueRouter.removeRoute(name);
      routes.value = routes.value.filter((route) => route.name !== name);
    };

    /**
     * 获取路由表
     * @returns 路由表的副本 对副本操作不会改变原始路由表
     */
    const getRoutes = (): RouteEntryPo[] => {
      return routes.value.map((route) => ({
        biz: route.biz,
        path: route.path,
        name: route.name,
        component: route.component,
        meta: {
          keepAlive: route.meta.keepAlive,
          breadcrumb: route.meta.breadcrumb,
          layout: route.meta.layout,
        },
      }));
    };

    return {
      initialize,
      addRoute,
      addRoutes,
      removeRoute,
      getRoutes,
      buildPath,
    };
  },
};
