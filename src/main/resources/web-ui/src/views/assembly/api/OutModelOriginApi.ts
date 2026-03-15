import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";

/**
 * 查询列表DTO
 */
export interface GetOutModelOriginListDto {
  outputSchemaId: string; // 输出方案ID
}

/**
 * 列表VO
 */
export interface GetOutModelOriginListVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  name: string; // 原始字段名
  kind: string; // 原始数据类型
  length: string; // 原始长度
  require: number; // 原始必填 0:否 1:是
  remark: string; // 原始备注
  seq: number; // 原始排序
}

export default {
  /**
   * 获取输出方案原始模型表列表
   */
  getOutModelOriginList: async (dto: GetOutModelOriginListDto): Promise<PageResult<GetOutModelOriginListVo>> => {
    return await Http.postEntity<PageResult<GetOutModelOriginListVo>>("/outModelOrigin/getOutModelOriginList", dto);
  },
};
