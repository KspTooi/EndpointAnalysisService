<template>
  <div class="tab-panel-container">
    <div class="tab-prefix-controls">
      <el-icon class="control-btn close-btn" @click="onCloseCurrentTab" title="关闭标签页">
        <Close />
      </el-icon>
    </div>
    <draggable
      v-model="tabs"
      item-key="id"
      class="draggable-tabs"
      animation="200"
      ghost-class="ghost-tab"
      ref="draggableRef"
    >
      <template #item="{ element: tab }">
        <div
          class="tab-item"
          :class="{ active: activeTabId === tab.id }"
          @click="onTabClick(tab.id)"
          @contextmenu.prevent="openContextMenu($event, tab)"
        >
          <span class="tab-title">{{ tab.title }}</span>
          <el-icon v-if="tab.closable !== false && tabs.length > 1" class="close-icon" @click.stop="onTabClose(tab.id)">
            <Close />
          </el-icon>
        </div>
      </template>
    </draggable>

    <div class="tab-controls">
      <el-icon class="control-btn" @click="onRefresh" title="刷新">
        <Refresh />
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
import { useTabStore, type Tab } from "@/store/TabHolder.ts";

const draggableRef = ref<any>(null);
const tabStore = useTabStore();
const { activeTabId, tabs } = storeToRefs(tabStore);



const onTabClick = (tabId: string) => {
  tabStore.setActiveTab(tabId);
};

const onTabClose = (tabId: string) => {
  if (tabs.value.length > 1) {
    tabStore.removeTab(tabId);
  }
};

const onRefresh = () => {
  tabStore.refreshActiveView();
};

const onCloseCurrentTab = () => {
  if (activeTabId.value && tabs.value.length > 1) {
    tabStore.removeTab(activeTabId.value);
  }
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
    onTabClose(contextMenu.value.targetTab.id);
  }
  closeContextMenu();
};

const closeOtherTabs = () => {
  if (contextMenu.value.targetTab) {
    tabStore.closeOtherTabs(contextMenu.value.targetTab.id);
  }
  closeContextMenu();
};

const onWheel = (event: WheelEvent) => {
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
    el.addEventListener("wheel", onWheel, { passive: false });
  }
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeContextMenu);
  const el = draggableRef.value?.$el;
  if (el) {
    el.removeEventListener("wheel", onWheel);
  }
});
</script>

<style scoped>
.tab-panel-container {
  display: flex;
  align-items: center;
  background-color: #f5f7fa;
  padding: 0;
  border-bottom: 1px solid #e4e7ed;
  user-select: none;
  height: 38px;
}

.draggable-tabs {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-wrap: nowrap;
  gap: 0;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  height: 100%;
}

.draggable-tabs::-webkit-scrollbar {
  display: none;
}

.tab-prefix-controls {
  display: flex;
  align-items: center;
  height: 100%;
  border-right: 1px solid #e4e7ed;
  background-color: #fff;
  flex-shrink: 0;
}

.tab-controls {
  display: flex;
  align-items: center;
  height: 100%;
  border-left: 1px solid #e4e7ed;
  background-color: #fff;
}

.control-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s ease;
  height: 100%;
  color: #606266;
}

.control-btn:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.close-btn:hover {
  background-color: #fef0f0;
  color: #f56c6c;
}

.tab-item {
  position: relative;
  display: flex;
  align-items: center;
  padding: 0 16px;
  height: 100%;
  background-color: transparent;
  border-right: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 13px;
  color: #606266;
  z-index: 1;
  border-radius: 0 !important;
}

.tab-item:hover {
  background-color: #fff;
  color: #409eff;
}

.tab-item.active {
  background-color: #fff;
  color: #009688; /* 使用项目主题色 */
  font-weight: 600;
  z-index: 2;
}

.tab-item.active::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.tab-title {
  margin-right: 8px;
}

.close-icon {
  font-size: 12px;
  padding: 2px;
  transition: all 0.2s ease;
  border-radius: 0;
}

.tab-item .close-icon:hover {
  background-color: #fef0f0;
  color: #f56c6c;
}

.ghost-tab {
  opacity: 0.5;
  background: #f5f7fa;
}

.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 0;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 4px 0;
  margin: 0;
  list-style: none;
  z-index: 3000;
  font-size: 12px;
  color: #606266;
}

.context-menu li {
  padding: 8px 16px;
  cursor: pointer;
  transition: all 0.2s;
}

.context-menu li:hover {
  background-color: #f5f7fa;
  color: #009688;
}
</style>
