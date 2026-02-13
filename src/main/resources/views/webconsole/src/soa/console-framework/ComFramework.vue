<template>
  <div class="flex h-full w-full overflow-hidden min-h-0">
    <el-container class="h-full w-full overflow-hidden min-h-0">
      <!-- 桌面版侧边栏 -->
      <component
        :is="isMenuCollapse ? ComLeftMenuShort : ComLeftMenu"
        :items="menuTree"
        :active-item-id="activeMenuId"
        title="EAS服务管理控制台"
        version="版本:1.5S CP18"
      />

      <el-container class="h-full w-full overflow-hidden min-h-0">
        <!-- 多标签页区域 -->
        <com-multi-tab>
          <template #controls>
            <div class="flex h-full w-full items-center mr-[15px]">
              <!-- 用户通知下拉菜单 -->
              <core-user-notice-drop-menu />
              <!-- 用户信息和下拉菜单-->
              <com-user-profile />
            </div>
          </template>
        </com-multi-tab>

        <!-- 头部区域 -->
        <el-header class="admin-header" height="35px">
          <div class="flex items-center h-full">
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
import { ElBreadcrumb, ElBreadcrumbItem, ElContainer, ElHeader, ElMain, ElMessage, ElIcon } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { useTabStore } from "@/store/TabHolder.ts";
import { storeToRefs } from "pinia";
import { computed, ref, watch } from "vue";
import ComFrameworkService from "@/soa/console-framework/service/ComFrameworkService.ts";
import ComMultiTab from "@/soa/console-framework/components/ComMultiTab.vue";
import ComLeftMenu from "@/soa/console-framework/components/ComLeftMenu.vue";
import ComUserProfile from "@/soa/console-framework/components/ComUserProfile.vue";
import ComLeftMenuShort from "@/soa/console-framework/components/ComLeftMenuShort.vue";

import CoreUserNoticeDropMenu from "@/views/core/components/public/CoreUserNoticeDropMenu.vue";

const router = useRouter();
const route = useRoute();
const tabStore = useTabStore();
const { refreshCounter } = storeToRefs(tabStore);
const viewKey = computed(() => `${route.fullPath}__${refreshCounter.value}`);

ComFrameworkService.useComTabHotkey();
const { isMenuCollapse, toggleMenu, autoBreadcrumbs, menuTree, activeMenuId } = ComFrameworkService.useComFramework();
</script>

<style scoped>
.admin-header {
  background-color: #fff;
  border-bottom: 1px solid var(--el-border-color-light);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 35px;
  flex-shrink: 0;
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
  border-radius: 0;
  box-shadow: none;
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
