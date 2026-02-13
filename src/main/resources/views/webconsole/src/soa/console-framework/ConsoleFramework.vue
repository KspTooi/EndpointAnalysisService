<template>
  <div class="common-layout">
    <el-container>
      <!-- 桌面版侧边栏 -->
      <component
        :is="isMenuCollapse ? SidePanelMenuShort : SidePanelMenu"
        :items="menuTree"
        :active-item-id="activeMenuId"
        title="EAS服务管理控制台"
        version="版本:1.5S CP18"
        @item-click="handleMenuClick"
        @action="handleMenuAction"
      />

      <el-container>
        <!-- 多标签页区域 -->
        <console-tab>
          <template #controls>
            <div class="header-right">
              <!-- 系统导航按钮区域 -->
              <div class="nav-buttons"></div>

              <!-- 用户自定义操作区域 -->
              <slot name="header-actions"></slot>

              <!-- 用户信息和下拉菜单-->
              <el-popover
                placement="bottom-end"
                :width="340"
                trigger="click"
                popper-style="padding: 0; border-radius: 0; overflow: hidden; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1); border: 1px solid #ebeef5;"
              >
                <template #reference>
                  <div class="user-info">
                    <el-avatar
                      :size="24"
                      :src="
                        userProfile?.avatarAttachId
                          ? `/getAttach?id=${userProfile.avatarAttachId}`
                          : '/api/profile/getUserAvatar'
                      "
                      style="margin-right: 8px"
                      shape="square"
                    />
                    <div class="username">
                      {{ userProfile?.nickname || userProfile?.username || "Operator" }}
                    </div>
                  </div>
                </template>
                <profile-drop-menu :profile="userProfile" />
              </el-popover>
            </div>
          </template>
        </console-tab>

        <!-- 头部区域 -->
        <el-header class="admin-header" height="35px">
          <div class="header-left">
            <div class="menu-toggle" @click="toggleMenu">
              <ILineMdMenuUnfoldRight />
            </div>
            <!-- 面包屑导航，放在头部区域 -->
            <el-breadcrumb v-if="autoBreadcrumbs.length" separator="/" class="admin-breadcrumb">
              <el-breadcrumb-item v-for="(item, index) in autoBreadcrumbs" :key="index" :to="item.to">
                {{ item.text }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </el-header>

        <!-- 内容区域 -->
        <el-main class="admin-content">
          <div class="content-wrapper">
            <!-- 路由视图 -->
            <router-view v-slot="{ Component, route }">
              <transition name="fade" mode="out-in">
                <div :key="route.name || route.path">
                  <keep-alive v-if="route.meta.keepAlive">
                    <component :is="Component" :key="route.name || route.path" />
                  </keep-alive>
                  <component :is="Component" v-if="!route.meta.keepAlive" :key="viewKey" />
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
import {
  ElBreadcrumb,
  ElBreadcrumbItem,
  ElContainer,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElHeader,
  ElMain,
  ElMessage,
  ElAvatar,
  ElPopover,
  ElIcon,
} from "element-plus";
import { Expand, Fold } from "@element-plus/icons-vue";
import { useRoute, useRouter } from "vue-router";
import { useTabStore } from "@/store/TabHolder.ts";
import { storeToRefs } from "pinia";
import { computed, onMounted, ref, watch } from "vue";
import MenuApi, { type GetUserMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import AuthApi, { type GetCurrentUserProfile } from "@/soa/console-framework/api/AuthApi.ts";
import GenricHotkeyService from "@/service/GenricHotkeyService.ts";
import { Result } from "@/commons/entity/Result.ts";
import { EventHolder } from "@/store/EventHolder.ts";
import ConsoleTab from "@/soa/console-framework/components/ConsoleTab.vue";
import SidePanelMenu from "@/soa/console-framework/components/SidePanelMenu.vue";
import ProfileDropMenu from "@/soa/console-framework/components/ProfileDropMenu.vue";
import SidePanelMenuShort from "@/soa/console-framework/components/SidePanelMenuShort.vue";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

const router = useRouter();
const route = useRoute();
const tabStore = useTabStore();
const { refreshCounter } = storeToRefs(tabStore);
const viewKey = computed(() => `${route.fullPath}__${refreshCounter.value}`);
const menuTree = ref<GetUserMenuTreeVo[]>([]);
const userProfile = ref<GetCurrentUserProfile | null>(null);

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

const hotKeyActive = ref(true);
const { activeOf } = useTabStore();

// 菜单折叠状态
const isMenuCollapse = ref(localStorage.getItem("isMenuCollapse") !== "false");

const toggleMenu = () => {
  isMenuCollapse.value = !isMenuCollapse.value;
  localStorage.setItem("isMenuCollapse", isMenuCollapse.value.toString());
};

//快捷键服务打包
GenricHotkeyService.useHotkeyFunction(
  {
    ctrl_1: () => {
      activeOf(1);
    },
    ctrl_2: () => {
      activeOf(2);
    },
    ctrl_3: () => {
      activeOf(3);
    },
    ctrl_4: () => {
      activeOf(4);
    },
    ctrl_5: () => {
      activeOf(5);
    },
    ctrl_6: () => {
      activeOf(6);
    },
    ctrl_7: () => {
      activeOf(7);
    },
    ctrl_8: () => {
      activeOf(8);
    },
    ctrl_9: () => {
      activeOf(9);
    },
  },
  hotKeyActive
);

const loadMenuTree = async () => {
  try {
    const result = await MenuApi.getUserMenuTree();

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

const loadUserProfile = async () => {
  try {
    userProfile.value = await AuthApi.getCurrentUserProfile();
  } catch (error: any) {
    console.error("加载用户信息失败:", error);
  }
};

onMounted(() => {
  loadUserProfile();
});

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
const findMenuIdByPath = (items: GetUserMenuTreeVo[], path: string): any => {
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

// 自动生成面包屑导航
const autoBreadcrumbs = computed(() => {
  // 如果 props 中提供了 breadcrumbs，则优先使用
  if (props.breadcrumbs?.length) return props.breadcrumbs;

  const revisedBreadcrumbs: Array<{ text: string; to?: string | object }> = [];

  // 尝试添加首页/根路径面板屑
  let homeTitle = "控制台";
  const homeRouteConfig = router.options.routes.find((r) => r.path === "/");
  const homeBreadcrumbMeta = homeRouteConfig?.meta?.breadcrumb as any;

  if (homeBreadcrumbMeta) {
    if (typeof homeBreadcrumbMeta === "string") {
      homeTitle = homeBreadcrumbMeta;
    }
    if (typeof homeBreadcrumbMeta === "object" && homeBreadcrumbMeta.title) {
      homeTitle = homeBreadcrumbMeta.title;
    }
  }

  // 如果当前不是根路径，则添加控制台首页
  if (route.path !== "/") {
    revisedBreadcrumbs.push({
      text: homeTitle,
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

    // 处理 meta.breadcrumb (支持字符串和对象)
    const breadcrumbMeta = meta?.breadcrumb as any;

    if (typeof breadcrumbMeta === "string") {
      title = breadcrumbMeta;
    }

    if (breadcrumbMeta && typeof breadcrumbMeta === "object") {
      if (breadcrumbMeta.title) {
        title = breadcrumbMeta.title;
      }
      hidden = breadcrumbMeta.hidden === true;
    }

    // 如果 breadcrumb 中没有 title，尝试使用 meta.title
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
  /* box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); */
  height: 35px;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  height: 100%;
}

.menu-toggle {
  font-size: 18px;
  cursor: pointer;
  color: var(--el-text-color-secondary);
  margin-right: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
  height: 100%;
}

.menu-toggle:hover {
  color: #764ba2;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-right: 15px;
}

.nav-buttons {
  display: flex;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 0;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f1f3f4;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.admin-breadcrumb {
  font-size: 13px;
  line-height: 1;
  margin-left: 15px;
  user-select: none;
  cursor: default !important;
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
  border-radius: 0; /* 修改为直角 */
  /* box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05); */
  box-shadow: none; /* 移除内容区域投影 */
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
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
