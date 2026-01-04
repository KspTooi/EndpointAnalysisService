import { defineStore } from "pinia";
import type { GetCollectionTreeVo } from "../api/CollectionApi";

export const useRdbgStore = defineStore("RdbgStore", {
  state: () => ({
    //当前被选中的集合列表
    selectedCollections: [] as GetCollectionTreeVo[],

    //当前展开的集合列表
    expandedCollections: [] as GetCollectionTreeVo[],
  }),
  getters: {
    getSelectedCollections: (state) => state.selectedCollections,
    getExpandedCollections: (state) => state.expandedCollections,
  },
  actions: {
    /**
     * 添加被选中的集合
     * @param collection 集合
     */
    addSelectedCollection(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return;
      }
      //已存在时不重复添加
      if (this.selectedCollections.find((c) => c.id === collection.id)) {
        return;
      }
      this.selectedCollections.push(collection);
    },

    /**
     * 移除被选中的集合
     * @param collection 集合
     */
    removeSelectedCollection(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return;
      }
      this.selectedCollections = this.selectedCollections.filter((c) => c.id !== collection.id);
    },

    /**
     * 清空被选中的集合列表
     */
    clearSelectedCollections() {
      this.selectedCollections = [];
    },

    /**
     * 判断集合是否被选中
     * @param collection 集合
     * @returns 是否被选中
     */
    isSelected(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return false;
      }
      return this.selectedCollections.find((c) => c.id === collection.id) != null;
    },

    /**
     * 添加展开的集合
     * @param collection 集合
     */
    addExpandedCollection(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return;
      }
      //已存在时不重复添加
      if (this.expandedCollections.find((c) => c.id === collection.id)) {
        return;
      }
      this.expandedCollections.push(collection);
    },

    /**
     * 移除展开的集合
     * @param collection 集合
     */
    removeExpandedCollection(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return;
      }
      this.expandedCollections = this.expandedCollections.filter((c) => c.id !== collection.id);
    },

    /**
     * 清空展开的集合列表
     */
    clearExpandedCollections() {
      this.expandedCollections = [];
    },

    /**
     * 判断集合是否展开
     * @param collection 集合
     * @returns 是否展开
     */
    isExpanded(collection: GetCollectionTreeVo) {
      if (collection == null) {
        return false;
      }
      return this.expandedCollections.find((c) => c.id === collection.id) != null;
    },
  },
});
