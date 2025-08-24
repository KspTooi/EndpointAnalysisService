import { defineStore } from "pinia";

export const EventHolder = defineStore("EventHolder", {
  state: () => ({
    //是否需要重新加载树
    needReloadTree: 0,

    //是否需要重新加载请求
    needReloadRequestDetails: 0,

    ctrlsState: 0,
    ctrlDState: 0,
    deleteState: 0,
  }),

  getters: {
    isNeedReloadTree: (state) => {
      return state.needReloadTree;
    },

    isNeedReloadRequestDetails: (state) => {
      return state.needReloadRequestDetails;
    },

    isOnCtrlS: (state) => {
      return state.ctrlsState;
    },

    isOnCtrlD: (state) => {
      return state.ctrlDState;
    },

    isOnDelete: (state) => {
      return state.deleteState;
    },
  },

  actions: {
    requestReloadTree() {
      this.needReloadTree++;
    },

    onCtrlS() {
      this.ctrlsState++;
    },

    onCtrlD() {
      this.ctrlDState++;
    },

    onDelete() {
      this.deleteState++;
    },

    requestReloadRequestDetails() {
      this.needReloadRequestDetails++;
    },
  },
});
