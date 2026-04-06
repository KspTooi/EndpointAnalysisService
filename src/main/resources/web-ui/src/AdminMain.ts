import { createApp, markRaw } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import AdminRoot from "@/AdminRoot.vue";
// 导入 Element Plus
import ElementPlus from "element-plus";
import "@/styles/element-theme.scss";
import "@/assets/tailwind.css";
// 导入中文语言包
import zhCn from "element-plus/dist/locale/zh-cn.mjs";

// 导入Element Plus图标并进行全局注册
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// 导入并设置 Iconify
import { setupIconify } from "@/commons/Iconify.ts";
import GenricRouteService from "@/soa/genric-route/service/GenricRouteService.ts";
import AuthRouteRegister from "@/views/auth/route/AuthRouteRegister";
import CoreRouteRegister from "@/views/core/route/CoreRouteRegister";
import DriveRouteRegister from "@/views/drive/route/DriveRouteRegister";
import RelayRouteRegister from "@/views/relay/route/RelayRouteRegister";
import DocumentRouteRegister from "@/views/document/route/DocumentRouteRegister";
import AuditRouteRegister from "@/views/audit/route/AuditRouteRegister";
import QtRouteRegister from "@/views/qt/route/QtRouteRegister.ts";
import AssemblyRouteRegister from "@/views/assembly/route/AssemblyRouteRegister.ts";

//初始化Iconify
setupIconify();

//创建应用实例
const app = createApp(AdminRoot);

//注册所有图标并使用 markRaw 包装
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, markRaw(component));
}

//注册Pinia 并加入持久化插件
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);
app.use(pinia);

const { initialize, addRoute } = GenricRouteService.useGenricRoute();

//在SOA路由服务中注册域业务路由
addRoute(new AuthRouteRegister());
addRoute(new CoreRouteRegister());
addRoute(new DriveRouteRegister());
addRoute(new RelayRouteRegister());
addRoute(new DocumentRouteRegister());
addRoute(new AuditRouteRegister());
addRoute(new QtRouteRegister());
addRoute(new AssemblyRouteRegister());

//初始化SOA路由服务
initialize(app);

// 使用Element Plus并设置为中文
app.use(ElementPlus, {
  locale: zhCn,
});

//挂载应用
app.mount("#app");
