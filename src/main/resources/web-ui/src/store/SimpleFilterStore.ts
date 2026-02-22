import { defineStore } from "pinia";

export const SimpleFilterStore = defineStore("SimpleFilterStore", {
  state: () => ({
    // 当前选中的过滤器ID
    selectedFilterId: null as string | null,

    //当前是否在创建模式
    isCreatingState: false,

    needReloadList: 0,
  }),

  getters: {
    isNeedReloadList: (state) => {
      return state.needReloadList;
    },

    getSelectedFilterId: (state) => {
      // 如果selectedFilterId为空，则从localStorage中加载
      if (state.selectedFilterId == null) {
        const selectedFilterId = localStorage.getItem("simple_filter_selected_id");
        if (selectedFilterId) {
          state.selectedFilterId = selectedFilterId;
        }
      }
      return state.selectedFilterId;
    },

    isCreating: (state) => {
      return state.isCreatingState;
    },
  },

  actions: {
    setSelectedFilterId(filterId: string | null) {
      this.isCreatingState = false;

      // 如果filterId为空，则从localStorage中删除
      if (filterId == null) {
        localStorage.removeItem("simple_filter_selected_id");
        this.selectedFilterId = null;
        return;
      }

      // 只有当选中的过滤器ID发生变化时才更新localStorage
      if (this.selectedFilterId !== filterId) {
        localStorage.setItem("simple_filter_selected_id", filterId);
      }
      this.selectedFilterId = filterId;
    },

    setIsCreating(isCreating: boolean) {
      this.isCreatingState = isCreating;
    },

    clearSelection() {
      this.setSelectedFilterId(null);
    },

    requestReloadList() {
      this.needReloadList++;
    },
  },
});
