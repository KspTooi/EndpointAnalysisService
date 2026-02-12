import type { Component } from "vue";

/**
 * 路由条目
 */
export interface RouteEntryPo {
  biz: string; //领域或业务代码 为null时直接使用path
  path: string; //路由路径
  name: string; //路由名称 为null时直接使用path
  component: () => Promise<Component>; //组件路径
  meta: {
    keepAlive?: boolean; //是否缓存
    breadcrumb?: string | null; //面包屑名称 为null时直接使用path
    layout?: string | null; //布局 为null时直接使用默认布局 blank:全局布局(不带控制台框架)
  };
}
