<template>
  <el-aside width="64px" class="admin-sidebar">
    <div class="admin-side-panel-short">
      <!-- LOGO区域 - 仅图标 -->
      <div class="logo-container">
        <img :src="logoUrl" alt="EAS Logo" class="logo-image" />
      </div>

      <!-- 菜单区域 -->
      <el-menu
        v-loading="loading"
        :default-active="activeMenuId"
        class="panel-menu-short"
        :collapse="true"
        :unique-opened="false"
      >
        <!-- 菜单项 一级菜单 目录类型 -->
        <el-sub-menu
          v-for="item in filterDirectoryMenu(menuTree)"
          :key="item.id"
          :index="item.id"
          :show-timeout="50"
          :hide-timeout="35"
        >
          <template #title>
            <el-icon>
              <component :is="getIconComponent(item.menuIcon)" v-if="item.menuIcon" />
            </el-icon>
            <span class="menu-name">{{ item.name }}</span>
          </template>

          <!-- 二级菜单 目录类型 -->
          <el-sub-menu v-for="child in filterDirectoryMenu(item.children)" :key="child.id" :index="child.id">
            <template #title>
              <el-icon>
                <component :is="getIconComponent(child.menuIcon)" v-if="child.menuIcon" />
              </el-icon>
              <span>{{ child.name }}</span>
            </template>
            <!-- 三级菜单 菜单项类型 -->
            <el-menu-item
              v-for="grandChild in filterItemMenu(child.children)"
              :key="grandChild.id"
              :index="grandChild.id"
              @click="openMenu(grandChild)"
            >
              <el-icon>
                <component :is="getIconComponent(grandChild.menuIcon)" v-if="grandChild.menuIcon" />
              </el-icon>
              <span>{{ grandChild.name }}</span>
            </el-menu-item>
          </el-sub-menu>

          <!-- 二级菜单 菜单项类型 -->
          <el-menu-item
            v-for="child in filterItemMenu(item.children)"
            :key="child.id"
            :index="child.id"
            @click="openMenu(child)"
          >
            <el-icon>
              <component :is="getIconComponent(child.menuIcon)" v-if="child.menuIcon" />
            </el-icon>
            <span>{{ child.name }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 菜单项 一级菜单 菜单项类型 -->
        <el-menu-item v-for="item in filterItemMenu(menuTree)" :key="item.id" :index="item.id" @click="openMenu(item)">
          <el-icon>
            <component :is="getIconComponent(item.menuIcon)" v-if="item.menuIcon" />
          </el-icon>
          <span class="menu-name">{{ item.name }}</span>
          <template #title>{{ item.name }}</template>
        </el-menu-item>
      </el-menu>
    </div>
  </el-aside>
</template>

<script setup lang="ts">
import { markRaw, h, onMounted } from "vue";
import { ElMenu, ElMenuItem, ElSubMenu, ElIcon, ElAside } from "element-plus";
import * as ElementPlusIcons from "@element-plus/icons-vue";
import { Icon } from "@iconify/vue";
import type { Component } from "vue";
import logoUrl from "@/assets/EAS_CROWN.png";
import ComMenuService from "@/soa/com-series/service/ComMenuService.ts";

// 使用菜单服务
const { menuTree, loading, activeMenuId, loadMenus, openMenu, filterDirectoryMenu, filterItemMenu } =
  ComMenuService.useMenuService();

// 初始化菜单
onMounted(() => {
  loadMenus();
});

// 使用 markRaw 包装所有图标组件
const icons = Object.fromEntries(Object.entries(ElementPlusIcons).map(([key, component]) => [key, markRaw(component)]));

// 缓存动态生成的图标组件，避免重复创建导致重渲染
const iconCache = new Map<string, Component>();

// 获取图标组件
const getIconComponent = (iconName: string | Component | undefined): Component | null => {
  if (!iconName) {
    return null;
  }

  if (typeof iconName === "string") {
    if (iconName.includes(":")) {
      if (iconCache.has(iconName)) {
        return iconCache.get(iconName);
      }

      const component = markRaw(() => h(Icon, { icon: iconName, width: 18, height: 18 }));
      iconCache.set(iconName, component);
      return component;
    }

    if (iconName in icons) {
      return icons[iconName];
    }
  }

  return iconName as Component;
};

// 定义事件
const emit = defineEmits<{
  (e: "item-click", itemId: string): void;
  (e: "update:activeItemId", itemId: string): void;
}>();
</script>

<style scoped>
.admin-side-panel-short {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(to bottom, #f8f9fa, #ffffff);
  width: 100%;
  overflow: hidden;
}

.logo-container {
  padding: 12px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-bottom: 1px solid #f0f0f0;
}

.logo-image {
  width: 32px;
  height: auto;
}

.panel-menu-short {
  flex: 1;
  border-right: none !important;
  background: transparent !important;
  overflow-y: auto;
  /* 隐藏滚动条 */
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.panel-menu-short::-webkit-scrollbar {
  display: none;
}

/* 直角风格与样式覆盖 */
:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.panel-menu-short.el-menu--collapse .el-menu-item),
:deep(.panel-menu-short.el-menu--collapse .el-sub-menu__title) {
  height: auto !important;
  min-height: 68px !important;
  line-height: normal !important;
  padding: 10px 0 !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  margin: 0 !important;
  border-radius: 0 !important;
  transition:
    background 0.2s,
    color 0.2s;
}

:deep(.panel-menu-short.el-menu--collapse .el-menu-item .el-icon),
:deep(.panel-menu-short.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0 0 4px 0 !important;
  font-size: 22px !important;
  color: #515a6e;
  transition: color 0.2s;
}

/* 菜单名称样式 - 居中且允许换行 */
:deep(.panel-menu-short.el-menu--collapse .menu-name) {
  width: 100% !important;
  height: auto !important;
  visibility: visible !important;
  display: block !important;
  text-align: center !important;
  font-size: 11px !important;
  line-height: 1.2 !important;
  white-space: normal !important;
  word-break: break-all !important;
  padding: 0 4px !important;
  color: inherit;
  box-sizing: border-box;
}

/* 覆盖 Element Plus 默认隐藏 span 的样式 */
:deep(.panel-menu-short.el-menu--collapse .el-sub-menu__title span) {
  display: block !important;
  height: auto !important;
  width: auto !important;
  visibility: visible !important;
}

/* 激活状态 */
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: #ffffff !important;
}

:deep(.el-menu-item.is-active .menu-name) {
  color: #ffffff !important;
}

:deep(.el-menu-item.is-active .el-icon) {
  color: #ffffff !important;
}

/* 悬停效果 */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(102, 126, 234, 0.1) !important;
}

:deep(.el-menu-item.is-active:hover) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
}

/* 弹出菜单样式（直角） */
:deep(.el-menu--popup) {
  padding: 0 !important;
  border-radius: 0 !important;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15) !important;
}

:deep(.el-menu--popup .el-menu-item) {
  height: 40px !important;
  line-height: 40px !important;
  padding: 0 20px !important;
  justify-content: flex-start !important;
}
</style>
