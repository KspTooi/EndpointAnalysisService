import type { GetEntryListVo } from "@/api/drive/DriveApi";
import { defineStore } from "pinia";

export const DriveHolder = defineStore("DriveHolder", {
  state: () => ({
    //粘贴板
    clipBoardEntry: [] as GetEntryListVo[],
  }),

  getters: {
    getClipBoardEntry: (state) => {
      return state.clipBoardEntry;
    },
  },

  actions: {
    setClipBoardEntry(entry: GetEntryListVo[]) {
      this.clipBoardEntry = entry;
    },
  },
});
