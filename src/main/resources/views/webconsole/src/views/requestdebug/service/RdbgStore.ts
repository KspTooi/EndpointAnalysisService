import { defineStore } from "pinia";
import type { GetCollectionDetailsVo, GetCollectionTreeVo } from "../api/CollectionApi";

/**
 * 提交详情
 * @param id 数据ID
 * @param data 数据
 * @param commitImmediately 立即提交
 */
export interface CommitDetails {
  id: string;
  data: GetCollectionDetailsVo;
  commitImmediately: () => void;
}

export const useRdbgStore = defineStore("RdbgStore", {
  state: () => ({
    //当前编辑器请求标签页
    editorTab: "params" as "params" | "body" | "header",

    //当前激活的集合
    activeCollection: null as GetCollectionTreeVo | null,

    //当前被选中的集合列表
    selectedCollections: [] as GetCollectionTreeVo[],

    //当前展开的集合列表
    expandedCollections: [] as GetCollectionTreeVo[],

    //当前未提交更改的集合列表
    uncommittedCollections: [] as CommitDetails[],
  }),
  getters: {
    getEditorTab: (state) => state.editorTab,
    getActiveCollection: (state) => state.activeCollection,
    getSelectedCollections: (state) => state.selectedCollections,
    getExpandedCollections: (state) => state.expandedCollections,
    getUncommittedCollections: (state) => state.uncommittedCollections,
  },
  actions: {
    setEditorTab(tab: "params" | "body" | "header") {
      this.editorTab = tab;
    },
    /**
     * 设置当前激活的集合
     * @param collection 集合
     */
    setActiveCollection(collection: GetCollectionTreeVo) {
      this.activeCollection = collection;
    },
    /**
     * 清空当前激活的集合
     */
    clearActiveCollection() {
      this.activeCollection = null;
    },

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
    addUncommittedCollection(collection: CommitDetails) {
      if (collection == null) {
        return;
      }
      //已存在时移除旧值并添加新值
      if (this.uncommittedCollections.find((c) => c.id === collection.id)) {
        this.removeUncommittedCollection(collection);
      }
      this.uncommittedCollections.push(collection);
    },
    removeUncommittedCollection(collection: CommitDetails) {
      if (collection == null) {
        return;
      }
      this.uncommittedCollections = this.uncommittedCollections.filter((c) => c.id !== collection.id);
    },
    clearUncommittedCollections() {
      this.uncommittedCollections = [];
    },
  },
});
