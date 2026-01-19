import GenricRouteService from "@/soa/genric-route/service/GenricRouteService.ts";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister.ts";

/**
 * 云盘路由注册
 */
export default class DriveRouteRegister extends GenricRouteRegister {
  /**
   * 注册路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      {
        biz: "drive",
        path: "main",
        name: "drive",
        component: () => import("@/views/drive/Drive.vue"),
        breadcrumb: "团队云盘",
      },
      {
        biz: "drive",
        path: "preview-pdf",
        name: "drive-preview-pdf",
        component: () => import("@/views/drive/PdfRenderer.vue"),
        breadcrumb: "云盘-PDF预览",
      },
      {
        biz: "drive",
        path: "preview-photo",
        name: "drive-preview-photo",
        component: () => import("@/views/drive/PhotoRenderer.vue"),
        breadcrumb: "云盘-图片预览",
      },
      {
        biz: "drive",
        path: "preview-video",
        name: "drive-preview-video",
        component: () => import("@/views/drive/VideoRenderer.vue"),
        breadcrumb: "云盘-媒体播放",
      },
      {
        biz: "drive",
        path: "preview-word",
        name: "drive-preview-word",
        component: () => import("@/views/drive/WordRenderer.vue"),
        breadcrumb: "云盘-Word预览",
      },
      {
        biz: "drive",
        path: "preview-excel",
        name: "drive-preview-excel",
        component: () => import("@/views/drive/ExcelRenderer.vue"),
        breadcrumb: "云盘-Excel预览",
      },
      {
        biz: "drive",
        path: "preview-code",
        name: "drive-preview-code",
        component: () => import("@/views/drive/CodeRenderer.vue"),
        breadcrumb: "云盘-代码预览",
      },
    ];
  }
}
