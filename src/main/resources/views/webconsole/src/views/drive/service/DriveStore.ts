import type { CurrentDirPo, EntryPo, GetEntryListPathVo } from "@/views/drive/api/DriveTypes.ts";
import { defineStore } from "pinia";

const CURRENT_DIR_KEY = "drive_current_dir";

/**
 * 从 localStorage 加载当前目录
 */
const loadCurrentDirFromStorage = (): CurrentDirPo => {
  const stored = localStorage.getItem(CURRENT_DIR_KEY);
  if (!stored) {
    return {
      id: null,
      name: null,
      parentId: null,
    };
  }
  try {
    return JSON.parse(stored);
  } catch (e) {
    return {
      id: null,
      name: null,
      parentId: null,
    };
  }
};

/**
 * 保存当前目录到 localStorage
 */
const saveCurrentDirToStorage = (dir: CurrentDirPo): void => {
  if (!dir) {
    localStorage.removeItem(CURRENT_DIR_KEY);
    return;
  }
  localStorage.setItem(CURRENT_DIR_KEY, JSON.stringify(dir));
};

export const DriveStore = defineStore("DriveStore", {
  state: () => ({
    //粘贴板
    clipBoardEntry: [] as EntryPo[],

    //当前目录
    currentDir: loadCurrentDirFromStorage(),

    //当前目录路径
    currentDirPaths: [] as GetEntryListPathVo[],
  }),

  getters: {
    /**
     * 获取粘贴板
     * @param state 状态
     * @returns 粘贴板
     */
    getClipBoardEntry: (state) => {
      return state.clipBoardEntry;
    },
    /**
     * 获取当前目录
     * @param state 状态
     * @returns 当前目录
     */
    getCurrentDir: (state) => {
      return state.currentDir;
    },
    /**
     * 获取当前目录路径
     * @param state 状态
     * @returns 当前目录路径
     */
    getCurrentDirPaths: (state) => {
      return state.currentDirPaths;
    },
  },

  actions: {
    /**
     * 设置粘贴板
     * @param entry 粘贴板
     */
    setClipBoardEntry(entry: EntryPo[]) {
      this.clipBoardEntry = entry;
    },
    /**
     * 设置当前目录
     * @param dir 当前目录
     */
    setCurrentDir(dir: CurrentDirPo) {
      this.currentDir = dir;
      saveCurrentDirToStorage(dir);
    },
    /**
     * 设置当前目录路径
     * @param paths 当前目录路径
     */
    setCurrentDirPaths(paths: GetEntryListPathVo[]) {
      this.currentDirPaths = paths;
    },
  },
});
