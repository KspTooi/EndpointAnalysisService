import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter, type RouteRecordNameGeneric } from "vue-router";
import { useTabStore } from "@/store/TabHolder";

/**
 * CDRC回退数据
 * 用于记录当前是否是CDRC回退
 */
let isCdrcReturn: boolean = false;

/**
 * CDRC跳转数据
 * 用于记录当前是否是CDRC跳转
 */
let isCdrcRedirect: boolean = false;

/**
 * CDRC上下文对象
 */
export interface CDRCContext {
  return: any; //从目标路由返回源路由时需要携带的参数
  send: any; //从源路由跳转到目标路由时需要发送的参数
}

export default {
  /**
   * 使用直接路由上下文
   */
  useDirectRouteContext() {
    const route = useRoute();
    const router = useRouter();
    const tabStore = useTabStore();

    //当前CDRC上下文
    const context = ref<CDRCContext | null>(null);
    const contextQuery = ref<any>(null);
    const contextReturnQuery = ref<any>(null);

    /**
     * CDRC来源
     * 用于记录当前CDRC来源
     */
    const cdrcSource = ref<string | null>(null);

    //初始化CDRC上下文
    const contextStr = sessionStorage.getItem("cdrc-context");

    //如果CDRC上下文存在，则解析为CDRC上下文对象
    if (contextStr) {
      context.value = JSON.parse(contextStr);
    }

    //解析send和return参数
    const sendQuery = context.value?.send;
    const returnQuery = context.value?.return;

    if (isCdrcRedirect && sendQuery) {
      contextQuery.value = sendQuery;
      isCdrcRedirect = false;
    }
    if (isCdrcReturn && returnQuery) {
      contextReturnQuery.value = returnQuery;
      isCdrcReturn = false;
    }

    //解析CDRC来源
    if (route.query["cdrc-source"]) {
      cdrcSource.value = route.query["cdrc-source"] as string;
    }

    //是否可以回退
    const hasReturn = computed(() => {
      //路由里面有参数drc-source 即可回退
      if (route.query["cdrc-source"]) {
        return true;
      }

      return false;
    });

    //回退名称 如果为空则返回默认值
    const returnName = computed(() => {
      if (route.query["cdrc-source-name"]) {
        return "返回" + route.query["cdrc-source-name"];
      }
      return "返回";
    });

    /**
     * 使用Vue路由名称跳转
     * @param name 名称
     * @param sendQuery 需发送的查询参数
     * @param returnQuery 从目标返回时会携带的查询参数
     */
    const redirectByName = (name: string, sendQuery?: any, returnQuery?: any) => {
      //当前激活标签的id
      const currentActiveTabId = tabStore.activeTabId;

      if (!currentActiveTabId) {
        console.error("无法使用CDRC跳转，当前激活标签的id不存在!");
        return;
      }

      //查询当前标签的name
      const currentTab = tabStore.tabs.find((tab) => tab.id === currentActiveTabId);

      if (!currentTab) {
        console.error("无法使用CDRC跳转，当前激活标签的name不存在!");
        return;
      }

      //组装CDRC上下文对象
      const cdrcContext: CDRCContext = {
        return: null,
        send: null,
      };

      if (sendQuery) {
        cdrcContext.send = sendQuery;
      }
      if (returnQuery) {
        cdrcContext.return = returnQuery;
      }

      if (cdrcContext.return != null && cdrcContext.send != null) {
        //将CDRC上下文对象放置到SessionStorage中
        sessionStorage.setItem("cdrc-context", JSON.stringify(cdrcContext));
      }
      //设置CDRC跳转标识
      isCdrcRedirect = true;

      router.push({
        name,
        query: {
          "cdrc-source": currentTab.path,
          "cdrc-source-name": currentTab.title,
        },
      });
    };

    /**
     * 使用Vue路由路径跳转
     * @param path 路径
     * @param sendQuery 需发送的查询参数
     * @param returnQuery 从目标返回时会携带的查询参数
     */
    const redirectByPath = (path: string, sendQuery?: any, returnQuery?: any) => {
      //当前激活标签的id
      const currentActiveTabId = tabStore.activeTabId;

      if (!currentActiveTabId) {
        console.error("无法使用CDRC跳转，当前激活标签的id不存在!");
        return;
      }

      //查询当前标签的name
      const currentTab = tabStore.tabs.find((tab) => tab.id === currentActiveTabId);

      if (!currentTab) {
        console.error("无法使用CDRC跳转，当前激活标签的name不存在!");
        return;
      }

      //组装CDRC上下文对象
      const cdrcContext: CDRCContext = {
        return: null,
        send: null,
      };

      if (sendQuery) {
        cdrcContext.send = sendQuery;
      }
      if (returnQuery) {
        cdrcContext.return = returnQuery;
      }

      if (cdrcContext.return != null && cdrcContext.send != null) {
        //将CDRC上下文对象放置到SessionStorage中
        sessionStorage.setItem("cdrc-context", JSON.stringify(cdrcContext));
      }

      //设置CDRC跳转标识
      isCdrcRedirect = true;

      router.push({
        path,
        query: {
          "cdrc-source": currentTab.path,
          "cdrc-source-name": currentTab.title,
        },
      });
    };

    /**
     * 使用CDRC回退
     */
    const redirectReturn = () => {
      if (!hasReturn.value) {
        console.error("无法使用CDRC回退，当前路由没有配置回退!");
        return;
      }

      //设置CDRC回退标识
      isCdrcReturn = true;

      //跳转到源路由并携带返回参数
      router.push({
        path: route.query["cdrc-source"] as string,
      });
    };

    return {
      cdrcSource,
      context,
      contextQuery,
      contextReturnQuery,
      hasReturn,
      returnName,
      redirectReturn,
      redirectByName,
      redirectByPath,
    };
  },
};
