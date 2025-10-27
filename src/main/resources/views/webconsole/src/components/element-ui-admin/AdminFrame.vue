<template>
  <div class="common-layout">
    <el-container>
      <!-- 桌面版侧边栏 -->
      <el-aside width="210px" class="admin-sidebar">
        <admin-side-panel
          :items="menuTree"
          :active-item-id="activeMenuId"
          :is-collapse="false"
          title="EAS服务管理控制台"
          version="版本:1.2A-DevPreview 技术预览"
          @item-click="handleMenuClick"
          @action="handleMenuAction"
        />
      </el-aside>

      <el-container>
        <!-- 多标签页区域 -->
        <tab-panel />

        <!-- 头部区域 -->
        <el-header class="admin-header" height="30px">
          <div class="header-left">
            <!-- 面包屑导航，放在头部区域 -->
            <el-breadcrumb v-if="autoBreadcrumbs.length" separator="/" class="admin-breadcrumb">
              <el-breadcrumb-item v-for="(item, index) in autoBreadcrumbs" :key="index" :to="item.to">
                {{ item.text }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <!-- 系统导航按钮区域 -->
            <div class="nav-buttons"></div>

            <!-- 用户自定义操作区域 -->
            <slot name="header-actions"></slot>

            <!-- 用户信息和下拉菜单-->
            <el-dropdown trigger="click">
              <div class="user-info" style="display: flex; align-items: center; height: 100%">
                <div class="color-block" style="width: 15px; height: 15px; background-color: #409eff; border-radius: 50%; margin-right: 5px"></div>
                <div style="line-height: 1">Operator Options</div>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>No options</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="admin-content">
          <div class="content-wrapper">
            <!-- 路由视图 -->
            <router-view v-slot="{ Component, route }">
              <transition name="fade" mode="out-in">
                <div :key="viewKey">
                  <keep-alive v-if="route.meta.keepAlive">
                    <component :is="Component" />
                  </keep-alive>
                  <component :is="Component" v-if="!route.meta.keepAlive" />
                </div>
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import AdminSidePanel from "./AdminSidePanel.vue";
import TabPanel from "./TabPanel.vue";
import {
  ElContainer,
  ElHeader,
  ElAside,
  ElMain,
  ElBreadcrumb,
  ElBreadcrumbItem,
  ElIcon,
  ElDropdown,
  ElDropdownMenu,
  ElDropdownItem,
  ElAvatar,
  ElLoading,
  ElButton,
  ElMessage,
} from "element-plus";
import type { GetMenuTreeVo } from "@/api/core/MenuApi";
import { Result } from "@/commons/entity/Result";
import MenuApi from "@/api/core/MenuApi";
import { EventHolder } from "@/store/EventHolder";
import { useTabStore } from "@/store/TabHolder";
import { storeToRefs } from "pinia";

// 定义组件props
const props = defineProps<{
  title?: string;
  logo?: string;
  user?: {
    name: string;
    avatar?: string;
    [key: string]: any;
  };
  defaultActiveMenuId?: string;
  breadcrumbs?: Array<{
    text: string;
    to?: string | object;
  }>;
}>();

// 定义事件
const emit = defineEmits<{
  (e: "menu-click", menuId: string): void;
  (e: "menu-action", action: string, menuId: string): void;
  (e: "logout"): void;
}>();

const router = useRouter();
const route = useRoute();
const tabStore = useTabStore();
const { refreshCounter } = storeToRefs(tabStore);
const viewKey = computed(() => `${route.fullPath}__${refreshCounter.value}`);
const menuTree = ref<GetMenuTreeVo[]>([]);

const loadMenuTree = async () => {
  try {
    const result = await MenuApi.getMenuTree({});

    if (!Result.isSuccess(result)) {
      ElMessage.error(result.message);
      return;
    }

    menuTree.value = result.data;
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

loadMenuTree();

// 导航到指定URL
const navigateToUrl = (url: string) => {
  window.location.href = url;
};

// 菜单项点击事件
const handleMenuItemClick = (menuId: string) => {
  emit("menu-click", menuId);
};

// 无移动端兼容逻辑

// 根据路由路径计算当前活动菜单ID
const findMenuIdByPath = (items: GetMenuTreeVo[], path: string): any=> {
  for (const item of items) {
    if (item.menuPath === path) {
      return item.id;
    }
    if (item.children?.length) {
      const foundId = findMenuIdByPath(item.children, path);
      if (foundId) return foundId;
    }
  }
  return "";
};

// 当前活动菜单
const activeMenuId = computed(() => {
  if (props.defaultActiveMenuId) return props.defaultActiveMenuId;
  return findMenuIdByPath(menuTree.value, route.path);
});

// 处理菜单点击
const handleMenuClick = (menuId: string) => {
  handleMenuItemClick(menuId);
};

// 处理菜单动作
const handleMenuAction = (action: string, menuId: string) => {
  emit("menu-action", action, menuId);
};

// 处理退出登录
const handleLogout = () => {
  emit("logout");
};

// 自动生成面包屑导航
const autoBreadcrumbs = computed(() => {
  // 如果 props 中提供了 breadcrumbs，则优先使用
  if (props.breadcrumbs?.length) return props.breadcrumbs;

  const revisedBreadcrumbs: Array<{ text: string; to?: string | object }> = [];

  // 尝试添加首页/根路径面包屑
  const homeRouteConfig = router.options.routes.find((r) => r.path === "/");
  // Use optional chaining and type checking for safer access
  const homeBreadcrumbMeta = homeRouteConfig?.meta?.breadcrumb as { title?: string; hidden?: boolean } | undefined;
  if (homeBreadcrumbMeta?.title && route.path !== "/") {
    revisedBreadcrumbs.push({
      text: homeBreadcrumbMeta.title,
      to: "/",
    });
  }

  // 遍历匹配的路由记录
  route.matched.forEach((record, index) => {
    // 如果已经添加了首页，并且当前记录是根路径，则跳过
    if (record.path === "/" && revisedBreadcrumbs.length > 0 && revisedBreadcrumbs[0].to === "/") return;

    const meta = record.meta;
    let title = "";
    let hidden = false;
    const path = record.path; // 使用匹配路由的路径

    // 检查 meta.breadcrumb 配置
    // Use type assertion for breadcrumb meta structure
    const breadcrumbMeta = meta?.breadcrumb as { title?: string; hidden?: boolean } | undefined;
    if (breadcrumbMeta) {
      if (breadcrumbMeta.title) {
        title = breadcrumbMeta.title;
      }
      hidden = breadcrumbMeta.hidden === true;
    }

    // 如果 breadcrumb 中没有 title，尝试使用 meta.title
    // Check if meta.title exists and is a string
    if (!title && meta?.title && typeof meta.title === "string") {
      title = meta.title;
    }

    // 如果有标题且不隐藏，则添加到面包屑数组中
    if (title && !hidden) {
      // 检查是否已存在（基于路径）
      const alreadyExists = revisedBreadcrumbs.some((b) => b.to === path);
      if (!alreadyExists) {
        revisedBreadcrumbs.push({
          text: title,
          // 最后一个面包屑（当前页面）不设置链接
          to: index === route.matched.length - 1 ? undefined : path,
        });
      }
    }
  });

  return revisedBreadcrumbs;
});

//监听左侧菜单重新加载事件
watch(
  () => EventHolder().isNeedReloadLeftMenu,
  (newVal) => {
    if (newVal) {
      loadMenuTree();
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.common-layout {
  height: 100%;
  width: 100%;
  overflow: hidden;
  display: flex;
  min-height: 0;
}

.el-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  min-height: 0;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  height: 50px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  margin-right: 20px;
}

.logo-image {
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

.menu-toggle {
  font-size: 20px;
  cursor: pointer;
  color: var(--el-text-color-secondary);
  margin-right: 15px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-buttons {
  display: flex;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.admin-sidebar {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-right: none;
  height: 100%;
  overflow: hidden;
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.08);
  position: relative;
  z-index: 10;
}

.admin-breadcrumb {
  font-size: 13px;
  line-height: 1;
  margin-left: 15px;
}

.admin-content {
  background-color: var(--el-bg-color-page);
  padding: 0;
  height: 100%;
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  width: 100%;
}

.content-wrapper {
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* 路由容器占满剩余高度，允许内部滚动 */
.content-wrapper > div {
  flex: 1;
  display: flex;
  min-height: 0;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
