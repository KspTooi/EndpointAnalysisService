import { defineStore } from "pinia";

export const EventHolder = defineStore("EventHolder", {
  state: () => ({
    //是否需要重新加载树
    needReloadTree: 0,

    ctrlsState: 0,
    ctrlDState: 0,
  }),

  getters: {
    isNeedReloadTree: (state) => {
      return state.needReloadTree;
    },

    isOnCtrlS: (state) => {
      return state.ctrlsState;
    },

    isOnCtrlD: (state) => {
      return state.ctrlDState;
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
  },
});
