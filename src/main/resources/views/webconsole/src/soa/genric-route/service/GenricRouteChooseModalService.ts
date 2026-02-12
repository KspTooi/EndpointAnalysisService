import { computed, ref } from "vue";
import GenricRouteService from "@/soa/genric-route/service/GenricRouteService";
import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";

export default {
  useGenricRouteChooseModal() {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const selectedRoute = ref<RouteEntryPo | null>(null);
    const routeList = ref<RouteEntryPo[]>([]);
    const searchKeyword = ref("");

    /**
     * 过滤后的路由列表
     */
    const filteredRouteList = computed(() => {
      if (!searchKeyword.value) {
        return routeList.value;
      }

      const keyword = searchKeyword.value.toLowerCase();
      return routeList.value.filter((route) => {
        const fullPath = route.buildPath().toLowerCase();
        const name = (route.name || "").toLowerCase();
        const breadcrumb = (route.meta?.breadcrumb || "").toLowerCase();
        return fullPath.includes(keyword) || name.includes(keyword) || breadcrumb.includes(keyword);
      });
    });

    /**
     * 打开模态框
     * @returns Promise<string | null> 返回选中的路由路径，取消返回null
     */
    const openModal = (): Promise<string | null> => {
      return new Promise((resolve) => {
        const genricRouteService = GenricRouteService.useGenricRoute();
        const routes = genricRouteService.getRoutes();

        for (const item of routes) {
          const route = new RouteEntryPo();
          route.biz = item.biz;
          route.path = item.path;
          route.name = item.name;
          route.component = item.component;
          route.meta = item.meta;
          routeList.value.push(route);
        }

        modalVisible.value = true;

        const checkInterval = setInterval(() => {
          if (!modalVisible.value) {
            clearInterval(checkInterval);
            if (selectedRoute.value) {
              resolve(selectedRoute.value.buildPath());
              return;
            }
            resolve(null);
          }
        }, 100);
      });
    };

    /**
     * 确认选择
     */
    const confirmSelect = () => {
      if (!selectedRoute.value) {
        return;
      }
      modalVisible.value = false;
    };

    /**
     * 取消选择
     */
    const cancelSelect = () => {
      selectedRoute.value = null;
      modalVisible.value = false;
    };

    return {
      modalVisible,
      modalLoading,
      selectedRoute,
      routeList,
      searchKeyword,
      filteredRouteList,
      openModal,
      confirmSelect,
      cancelSelect,
    };
  },
};
