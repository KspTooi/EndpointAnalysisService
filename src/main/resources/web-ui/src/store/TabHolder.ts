import { defineStore } from "pinia";
import { ref, watch } from "vue";
import { useRouter } from "vue-router";

export interface Tab {
  id: string; // 标签ID
  title: string; // 标签标题
  path: string; // 标签路径
  closable?: boolean; // 是否可关闭
}

const fixedTabs = [
  {
    id: "index",
    title: "首页",
    path: "/index",
    closable: false,
  },
];

const STORAGE_KEY_TABS = "admin_tabs_state";
const STORAGE_KEY_ACTIVE_TAB = "admin_active_tab_state";

const fixedTabIds = new Set(fixedTabs.map((t) => t.id));

export const useTabStore = defineStore("tabStore", () => {
  const router = useRouter();

  /**
   * 从localStorage中加载数据
   * @param key 键
   * @param defaultValue 默认值
   * @returns 数据
   */
  const loadFromStorage = <T>(key: string, defaultValue: T): T => {
    try {
      const saved = localStorage.getItem(key);
      if (saved) {
        return JSON.parse(saved);
      }
    } catch (error) {
      console.error(`Failed to load state from localStorage for key "${key}":`, error);
    }
    return defaultValue;
  };

  // 持久化只存非固定标签，加载时将fixedTabs插入头部
  const savedDynamicTabs = loadFromStorage<Tab[]>(STORAGE_KEY_TABS, []).filter(
    (t) => !fixedTabIds.has(t.id)
  );
  const tabs = ref<Tab[]>([...fixedTabs, ...savedDynamicTabs]);
  const activeTabId = ref<string | null>(loadFromStorage(STORAGE_KEY_ACTIVE_TAB, null));
  const refreshCounter = ref(0);

  // 持久化时只保存非固定标签
  watch(
    tabs,
    (newTabs) => {
      try {
        const dynamicTabs = newTabs.filter((t) => !fixedTabIds.has(t.id));
        localStorage.setItem(STORAGE_KEY_TABS, JSON.stringify(dynamicTabs));
      } catch (error) {
        console.error("Failed to save tabs state to localStorage:", error);
      }
    },
    { deep: true }
  );

  watch(activeTabId, (newActiveTabId) => {
    try {
      localStorage.setItem(STORAGE_KEY_ACTIVE_TAB, JSON.stringify(newActiveTabId));
    } catch (error) {
      console.error("Failed to save active tab state to localStorage:", error);
    }
  });

  const refreshActiveView = () => {
    refreshCounter.value++;
  };

  const setActiveTab = (tabId: string) => {
    const tabToActivate = tabs.value.find((t) => t.id === tabId);
    if (tabToActivate) {
      activeTabId.value = tabToActivate.id;
      if (router.currentRoute.value.path !== tabToActivate.path) {
        router.push(tabToActivate.path);
      }
    }
  };

  /**
   * 添加标签，始终追加到非固定标签末尾
   * @param tab 标签
   */
  const addTab = (tab: Tab) => {
    const existingTab = tabs.value.find((t) => t.id === tab.id);
    if (!existingTab) {
      tabs.value.push(tab);
    }
    setActiveTab(tab.id);
  };

  /**
   * 插入标签，索引不得小于fixedTabs数量
   * @param tab 标签
   * @param index 索引（从0开始，最小为fixedTabs.length）
   */
  const insertTab = (tab: Tab, index: number) => {
    let _index = index;
    if (_index < fixedTabs.length) {
      _index = fixedTabs.length;
    }
    if (_index > tabs.value.length) {
      _index = tabs.value.length;
    }

    const existingTab = tabs.value.find((t) => t.id === tab.id);
    if (existingTab) {
      setActiveTab(existingTab.id);
      return;
    }
    tabs.value.splice(_index, 0, tab);
    setActiveTab(tab.id);
  };

  const removeTab = (tabId: string) => {
    // 固定标签不可删除
    if (fixedTabIds.has(tabId)) return;

    const index = tabs.value.findIndex((t) => t.id === tabId);
    if (index === -1) return;

    if (activeTabId.value === tabId) {
      const newActiveTab = tabs.value[index + 1] || tabs.value[index - 1];
      if (newActiveTab) {
        setActiveTab(newActiveTab.id);
      } else {
        activeTabId.value = null;
      }
    }

    tabs.value.splice(index, 1);
  };

  const closeOtherTabs = (tabId: string) => {
    const currentTab = tabs.value.find((t) => t.id === tabId);
    if (!currentTab) return;
    // 保留固定标签 + 当前标签（若当前标签本身是固定标签则只保留固定标签）
    if (fixedTabIds.has(tabId)) {
      tabs.value = [...fixedTabs];
    } else {
      tabs.value = [...fixedTabs, currentTab];
    }
    setActiveTab(tabId);
  };

  const setTabs = (newTabs: Tab[]) => {
    // 确保fixedTabs始终在最前
    const dynamicTabs = newTabs.filter((t) => !fixedTabIds.has(t.id));
    tabs.value = [...fixedTabs, ...dynamicTabs];
  };

  // 同步路由到激活标签
  watch(
    () => router.currentRoute.value.path,
    (newPath) => {
      const matchingTab = tabs.value.find((tab) => tab.path === newPath);
      if (matchingTab && activeTabId.value !== matchingTab.id) {
        activeTabId.value = matchingTab.id;
      }
    },
    { immediate: true }
  );

  /**
   * 激活指定索引的标签（1-based）
   * @param index 索引
   */
  const activeOf = (index: number) => {
    if (index < 1 || index > tabs.value.length) {
      return null;
    }
    setActiveTab(tabs.value[index - 1].id);
  };

  return {
    tabs,
    activeTabId,
    refreshCounter,
    addTab,
    insertTab,
    removeTab,
    setActiveTab,
    closeOtherTabs,
    setTabs,
    refreshActiveView,
    activeOf,
  };
});
