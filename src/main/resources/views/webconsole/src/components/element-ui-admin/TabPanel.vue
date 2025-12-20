<template>
  <div class="tab-panel-container">
    <draggable v-model="tabs" item-key="id" class="draggable-tabs" animation="200" ghost-class="ghost-tab" @end="onDragEnd" ref="draggableRef">
      <template #item="{ element: tab }">
        <div class="tab-item" :class="{ active: activeTabId === tab.id }" @click="handleTabClick(tab.id)" @contextmenu.prevent="openContextMenu($event, tab)">
          <span class="tab-title">{{ tab.title }}</span>
          <el-icon v-if="tab.closable !== false && tabs.length > 1" class="close-icon" @click.stop="handleTabClose(tab.id)">
            <Close />
          </el-icon>
        </div>
      </template>
    </draggable>

    <div class="tab-controls">
      <el-icon class="control-btn" @click="handleRefresh" title="刷新">
        <Refresh />
      </el-icon>
      <el-icon class="control-btn close-btn" @click="handleCloseCurrentTab" title="关闭标签页">
        <Close />
      </el-icon>
      <slot name="controls"></slot>
    </div>

    <ul v-if="contextMenu.visible" class="context-menu" :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }">
      <li @click="closeCurrentTab">关闭当前标签页</li>
      <li @click="closeOtherTabs">关闭其他标签页</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { storeToRefs } from "pinia";
import draggable from "vuedraggable";
import { ElIcon } from "element-plus";
import { Refresh, Close } from "@element-plus/icons-vue";
import { useTabStore, type Tab } from "@/store/TabHolder";

const draggableRef = ref<any>(null);
const tabStore = useTabStore();
const { activeTabId, tabs } = storeToRefs(tabStore);

// Use a computed property with a setter for v-model with Pinia state
const computedTabs = computed({
  get: () => tabs.value,
  set: (value) => tabStore.setTabs(value),
});

const handleTabClick = (tabId: string) => {
  tabStore.setActiveTab(tabId);
};

const handleTabClose = (tabId: string) => {
  if (tabs.value.length > 1) {
    tabStore.removeTab(tabId);
  }
};

const handleRefresh = () => {
  tabStore.refreshActiveView();
};

const handleCloseCurrentTab = () => {
  if (activeTabId.value && tabs.value.length > 1) {
    tabStore.removeTab(activeTabId.value);
  }
};

const onDragEnd = () => {
  // The setter on the `computedTabs` computed property handles the update
};

// Context Menu Logic
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  targetTab: null as Tab | null,
});

const openContextMenu = (event: MouseEvent, tab: Tab) => {
  contextMenu.value.visible = true;
  contextMenu.value.x = event.clientX;
  contextMenu.value.y = event.clientY;
  contextMenu.value.targetTab = tab;
};

const closeContextMenu = () => {
  contextMenu.value.visible = false;
  contextMenu.value.targetTab = null;
};

const closeCurrentTab = () => {
  if (contextMenu.value.targetTab) {
    handleTabClose(contextMenu.value.targetTab.id);
  }
  closeContextMenu();
};

const closeOtherTabs = () => {
  if (contextMenu.value.targetTab) {
    tabStore.closeOtherTabs(contextMenu.value.targetTab.id);
  }
  closeContextMenu();
};

const handleWheel = (event: WheelEvent) => {
  const el = draggableRef.value?.$el as HTMLElement | undefined;
  if (el) {
    event.preventDefault();
    el.scrollLeft += event.deltaY;
  }
};

onMounted(() => {
  document.addEventListener("click", closeContextMenu);
  const el = draggableRef.value?.$el;
  if (el) {
    el.addEventListener("wheel", handleWheel, { passive: false });
  }
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeContextMenu);
  const el = draggableRef.value?.$el;
  if (el) {
    el.removeEventListener("wheel", handleWheel);
  }
});
</script>

<style scoped>
.tab-panel-container {
  display: flex;
  align-items: center;
  background-color: #f0f2f5;
  padding: 1px 0 0 5px;
  border-bottom: 1px solid var(--el-border-color-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.draggable-tabs {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-wrap: nowrap; /* Prevent wrapping */
  gap: 2px;
  overflow-x: auto; /* Allow horizontal scrolling if tabs overflow */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.draggable-tabs::-webkit-scrollbar {
  display: none; /* Chrome, Safari, and Opera */
}

.tab-controls {
  display: flex;
  align-items: stretch;
  height: 100%;
}

.control-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 45px;
  cursor: pointer;
  font-size: 18px;
  transition: background-color 0.2s ease;
  height: 100%;
}

.control-btn:hover {
  background-color: #d1d5db;
}

.close-btn:hover {
  background-color: #e81123;
  color: white;
}

.tab-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 34px;
  background-color: #d1d5db;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 13px;
  color: #4b5563;
  z-index: 1;
}

.tab-item:hover {
  background-color: #e5e7eb;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  z-index: 2;
}

.tab-item::before,
.tab-item::after {
  content: "";
  position: absolute;
  bottom: 0;
  width: 10px;
  height: 10px;
}

/* Curves for inactive tabs */
.tab-item:not(.active)::before {
  left: -10px;
  background-image: radial-gradient(circle at 0 0, transparent 9.5px, #d1d5db 10px);
}
.tab-item:not(.active)::after {
  right: -10px;
  background-image: radial-gradient(circle at 100% 0, transparent 9.5px, #d1d5db 10px);
}

/* Curves for hovered tabs */
.tab-item:not(.active):hover::before {
  background-image: radial-gradient(circle at 0 0, transparent 9.5px, #e5e7eb 10px);
}
.tab-item:not(.active):hover::after {
  background-image: radial-gradient(circle at 100% 0, transparent 9.5px, #e5e7eb 10px);
}

/* Curves for active tab */
.tab-item.active::before {
  left: -10px;
  background-image: radial-gradient(circle at 0 0, transparent 9.5px, #667eea 10px);
}
.tab-item.active::after {
  right: -10px;
  background-image: radial-gradient(circle at 100% 0, transparent 9.5px, #764ba2 10px);
}

.tab-title {
  margin-right: 8px;
}

.close-icon {
  font-size: 14px;
  border-radius: 50%;
  padding: 2px;
  transition: all 0.2s ease;
}

.tab-item:not(.active) .close-icon:hover {
  background-color: #dcdfe6;
  color: #909399;
}

.tab-item.active .close-icon:hover {
  background-color: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.ghost-tab {
  opacity: 0.5;
  background: #c8e6fb;
}

.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 8px 0;
  margin: 0;
  list-style: none;
  z-index: 3000;
  font-size: 13px;
  color: #606266;
}

.context-menu li {
  padding: 8px 16px;
  cursor: pointer;
}

.context-menu li:hover {
  background-color: #ecf5ff;
  color: #409eff;
}
</style>
