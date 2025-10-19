import { defineStore } from "pinia";
import { ref, watch } from "vue";
import { useRouter } from "vue-router";

export interface Tab {
  id: string; // Corresponds to menu id
  title: string;
  path: string;
  closable?: boolean;
}

const STORAGE_KEY_TABS = "admin_tabs_state";
const STORAGE_KEY_ACTIVE_TAB = "admin_active_tab_state";

export const useTabStore = defineStore("tabStore", () => {
  const router = useRouter();

  // Load initial state from localStorage
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

  const tabs = ref<Tab[]>(loadFromStorage(STORAGE_KEY_TABS, []));
  const activeTabId = ref<string | null>(loadFromStorage(STORAGE_KEY_ACTIVE_TAB, null));

  // Watch for changes and save to localStorage
  watch(
    tabs,
    (newTabs) => {
      try {
        localStorage.setItem(STORAGE_KEY_TABS, JSON.stringify(newTabs));
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

  const setActiveTab = (tabId: string) => {
    const tabToActivate = tabs.value.find((t) => t.id === tabId);
    if (tabToActivate) {
      activeTabId.value = tabToActivate.id;
      if (router.currentRoute.value.path !== tabToActivate.path) {
        router.push(tabToActivate.path);
      }
    }
  };

  const addTab = (tab: Tab) => {
    const existingTab = tabs.value.find((t) => t.id === tab.id);
    if (!existingTab) {
      tabs.value.push(tab);
    }
    setActiveTab(tab.id);
  };

  const removeTab = (tabId: string) => {
    const index = tabs.value.findIndex((t) => t.id === tabId);
    if (index === -1) return;

    // If the closed tab was active, decide which tab to activate next
    if (activeTabId.value === tabId) {
      const newActiveTab = tabs.value[index + 1] || tabs.value[index - 1];
      if (newActiveTab) {
        setActiveTab(newActiveTab.id);
      } else {
        activeTabId.value = null;
        // Optionally, navigate to a default route if all tabs are closed
        // router.push('/');
      }
    }

    tabs.value.splice(index, 1);
  };

  const closeOtherTabs = (tabId: string) => {
    const currentTab = tabs.value.find((t) => t.id === tabId);
    if (currentTab) {
      tabs.value = [currentTab];
      setActiveTab(tabId);
    }
  };

  const setTabs = (newTabs: Tab[]) => {
    tabs.value = newTabs;
  };

  // Sync active tab with router
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

  return {
    tabs,
    activeTabId,
    addTab,
    removeTab,
    setActiveTab,
    closeOtherTabs,
    setTabs,
  };
});
