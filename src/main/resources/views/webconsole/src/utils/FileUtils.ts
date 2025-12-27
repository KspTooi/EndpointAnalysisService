/**
 * 格式化文件大小
 * @param bytes 文件字节大小
 * @returns 格式化后的文件字节大小 当文件字节大小为0时返回"-"
 */
export const formatFileSize = (bytes: string): string => {
  const bytesNum = parseInt(bytes);

  if (!bytesNum || bytesNum === 0) {
    return "-";
  }
  if (bytesNum < 1024) {
    return bytesNum + " B";
  }
  if (bytesNum < 1024 * 1024) {
    return (bytesNum / 1024).toFixed(2) + " KB";
  }
  if (bytesNum < 1024 * 1024 * 1024) {
    return (bytesNum / (1024 * 1024)).toFixed(2) + " MB";
  }
  return (bytesNum / (1024 * 1024 * 1024)).toFixed(2) + " GB";
};

export default {
  formatFileSize,
};
