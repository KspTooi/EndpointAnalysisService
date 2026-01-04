import { defineStore } from "pinia";
import type { GetCollectionTreeVo } from "../api/CollectionApi";

export const useRdbgStore = defineStore("RdbgStore", {
  state: () => ({
    //当前激活的集合
    selectedIds: [] as string[],
  }),
  getters: {
    /**
     * 获取当前激活的集合
     * @param state 状态
     * @returns 当前激活的集合
     */
    getSelectedIds: (state) => state.selectedIds,
  },
  actions: {
    addSelectedId(id: string) {
      this.selectedIds.push(id);
      console.log(this.selectedIds);
    },
    removeSelectedId(id: string) {
      this.selectedIds = this.selectedIds.filter((id) => id !== id);
    },
    clearSelectedIds() {
      this.selectedIds = [];
    },
  },
});
