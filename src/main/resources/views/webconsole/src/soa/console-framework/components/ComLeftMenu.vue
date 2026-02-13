<template>
  <el-aside width="210px" class="admin-sidebar">
    <div class="admin-side-panel">
      <!-- LOGO与标题区域 -->
      <div class="title-container">
        <div v-if="title" class="panel-title">
          <img :src="logoUrl" alt="EAS Logo" class="logo-image" />
          {{ title }}
        </div>
        <div v-if="version" class="panel-version">
          {{ version }}
        </div>
      </div>

      <!-- 菜单区域 -->
      <el-menu
        :default-active="activeItemId"
        :default-openeds="openedMenus"
        class="panel-menu"
        @select="handleSelect"
        @open="handleMenuOpen"
        @close="handleMenuClose"
        :collapse="isCollapse"
        :unique-opened="false"
      >
        <!-- Fallback for Maintenance Center -->
        <el-menu-item v-if="!hasMaintainCenter" index="fallback-maintenance-center" @click="goToMaintainCenter">
          <el-icon>
            <component :is="getIconComponent('Setting')" />
          </el-icon>
          <span>维护中心(备用)</span>
        </el-menu-item>

        <template v-for="item in filteredItems" :key="item.id">
          <!-- 目录类型 -->
          <el-sub-menu v-show="item.menuKind === 0 && item.children?.length" :index="item.id as any">
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
          <el-menu-item v-show="item.menuKind === 1" :index="item.id" @click="handleMenuItemClick(item)">
            <el-icon>
              <component :is="getIconComponent(item.menuIcon)" v-if="item.menuIcon" />
            </el-icon>
            <span>{{ item.name }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>
  </el-aside>
</template>

<script setup lang="ts">
import { ref, inject, computed, markRaw, h } from "vue";
import { ElMenu, ElMenuItem, ElSubMenu, ElIcon, ElBadge, ElAside } from "element-plus";
import { useRouter } from "vue-router";
import type { Component } from "vue";
import * as ElementPlusIcons from "@element-plus/icons-vue";
import { Icon } from "@iconify/vue";
import type { GetUserMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import { useTabStore } from "@/store/TabHolder";
import logoUrl from "@/assets/EAS_CROWN.png";

const router = useRouter();
const tabStore = useTabStore();

// 使用 markRaw 包装所有图标组件
const icons = Object.fromEntries(Object.entries(ElementPlusIcons).map(([key, component]) => [key, markRaw(component)]));

// 定义组件props
const props = defineProps<{
  items: GetUserMenuTreeVo[];
  activeItemId?: string;
  title?: string;
  isCollapse?: boolean;
  version?: string;
}>();

const hasMaintainCenter = computed(() => {
  const searchPath = "/core/application-maintain";
  const findItemByPath = (items: GetUserMenuTreeVo[]): boolean => {
    for (const item of items) {
      if (item.menuPath === searchPath) {
        return true;
      }
      if (item.children && item.children.length > 0) {
        if (findItemByPath(item.children)) {
          return true;
        }
      }
    }
    return false;
  };
  return findItemByPath(props.items);
});

const goToMaintainCenter = () => {
  router.push("/core/application-maintain");
};

const STORAGE_KEY = "admin_menu_opened_state";

// 初始化菜单展开状态
const getInitialOpenedMenus = (): string[] => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (error) {
    console.error("Failed to load menu state:", error);
  }
  return [];
};

// 菜单展开状态
const openedMenus = ref<string[]>(getInitialOpenedMenus());

// 保存展开状态到 localStorage
const saveOpenedMenus = () => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(openedMenus.value));
  } catch (error) {
    console.error("Failed to save menu state:", error);
  }
};

// 处理菜单打开
const handleMenuOpen = (index: string) => {
  if (!openedMenus.value.includes(index)) {
    openedMenus.value.push(index);
    saveOpenedMenus();
  }
};

// 处理菜单关闭
const handleMenuClose = (index: string) => {
  const idx = openedMenus.value.indexOf(index);
  if (idx > -1) {
    openedMenus.value.splice(idx, 1);
    saveOpenedMenus();
  }
};

// 过滤掉按钮类型的菜单
const filteredItems = computed(() => {
  return props.items.filter((item) => item.menuKind !== 2);
});

// 过滤子菜单中的按钮
const filterChildren = (children: GetUserMenuTreeVo[] | undefined) => {
  if (!children) return [];
  return children.filter((child) => child.menuKind !== 2);
};

// 获取图标组件，确保使用 markRaw 版本
const getIconComponent = (iconName: string | Component | undefined) => {
  if (!iconName) return null;

  if (typeof iconName === "string") {
    if (iconName.includes(":")) {
      return markRaw(() => h(Icon, { icon: iconName, width: 16, height: 16 }));
    }

    if (iconName in icons) {
      return icons[iconName];
    }
  }

  return iconName;
};

// 定义事件
const emit = defineEmits<{
  (e: "item-click", itemId: string): void;
  (e: "action", action: string, itemId: string): void;
  (e: "update:activeItemId", itemId: string): void;
}>();

// 处理菜单项点击
const handleMenuItemClick = (item: GetUserMenuTreeVo) => {
  if (item.menuKind !== 1) {
    return;
  }

  if (!item.menuPath) {
    return;
  }

  // Use the tab store to add a new tab
  tabStore.addTab({
    id: item.id as any,
    title: item.name as any,
    path: item.menuPath,
  });

  // Emit events for parent components if needed, but navigation is handled by the tab store
  emit("item-click", item.id as any);
  emit("update:activeItemId", item.id as any);
};

// 处理菜单选择
const handleSelect = (index: string) => {
  const findItem = (items: GetUserMenuTreeVo[], index: string): GetUserMenuTreeVo | null => {
    for (const item of items) {
      if (item.id === index) {
        return item;
      }
      if (item.children?.length) {
        const found = findItem(item.children, index);
        if (found) return found;
      }
    }
    return null;
  };

  const selectedItem = findItem(props.items, index);
  if (!selectedItem) {
    return;
  }

  if (selectedItem.menuKind === 0) {
    return;
  }
};
</script>

<style scoped>
.admin-side-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: linear-gradient(to bottom, #f8f9fa, #ffffff);
}

.title-container {
  padding: 8px 8px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-bottom: 1px solid #f0f0f0;
}

.logo-image:hover {
  transform: scale(1.05);
}

.logo-image {
  max-width: 30px;
  height: auto;
}

.panel-title {
  padding: 0;
  font-size: 14px;
  font-weight: 600;
  text-align: center;
  color: #430675;
  flex-shrink: 0;
  letter-spacing: 0.5px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.panel-version {
  margin-top: 8px;
  font-size: 12px;
  font-weight: 500;
  color: #868e96;
  background-color: #f1f3f5;
  border: 1px solid #dee2e6;
  padding: 2px 8px;
  border-radius: 12px;
  display: inline-block;
  line-height: 1.5;
}

.panel-menu {
  flex: 1;
  overflow-y: auto;
  border-right: none;
  background: transparent;
}

.panel-menu:not(.el-menu--collapse) {
  width: 220px;
}

.item-badge {
  margin-left: 8px;
}

/* 滚动条样式 */
.panel-menu::-webkit-scrollbar {
  width: 5px;
}

.panel-menu::-webkit-scrollbar-track {
  background: transparent;
}

.panel-menu::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, #c0c4cc 0%, #909399 100%);
  border-radius: 3px;
}

.panel-menu::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, #909399 0%, #606266 100%);
}

/* 菜单项样式 */
.panel-menu :deep(.el-menu) {
  border-right: none;
  background: transparent;
}

.panel-menu :deep(.el-menu-item),
.panel-menu :deep(.el-sub-menu__title) {
  margin: 3px 0;
  height: 42px;
  line-height: 42px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.panel-menu :deep(.el-menu-item span),
.panel-menu :deep(.el-sub-menu__title span) {
  user-select: none; /* 防止文本被选中 */
}

/* 菜单项激活状态 */
.panel-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  font-weight: 500;
}

.panel-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #ffffff;
}

/* 移除旧的左侧边框 */
.panel-menu :deep(.el-menu-item.is-active)::before {
  display: none;
}

/* 菜单项悬停效果 */
.panel-menu :deep(.el-menu-item:hover),
.panel-menu :deep(.el-sub-menu__title:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
}

/* 激活状态下的菜单项悬停效果 */
.panel-menu :deep(.el-menu-item.is-active:hover) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); /* 保持激活状态的背景色 */
}

/* 子菜单样式 */
.panel-menu :deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
  height: 38px;
  line-height: 38px;
  padding-left: 48px !important;
  margin: 2px 0;
}

.panel-menu :deep(.el-sub-menu .el-menu-item.is-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 3px 8px rgba(102, 126, 234, 0.3);
  font-weight: 500;
}

/* 图标样式 */
.panel-menu :deep(.el-menu-item .el-icon),
.panel-menu :deep(.el-sub-menu__title .el-icon) {
  margin-right: 10px;
  font-size: 17px;
  transition: all 0.3s;
}

.panel-menu :deep(.el-menu-item.is-active .el-icon) {
  transform: scale(1.1);
}

/* 折叠状态 */
.panel-menu :deep(.el-menu--collapse .el-menu-item),
.panel-menu :deep(.el-menu--collapse .el-sub-menu__title) {
  height: 42px;
  line-height: 42px;
}

.panel-menu :deep(.el-menu--collapse .el-menu-item .el-icon),
.panel-menu :deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin: 0;
}

/* 展开箭头样式 */
.panel-menu :deep(.el-sub-menu__icon-arrow) {
  transition: transform 0.3s;
}

.panel-menu :deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: rotateZ(180deg);
}
</style>
