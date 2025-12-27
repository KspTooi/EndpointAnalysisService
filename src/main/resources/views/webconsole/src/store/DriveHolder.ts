import type { EntryPo } from "@/views/drive/api/DriveTypes.ts";
import { defineStore } from "pinia";

export const DriveHolder = defineStore("DriveHolder", {
  state: () => ({
    //粘贴板
    clipBoardEntry: [] as EntryPo[],
  }),

  getters: {
    getClipBoardEntry: (state) => {
      return state.clipBoardEntry;
    },
  },

  actions: {
    setClipBoardEntry(entry: EntryPo[]) {
      this.clipBoardEntry = entry;
    },
  },
});
