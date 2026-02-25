import { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo.ts";
import GenricRouteRegister from "@/soa/genric-route/service/GenricRouteRegister";

/**
 * 云盘路由注册
 */
export default class DriveRouteRegister extends GenricRouteRegister {
  /**
   * 注册云盘路由
   * @returns 路由条目数组
   */
  public doRegister(): RouteEntryPo[] {
    return [
      RouteEntryPo.build({
        biz: "drive",
        path: "main",
        name: "drive",
        component: () => import("@/views/drive/Drive.vue"),
        meta: { breadcrumb: "团队云盘" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-pdf",
        name: "drive-preview-pdf",
        component: () => import("@/views/drive/PdfRenderer.vue"),
        meta: { breadcrumb: "云盘-PDF预览" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-photo",
        name: "drive-preview-photo",
        component: () => import("@/views/drive/PhotoRenderer.vue"),
        meta: { breadcrumb: "云盘-图片预览" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-video",
        name: "drive-preview-video",
        component: () => import("@/views/drive/VideoRenderer.vue"),
        meta: { breadcrumb: "云盘-媒体播放" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-word",
        name: "drive-preview-word",
        component: () => import("@/views/drive/WordRenderer.vue"),
        meta: { breadcrumb: "云盘-Word预览" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-excel",
        name: "drive-preview-excel",
        component: () => import("@/views/drive/ExcelRenderer.vue"),
        meta: { breadcrumb: "云盘-Excel预览" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "preview-code",
        name: "drive-preview-code",
        component: () => import("@/views/drive/CodeRenderer.vue"),
        meta: { breadcrumb: "云盘-代码预览" },
      }),
      RouteEntryPo.build({
        biz: "drive",
        path: "drive-space",
        name: "drive-space",
        component: () => import("@/views/drive/DriveSpace.vue"),
        meta: { breadcrumb: "云盘空间" },
      }),
    ];
  }
}
