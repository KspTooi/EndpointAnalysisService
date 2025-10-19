import { defineStore } from "pinia";
import { EventHolder } from "./EventHolder.ts";

export const PreferenceHolder = defineStore("PreferenceHolder", {
  state: () => ({
    //请求编辑器Tab
    requestEditorTab: "header" as "header" | "body" | "response" | "lastResponse",

    //请求组编辑器Tab
    requestGroupEditorTab: "filter" as "filter" | "request-list",
  }),

  getters: {
    getRequestEditorTab: (state) => {
      //直接从localStorage中加载 如果为空则返回header
      const persistedRequestEditorTab = localStorage.getItem("request_editor_tab");

      if (persistedRequestEditorTab) {
        state.requestEditorTab = persistedRequestEditorTab as "header" | "body" | "response" | "lastResponse";
      }

      return state.requestEditorTab;
    },

    getRequestGroupEditorTab: (state) => {
      //直接从localStorage中加载 如果为空则返回simpleFilter
      const persistedRequestGroupEditorTab = localStorage.getItem("request_group_editor_tab");
      if (persistedRequestGroupEditorTab) {
        state.requestGroupEditorTab = persistedRequestGroupEditorTab as "filter" | "request-list";
      }
      return state.requestGroupEditorTab;
    },
  },

  actions: {
    setRequestEditorTab(tab: "header" | "body" | "response" | "lastResponse") {
      localStorage.setItem("request_editor_tab", tab);
      this.requestEditorTab = tab;
    },

    setRequestGroupEditorTab(tab: "filter" | "request-list") {
      localStorage.setItem("request_group_editor_tab", tab);
      this.requestGroupEditorTab = tab;
    },
  },
});
