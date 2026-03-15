import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter, type RouteRecordNameGeneric } from "vue-router";
import { useTabStore } from "@/store/TabHolder";
import { ElMessage } from "element-plus";

/** CDRC上下文前缀 */
const CDRC_CONTEXT_PREFIX = "cdrc-context";

/** CDRC回退上下文前缀 */
const CDRC_RETURN_CONTEXT_PREFIX = "cdrc-return";

/** CDRC目标页上下文TTL(毫秒) */
const CDRC_CONTEXT_TTL_MS = 30 * 60 * 1000;

/** CDRC回源页上下文TTL(毫秒) */
const CDRC_RETURN_CONTEXT_TTL_MS = 10 * 60 * 1000;

/**
 * CDRC上下文对象
 */
export interface CDRCContext {
  return: any; //从目标路由返回源路由时需要携带的参数
  send: any; //从源路由跳转到目标路由时需要发送的参数
}

/**
 * CDRC缓存对象
 * 用于给上下文增加过期时间，避免sessionStorage长期堆积
 */
interface CDRCCache {
  context: CDRCContext; //实际存储的CDRC上下文
  expireAt: number; //过期时间戳，超过该时间后自动失效
}

/**
 * 获取一个唯一的CDRC跳转ID
 */
function nextRedirectId() {
  return "cdrc-redirect-" + Date.now() + "-" + Math.random().toString(36).substring(2, 15);
}

/**
 * 获取CDRC上下文TTL
 * @param prefix CDRC上下文前缀
 * @returns TTL毫秒数
 */
function getCdrcContextTtl(prefix: string) {
  //回源上下文是一次性的，TTL应更短
  if (prefix === CDRC_RETURN_CONTEXT_PREFIX) {
    return CDRC_RETURN_CONTEXT_TTL_MS;
  }

  //目标页上下文需要支持刷新恢复，TTL可适当更长
  return CDRC_CONTEXT_TTL_MS;
}

/**
 * 解析CDRC上下文
 * @param prefix CDRC上下文前缀
 * @param cdrcContextId CDRC上下文ID
 * @returns CDRC上下文对象
 */
function resolveCdrcContext(prefix: string, cdrcContextId: string): CDRCContext | null {
  if (!cdrcContextId) {
    return null;
  }

  const cdrcContextStr = sessionStorage.getItem(prefix + "_" + cdrcContextId);

  //如果CDRC上下文存在，则解析为CDRC上下文对象
  if (cdrcContextStr) {
    try {
      const cdrcCache = JSON.parse(cdrcContextStr) as CDRCCache | CDRCContext;
      let cdrcContext: CDRCContext = null;

      //优先按新结构解析：{ context, expireAt }
      if ("expireAt" in cdrcCache && "context" in cdrcCache) {
        //上下文已过期则直接清理
        if (cdrcCache.expireAt <= Date.now()) {
          removeCdrcContext(prefix, cdrcContextId);
          return null;
        }

        cdrcContext = cdrcCache.context;
      }

      //兼容旧结构：直接存储CDRCContext
      if (!cdrcContext && ("send" in cdrcCache || "return" in cdrcCache)) {
        cdrcContext = cdrcCache as CDRCContext;
      }

      //结构不符合预期时直接清理，避免脏数据残留
      if (!cdrcContext) {
        removeCdrcContext(prefix, cdrcContextId);
        return null;
      }

      //如果CDRC上下文里面send 和 return都为空，则认为CDRC上下文已经失效，删除SessionStorage中的CDRC上下文
      if (!cdrcContext.send && !cdrcContext.return) {
        removeCdrcContext(prefix, cdrcContextId);
        return null;
      }

      return cdrcContext;
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : String(error);
      console.error("解析CDRC上下文失败:", error);
      ElMessage.error("解析CDRC上下文失败:" + errorMessage);

      //删除SessionStorage中的CDRC上下文
      removeCdrcContext(prefix, cdrcContextId);
      return null;
    }
  }

  //如果CDRC上下文不存在，则返回null
  return null;
}

/**
 * 删除CDRC上下文
 * @param prefix CDRC上下文前缀
 * @param cdrcRedirectId CDRC跳转ID或回退ID
 */
function removeCdrcContext(prefix: string, cdrcRedirectId: string) {
  if (!cdrcRedirectId) {
    return;
  }

  sessionStorage.removeItem(prefix + "_" + cdrcRedirectId);
}

/**
 * 放入CDRC上下文
 * @param prefix CDRC上下文前缀
 * @param cdrcRedirectId CDRC跳转ID或回退ID
 * @param cdrcContext CDRC上下文对象
 */
function putCdrcContext(prefix: string, cdrcRedirectId: string, cdrcContext: CDRCContext) {
  //如果上下文为空 不进行存储
  if (!cdrcContext) {
    return;
  }

  if (!cdrcRedirectId) {
    return;
  }

  //如果上下文中send、return为null 不进行存储
  if (cdrcContext.send == null && cdrcContext.return == null) {
    return;
  }

  const cdrcCache: CDRCCache = {
    context: cdrcContext,
    //不同前缀使用不同TTL，兼顾刷新恢复与自动清理
    expireAt: Date.now() + getCdrcContextTtl(prefix),
  };

  //放入CDRC上下文
  sessionStorage.setItem(prefix + "_" + cdrcRedirectId, JSON.stringify(cdrcCache));
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
    let cdrcContext: CDRCContext = null;
    let cdrcQuery = null;
    let cdrcReturnQuery = null;
    const cdrcSource = route.query["cdrc-source"] as string;
    const cdrcSourceName = route.query["cdrc-source-name"] as string;
    const cdrcRedirectId = route.query["cdrc-redirect-id"] as string;
    const cdrcReturnId = route.query["cdrc-return-id"] as string;

    //是否可以回退
    const cdrcCanReturn = cdrcSource != null;

    //回退名称 如果为空则返回默认值
    const cdrcReturnName = "返回" + (cdrcSourceName ?? "");

    //解析CDRC上下文
    cdrcContext = resolveCdrcContext(CDRC_CONTEXT_PREFIX, cdrcRedirectId);

    //如果CDRC上下文存在，则解析send和return参数
    if (cdrcContext) {
      cdrcQuery = cdrcContext.send;
      cdrcReturnQuery = cdrcContext.return;
    }

    //如果cdrc上下文不存在，则解析回退上下文
    if (!cdrcContext) {
      cdrcContext = resolveCdrcContext(CDRC_RETURN_CONTEXT_PREFIX, cdrcReturnId);
      if (cdrcContext) {
        //删除CDRC回退上下文(CDRC回源上下文是一次性的,从目标回源需要读后删)
        removeCdrcContext(CDRC_RETURN_CONTEXT_PREFIX, cdrcReturnId);

        cdrcReturnQuery = cdrcContext.return;
      }
    }

    /**
     * 跳转
     * @param nameOrPath 路由的名称或路径
     * @param sendQuery 需发送的查询参数
     * @param returnQuery 从目标返回时会携带的查询参数
     */
    const cdrcRedirect = (nameOrPath: string, sendQuery?: any, returnQuery?: any) => {
      //查找Vue路由里面是否有匹配的名称
      const hasFindName = router.getRoutes().find((route) => route.name === nameOrPath);
      const hasFindPath = router.getRoutes().find((route) => route.path === nameOrPath);

      if (!hasFindName && !hasFindPath) {
        console.error(`CDRC跳转失败，无法通过名称或路径 ${nameOrPath} 找到对应的路由!`);
        ElMessage.error(`CDRC跳转失败，无法通过名称或路径 ${nameOrPath} 找到对应的路由!`);
        return;
      }

      //查询当前标签的name
      const currentActiveTabId = tabStore.activeTabId;
      const currentTab = tabStore.tabs.find((tab) => tab.id === currentActiveTabId);

      if (!currentTab) {
        console.error("无法使用CDRC跳转，当前激活标签的name不存在!");
        ElMessage.error("无法使用CDRC跳转，无法找到当前激活的标签!");
        return;
      }

      //获取一个唯一的CDRC跳转ID
      const redirectId = nextRedirectId();

      //构建CDRC上下文
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

      //放入CDRC上下文
      putCdrcContext(CDRC_CONTEXT_PREFIX, redirectId, cdrcContext);

      //优先通过名称跳转
      if (hasFindName) {
        router.push({
          name: nameOrPath,
          query: {
            "cdrc-source": currentTab.path,
            "cdrc-source-name": currentTab.title,
            "cdrc-redirect-id": redirectId,
          },
        });
      }
      //如果名称不存在，则通过路径跳转
      if (hasFindPath) {
        router.push({
          path: nameOrPath,
          query: {
            "cdrc-source": currentTab.path,
            "cdrc-source-name": currentTab.title,
            "cdrc-redirect-id": redirectId,
          },
        });
      }
    };

    /**
     * 使用CDRC回退
     */
    const cdrcReturn = () => {
      if (!cdrcCanReturn) {
        console.error("无法使用CDRC回退，当前路由没有配置回退!");
        return;
      }

      //如果现在有CDRC上下文，删除它 并将它转为回退上下文
      if (cdrcContext) {
        //删除CDRC上下文
        removeCdrcContext(CDRC_CONTEXT_PREFIX, cdrcRedirectId);

        //放入CDRC回退上下文
        putCdrcContext(CDRC_RETURN_CONTEXT_PREFIX, cdrcRedirectId, cdrcContext);
      }

      //跳转到源路由并携带返回参数
      router.push({
        path: route.query["cdrc-source"] as string,
        query: {
          "cdrc-return-id": cdrcRedirectId,
        },
      });
    };

    /**
     * 获取CDRC查询参数
     */
    const getCdrcQuery = () => {
      //如果CDRC上下文不存在，则自动回退
      if (!cdrcQuery) {
        cdrcReturn();
        ElMessage.error("CDRC上下文获取失败,自动回源到来源路由!");
        return;
      }

      return cdrcQuery;
    };

    /**
     * 获取CDRC返回查询参数
     */
    const getCdrcReturnQuery = () => {
      return cdrcReturnQuery;
    };

    return {
      //CDRC来源路由 这里记录了从哪个路由跳转过来的
      cdrcSource,

      //CDRC发送的查询参数 通常为来源路由需要传递给目标路由的参数
      getCdrcQuery,

      //CDRC返回的查询参数 由来源路由指定，通过CDRC回退时会自动携带这些参数(可以用来保存当时的表单数据等，避免跳转后再跳回时丢失数据)
      getCdrcReturnQuery,

      //是否可以回退
      cdrcCanReturn,

      //回退名称 如果为空则返回默认值
      cdrcReturnName,

      /**
       * CDRC回退函数
       * 用于回退到来源路由
       * 通常在目标路由的按钮上使用，点击后会自动回退到来源路由
       */
      cdrcReturn,

      /**
       * CDRC跳转函数
       * @param nameOrPath 路由的名称或路径
       * @param sendQuery 需发送的查询参数
       * @param returnQuery 从目标返回时会携带的查询参数
       */
      cdrcRedirect,
    };
  },
};
