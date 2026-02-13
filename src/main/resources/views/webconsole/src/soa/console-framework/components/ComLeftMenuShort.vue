<template>
  <el-aside width="64px" class="admin-sidebar">
    <div class="admin-side-panel-short">
      <!-- LOGO区域 - 仅图标 -->
      <div class="logo-container">
        <img :src="logoUrl" alt="EAS Logo" class="logo-image" />
      </div>

      <!-- 菜单区域 -->
      <el-menu
        :default-active="activeItemId"
        class="panel-menu-short"
        :collapse="true"
        :unique-opened="false"
        @select="handleSelect"
      >
        <template v-for="item in filteredItems" :key="item.id">
          <!-- 目录类型 -->
          <el-sub-menu
            v-if="item.menuKind === 0 && item.children?.length"
            :index="item.id"
            :show-timeout="50"
            :hide-timeout="35"
          >
            <template #title>
              <el-icon>
                <component :is="getIconComponent(item.menuIcon)" v-if="item.menuIcon" />
              </el-icon>
              <span>{{ item.name }}</span>
            </template>

            <template v-for="child in filterChildren(item.children)" :key="child.id">
              <el-menu-item v-if="child.menuKind === 1" :index="child.id" @click="handleMenuItemClick(child)">
                <el-icon>
                  <component :is="getIconComponent(child.menuIcon)" v-if="child.menuIcon" />
                </el-icon>
                <span>{{ child.name }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>

          <!-- 菜单类型 -->
          <el-menu-item v-else-if="item.menuKind === 1" :index="item.id" @click="handleMenuItemClick(item)">
            <el-icon>
              <component :is="getIconComponent(item.menuIcon)" v-if="item.menuIcon" />
            </el-icon>
            <template #title>{{ item.name }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
  </el-aside>
</template>

<script setup lang="ts">
import { computed, markRaw, h } from "vue";
import { ElMenu, ElMenuItem, ElSubMenu, ElIcon, ElAside } from "element-plus";
import * as ElementPlusIcons from "@element-plus/icons-vue";
import { Icon } from "@iconify/vue";
import type { Component } from "vue";
import type { GetUserMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import { useTabStore } from "@/store/TabHolder";
import logoUrl from "@/assets/EAS_CROWN.png";

// 使用 markRaw 包装所有图标组件
const icons = Object.fromEntries(Object.entries(ElementPlusIcons).map(([key, component]) => [key, markRaw(component)]));

const tabStore = useTabStore();

// 定义组件props
const props = defineProps<{
  items: GetUserMenuTreeVo[];
  activeItemId?: string;
}>();

// 定义事件
const emit = defineEmits<{
  (e: "item-click", itemId: string): void;
  (e: "update:activeItemId", itemId: string): void;
}>();

// 过滤掉按钮类型的菜单
const filteredItems = computed(() => {
  return props.items.filter((item) => item.menuKind !== 2);
});

// 过滤子菜单中的按钮
const filterChildren = (children: GetUserMenuTreeVo[] | undefined) => {
  if (!children) return [];
  return children.filter((child) => child.menuKind !== 2);
};

// 获取图标组件
const getIconComponent = (iconName: string | Component | undefined) => {
  if (!iconName) return null;

  if (typeof iconName === "string") {
    if (iconName.includes(":")) {
      return markRaw(() => h(Icon, { icon: iconName, width: 18, height: 18 }));
    }

    if (iconName in icons) {
      return icons[iconName];
    }
  }

  return iconName;
};

// 处理菜单项点击
const handleMenuItemClick = (item: GetUserMenuTreeVo) => {
  if (item.menuKind !== 1 || !item.menuPath) return;

  tabStore.addTab({
    id: item.id as any,
    title: item.name as any,
    path: item.menuPath,
  });

  emit("item-click", item.id as any);
  emit("update:activeItemId", item.id as any);
};

// 处理菜单选择
const handleSelect = (index: string) => {
  // 保持与原组件逻辑一致，目前仅用于占位
};
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
  transition: transform 0.3s;
}

.logo-image:hover {
  transform: scale(1.1);
}

.panel-menu-short {
  flex: 1;
  border-right: none !important;
  background: transparent !important;
}

/* 直角风格与样式覆盖 */
:deep(.el-menu--collapse) {
  width: 64px;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px !important;
  line-height: 50px !important;
  padding: 0 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  margin: 0 !important;
  border-radius: 0 !important; /* 直角 */
  transition: all 0.2s ease-in-out;
}

:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  margin: 0 !important;
  font-size: 20px;
  color: #515a6e;
}

/* 激活状态 */
:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
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
