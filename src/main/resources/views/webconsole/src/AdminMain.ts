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
import CoreRouteService from "./views/core/service/CoreRouteService";

setupIconify();

const app = createApp(AdminRoot);

// 注册所有图标并使用 markRaw 包装
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, markRaw(component));
}

app.use(createPinia());

//注册业务路由
CoreRouteService.useCoreRoute();

//初始化路由服务
const { initialize } = GenricRouteService.useGenricRoute();
initialize(app);

// 使用Element Plus并设置为中文
app.use(ElementPlus, {
  locale: zhCn,
});
app.mount("#app");
