import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface PreCheckAttachDto {
  name: string; //文件名
  kind: string; //业务代码
  totalSize: string; //文件大小
  sha256: string; //SHA256
}

export interface PreCheckAttachVo {
  preCheckId: string; //文件预检ID
  name: string; //服务端文件名
  kind: string; //业务代码
  path: string; //服务端文件路径
  status: number; //状态 0:预检文件 1:区块不完整 2:校验中 3:正常
  missingChunkIds: string[]; //缺失的分块ID
}

export interface ApplyChunkVo {
  attachId: string; //附件ID
  chunkTotal: number; //分块总数
  chunkApplied: number; //分块已应用数量
}

export default {
  /**
   * 预检附件
   * @param dto 预检附件参数
   * @returns 预检附件结果
   */
  preCheckAttach: async (dto: PreCheckAttachDto): Promise<Result<PreCheckAttachVo>> => {
    return await Http.postEntity<Result<PreCheckAttachVo>>("/attach/preCheckAttach", dto);
  },

  /**
   * 应用区块
   * @param preCheckId 文件预检ID
   * @param chunkId 区块ID
   * @param file 区块文件
   * @returns 应用区块结果
   */
  applyChunk: async (preCheckId: string, chunkId: string, file: File): Promise<Result<ApplyChunkVo>> => {
    return await Http.postForm<Result<ApplyChunkVo>>("/attach/applyChunk", { preCheckId, chunkId, file });
  },
};
