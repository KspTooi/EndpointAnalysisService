import type { CurrentDirPo, EntryPo, GetEntryListPathVo } from "@/views/drive/api/DriveTypes.ts";
import type { GetDriveSpaceListVo } from "@/views/drive/api/DriveSpaceApi.ts";
import { defineStore } from "pinia";

const CURRENT_DIR_KEY = "drive_current_dir";
const CURRENT_DRIVE_SPACE_KEY = "drive_current_space";

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

/**
 * 从 localStorage 加载上次选择的云盘空间
 */
const loadCurrentDriveSpaceFromStorage = (): GetDriveSpaceListVo | null => {
  const stored = localStorage.getItem(CURRENT_DRIVE_SPACE_KEY);
  if (!stored) {
    return null;
  }
  try {
    return JSON.parse(stored);
  } catch (e) {
    return null;
  }
};

/**
 * 保存选中的云盘空间到 localStorage
 */
const saveCurrentDriveSpaceToStorage = (space: GetDriveSpaceListVo | null): void => {
  if (!space) {
    localStorage.removeItem(CURRENT_DRIVE_SPACE_KEY);
    return;
  }
  localStorage.setItem(CURRENT_DRIVE_SPACE_KEY, JSON.stringify(space));
};

export const DriveStore = defineStore("DriveStore", {
  state: () => ({
    //粘贴板
    clipBoardEntry: [] as EntryPo[],

    //当前目录
    currentDir: loadCurrentDirFromStorage(),

    //当前目录路径
    currentDirPaths: [] as GetEntryListPathVo[],

    //当前选中的云盘空间
    currentDriveSpace: loadCurrentDriveSpaceFromStorage() as GetDriveSpaceListVo | null,
  }),

  getters: {
    /**
     * 获取粘贴板
     */
    getClipBoardEntry: (state) => {
      return state.clipBoardEntry;
    },
    /**
     * 获取当前目录
     */
    getCurrentDir: (state) => {
      return state.currentDir;
    },
    /**
     * 获取当前目录路径
     */
    getCurrentDirPaths: (state) => {
      return state.currentDirPaths;
    },
    /**
     * 获取当前选中的云盘空间
     */
    getCurrentDriveSpace: (state) => {
      return state.currentDriveSpace;
    },
  },

  actions: {
    /**
     * 设置粘贴板
     */
    setClipBoardEntry(entry: EntryPo[]) {
      this.clipBoardEntry = entry;
    },
    /**
     * 设置当前目录
     */
    setCurrentDir(dir: CurrentDirPo) {
      this.currentDir = dir;
      saveCurrentDirToStorage(dir);
    },
    /**
     * 设置当前目录路径
     */
    setCurrentDirPaths(paths: GetEntryListPathVo[]) {
      this.currentDirPaths = paths;
    },
    /**
     * 设置当前选中的云盘空间（持久化）
     */
    setCurrentDriveSpace(space: GetDriveSpaceListVo | null) {
      this.currentDriveSpace = space;
      saveCurrentDriveSpaceToStorage(space);
    },
  },
});
