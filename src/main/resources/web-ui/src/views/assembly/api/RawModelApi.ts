import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/model/PageResult.ts";

/**
 * 查询原始模型列表 DTO（对应后端 GetRawModelDto）
 */
export interface GetRawModelDto {
  outputSchemaId: string; // 输出方案ID
}

/**
 * 原始模型列表 VO（对应后端 GetRawModelListVo）
 */
export interface GetRawModelListVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  name: string; // 字段名
  dataType: string; // 数据类型
  length: string; // 长度
  require: number; // 必填 0:否 1:是
  remark: string; // 备注
  seq: number; // 排序
}

export default {
  /**
   * 获取输出方案原始模型列表
   */
  getRawModelList: async (dto: GetRawModelDto): Promise<PageResult<GetRawModelListVo>> => {
    return await Http.postEntity<PageResult<GetRawModelListVo>>("/rawModel/getRawModelList", dto);
  },
};
