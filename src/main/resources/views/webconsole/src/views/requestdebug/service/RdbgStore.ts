import { defineStore } from "pinia";
import type { GetCollectionTreeVo } from "../api/CollectionApi";

export const useRdbgStore = defineStore("RdbgStore", {
  state: () => ({
    //当前被选中的集合ID列表
    selectedIds: [] as string[],

    //当前展开的集合ID列表
    expandedIds: [] as string[],
  }),
  getters: {
    /**
     * 获取当前激活的集合
     * @param state 状态
     * @returns 当前激活的集合
     */
    getSelectedIds: (state) => state.selectedIds,

    /**
     * 获取当前展开的集合ID列表
     * @param state 状态
     * @returns 当前展开的集合ID列表
     */
    getExpandedIds: (state) => state.expandedIds,
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
    addExpandedId(id: string) {
      this.expandedIds.push(id);
    },
    removeExpandedId(id: string) {
      this.expandedIds = this.expandedIds.filter((id) => id !== id);
    },
    clearExpandedIds() {
      this.expandedIds = [];
    },
  },
});
