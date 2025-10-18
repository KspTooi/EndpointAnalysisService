<template>
  <div class="admin-side-panel">
    <!-- 标题区域 -->
    <div v-if="title" class="panel-title">
      {{ title }}
    </div>

    <!-- 菜单区域 -->
    <el-menu :default-active="activeItemId" class="panel-menu" @select="handleSelect" :collapse="isCollapse" :unique-opened="true">
      <template v-for="item in filteredItems" :key="item.id">
        <!-- 目录类型 -->
        <el-sub-menu v-if="item.menuKind === 0 && item.children?.length" :index="item.id">
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
          <span>{{ item.name }}</span>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { ref, inject, computed, markRaw, h } from "vue";
import { ElMenu, ElMenuItem, ElSubMenu, ElIcon, ElBadge } from "element-plus";
import { useRouter } from "vue-router";
import type { Component } from "vue";
import * as ElementPlusIcons from "@element-plus/icons-vue";
import { Icon } from "@iconify/vue";
import type { GetUserMenuTreeVo } from "@/api/core/MenuApi";

const router = useRouter();

// 使用 markRaw 包装所有图标组件
const icons = Object.fromEntries(Object.entries(ElementPlusIcons).map(([key, component]) => [key, markRaw(component)]));

// 定义组件props
const props = defineProps<{
  items: GetUserMenuTreeVo[];
  activeItemId?: string;
  title?: string;
  isCollapse?: boolean;
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

  router.push(item.menuPath);
  emit("item-click", item.id);
  emit("update:activeItemId", item.id);
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
  border-right: 1px solid var(--el-border-color-light);
  overflow: hidden;
}

.panel-title {
  padding: 12px;
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  color: var(--el-text-color-primary);
  border-bottom: 1px solid var(--el-border-color-light);
  flex-shrink: 0;
}

.panel-menu {
  flex: 1;
  overflow-y: auto;
  border-right: none;
}

.panel-menu:not(.el-menu--collapse) {
  width: 220px;
}

.item-badge {
  margin-left: 8px;
}

/* 滚动条样式 */
.panel-menu::-webkit-scrollbar {
  width: 4px;
}

.panel-menu::-webkit-scrollbar-track {
  background: transparent;
}

.panel-menu::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 2px;
}

.panel-menu::-webkit-scrollbar-thumb:hover {
  background: var(--el-border-color-darker);
}
</style>
