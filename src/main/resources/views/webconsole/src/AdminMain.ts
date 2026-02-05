import { createApp, markRaw } from "vue";
import { createPinia } from "pinia";
import AdminRoot from "./AdminRoot.vue";
import router from "./router/admin"; // 使用admin专用路由
// 导入 Element Plus
import ElementPlus from "element-plus";
import "@/styles/element-theme.scss";
import "@/assets/tailwind.css";
// 导入中文语言包
// @ts-ignore
import zhCn from "element-plus/dist/locale/zh-cn.mjs";

// 导入Element Plus图标并进行全局注册
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// 导入并设置 Iconify
import { setupIconify } from "./commons/Iconify.ts";
import GenricRouteService from "./soa/genric-route/service/GenricRouteService.ts";
import CoreRouteRegister from "./views/core/route/CoreRouteRegister";
import DriveRouteRegister from "./views/drive/route/DriveRouteRegister";
import RelayRouteRegister from "./views/relay/route/RelayRouteRegister";
import RdbgRouteRegister from "@/views/rdbg/route/RdbgRouteRegister";
import DocumentRouteRegister from "@/views/document/route/DocumentRouteRegister";
import AuditRouteRegister from "@/views/audit/route/AuditRouteRegister";

setupIconify();

const app = createApp(AdminRoot);

// 注册所有图标并使用 markRaw 包装
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, markRaw(component));
}

app.use(createPinia());

const { initialize, addRoute } = GenricRouteService.useGenricRoute();

//注册业务路由
addRoute(new CoreRouteRegister());
addRoute(new DriveRouteRegister());
addRoute(new RelayRouteRegister());
addRoute(new RdbgRouteRegister());
addRoute(new DocumentRouteRegister());
addRoute(new AuditRouteRegister());

//初始化路由服务
initialize(app);

// 使用Element Plus并设置为中文
app.use(ElementPlus, {
  locale: zhCn,
});
app.mount("#app");
