import { computed } from "vue";

//定义常用文件分类
export enum EntryCategory {
  // 图片
  PHOTO = "photo",
  // 视频
  VIDEO = "video",
  // 音频
  AUDIO = "audio",
  // PDF
  PDF = "pdf",
  // 文件夹
  DIRECTORY = "directory",
  // Word文档
  WORD = "word",
  // Excel表格
  EXCEL = "excel",
  // PPT演示文稿
  PPT = "ppt",
  // 压缩包
  ARCHIVE = "archive",
  // 代码文件
  CODE = "code",
  // 文本文件
  TEXT = "text",
  // 可执行文件
  EXECUTABLE = "executable",
  // 其他
  OTHER = "other",
}

const FILE_CATEGORY_MAP = {
  [EntryCategory.PHOTO]: ["jpg", "jpeg", "png", "gif", "bmp", "webp", "svg", "ico", "heic", "tiff"],
  [EntryCategory.VIDEO]: ["mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v", "mpeg", "mpg"],
  [EntryCategory.AUDIO]: ["mp3", "wav", "flac", "aac", "ogg", "wma", "m4a", "ape"],
  [EntryCategory.PDF]: ["pdf"],
  [EntryCategory.WORD]: ["doc", "docx", "rtf"],
  [EntryCategory.EXCEL]: ["xls", "xlsx"],
  [EntryCategory.PPT]: ["ppt", "pptx"],
  [EntryCategory.ARCHIVE]: ["zip", "rar", "7z", "tar", "gz", "bz2", "iso"],
  [EntryCategory.CODE]: [
    "js",
    "ts",
    "jsx",
    "tsx",
    "vue",
    "java",
    "py",
    "cpp",
    "c",
    "cs",
    "go",
    "rs",
    "php",
    "rb",
    "swift",
    "kt",
    "sql",
    "css",
    "scss",
    "less",
    "html",
    "xml",
    "json",
    "yaml",
    "yml",
    "sh",
    "bat",
    "cmd",
    "conf",
  ],
  [EntryCategory.TEXT]: ["txt", "md", "log", "csv"],
  [EntryCategory.EXECUTABLE]: ["exe", "msi", "app", "apk", "dmg", "sh", "bat", "cmd"],
};

export default {
  /**
   * 根据文件后缀获取文件分类
   * @param suffix 文件后缀
   * @returns 文件分类
   */
  useFileCategory(suffix: string) {
    const fileCategory = computed(() => {
      return this.getFileCategory(suffix);
    });

    return {
      fileCategory,
    };
  },

  /**
   * 根据文件后缀获取文件分类
   * @param suffix 文件后缀
   * @returns 文件分类
   */
  getFileCategory(suffix: string): EntryCategory {
    if (!suffix) {
      return EntryCategory.OTHER;
    }

    for (const [key, value] of Object.entries(FILE_CATEGORY_MAP)) {
      if (value.includes(suffix)) {
        return key as EntryCategory;
      }
    }
    return EntryCategory.OTHER;
  },
};
