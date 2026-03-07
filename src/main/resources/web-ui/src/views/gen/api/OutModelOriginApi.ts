import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetOutModelOriginListDto extends PageQuery {
  id?: string; // 主键ID
  outputSchemaId?: string; // 输出方案ID
  name?: string; // 原始字段名
  kind?: string; // 原始数据类型
  length?: string; // 原始长度
  require?: number; // 原始必填 0:否 1:是
  remark?: string; // 原始备注
  seq?: number; // 原始排序
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

/**
 * 详情VO
 */
export interface GetOutModelOriginDetailsVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  name: string; // 原始字段名
  kind: string; // 原始数据类型
  length: string; // 原始长度
  require: number; // 原始必填 0:否 1:是
  remark: string; // 原始备注
  seq: number; // 原始排序
}

/**
 * 新增DTO
 */
export interface AddOutModelOriginDto {
  outputSchemaId: string; // 输出方案ID
  name: string; // 原始字段名
  kind: string; // 原始数据类型
  length: string; // 原始长度
  require: number; // 原始必填 0:否 1:是
  remark: string; // 原始备注
  seq: number; // 原始排序
}

/**
 * 编辑DTO
 */
export interface EditOutModelOriginDto {
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

  /**
   * 获取输出方案原始模型表详情
   */
  getOutModelOriginDetails: async (dto: CommonIdDto): Promise<GetOutModelOriginDetailsVo> => {
    const result = await Http.postEntity<Result<GetOutModelOriginDetailsVo>>("/outModelOrigin/getOutModelOriginDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增输出方案原始模型表
   */
  addOutModelOrigin: async (dto: AddOutModelOriginDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelOrigin/addOutModelOrigin", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑输出方案原始模型表
   */
  editOutModelOrigin: async (dto: EditOutModelOriginDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelOrigin/editOutModelOrigin", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除输出方案原始模型表
   */
  removeOutModelOrigin: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelOrigin/removeOutModelOrigin", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
