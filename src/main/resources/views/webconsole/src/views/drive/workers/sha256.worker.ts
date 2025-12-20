import { createSHA256 } from "hash-wasm";

const CHUNK_SIZE = 10 * 1024 * 1024; // 10MB - 增大分片以提升性能

interface HashMessage {
  type: "start" | "chunk" | "end";
  file?: File;
  chunk?: ArrayBuffer;
  chunkIndex?: number;
  totalChunks?: number;
}

interface HashResponse {
  type: "progress" | "completed" | "error";
  progress?: number;
  hash?: string;
  error?: string;
}

self.onmessage = async (e: MessageEvent<HashMessage>) => {
  const { type, file } = e.data;

  if (type === "start" && file) {
    try {
      const sha256 = await createSHA256();
      const fileSize = file.size;
      const totalChunks = Math.ceil(fileSize / CHUNK_SIZE);
      let processedChunks = 0;
      let lastReportedProgress = 0;

      for (let i = 0; i < totalChunks; i++) {
        const start = i * CHUNK_SIZE;
        const end = Math.min(start + CHUNK_SIZE, fileSize);
        const fileChunk = file.slice(start, end);
        const arrayBuffer = await fileChunk.arrayBuffer();

        sha256.update(new Uint8Array(arrayBuffer));
        processedChunks++;

        const progress = Math.round((processedChunks / totalChunks) * 100);
        
        // 只在进度变化超过 5% 或完成时才报告，减少消息传递开销
        if (progress - lastReportedProgress >= 5 || progress === 100) {
          lastReportedProgress = progress;
          const response: HashResponse = {
            type: "progress",
            progress: progress,
          };
          self.postMessage(response);
        }
      }

      const hash = sha256.digest("hex");
      const response: HashResponse = {
        type: "completed",
        hash: hash,
      };
      self.postMessage(response);
    } catch (error: any) {
      const response: HashResponse = {
        type: "error",
        error: error.message || "计算哈希失败",
      };
      self.postMessage(response);
    }
    return;
  }
};
