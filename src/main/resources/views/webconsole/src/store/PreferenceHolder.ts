import { defineStore } from "pinia";
import { ReloadHolder } from "./ReloadHolder";

export const PreferenceHolder = defineStore("PreferenceHolder", {
  state: () => ({
    //请求编辑器Tab
    requestEditorTab: "header" as "header" | "body" | "response",
  }),

  getters: {
    getRequestEditorTab: (state) => {
      //直接从localStorage中加载 如果为空则返回header
      const persistedRequestEditorTab = localStorage.getItem("request_editor_tab");

      if (persistedRequestEditorTab) {
        state.requestEditorTab = persistedRequestEditorTab as "header" | "body" | "response";
      }

      return state.requestEditorTab;
    },
  },

  actions: {
    setRequestEditorTab(tab: "header" | "body" | "response") {
      localStorage.setItem("request_editor_tab", tab);
      this.requestEditorTab = tab;
    },
  },
});
