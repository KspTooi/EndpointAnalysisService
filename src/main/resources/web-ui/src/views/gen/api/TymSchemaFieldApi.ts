import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetTymSchemaFieldListDto extends PageQuery {
  typeSchemaId?: string; // 类型映射方案ID
}

/**
 * 列表VO
 */
export interface GetTymSchemaFieldListVo {
  id: string; // 主键ID
  typeSchemaId: string; // 类型映射方案ID
  source: string; // 匹配源类型
  target: string; // 匹配目标类型
  seq: number; // 排序
}

/**
 * 详情VO
 */
export interface GetTymSchemaFieldDetailsVo {
  id: string; // 主键ID
  typeSchemaId: string; // 类型映射方案ID
  source: string; // 匹配源类型
  target: string; // 匹配目标类型
  seq: number; // 排序
}

/**
 * 新增DTO
 */
export interface AddTymSchemaFieldDto {
  typeSchemaId: string; // 类型映射方案ID
  source: string; // 匹配源类型
  target: string; // 匹配目标类型
  seq: number; // 排序
}

/**
 * 编辑DTO
 */
export interface EditTymSchemaFieldDto {
  id: string; // 主键ID
  source: string; // 匹配源类型
  target: string; // 匹配目标类型
  seq: number; // 排序
}

export default {
  /**
   * 获取类型映射方案字段表列表
   */
  getTymSchemaFieldList: async (dto: GetTymSchemaFieldListDto): Promise<PageResult<GetTymSchemaFieldListVo>> => {
    return await Http.postEntity<PageResult<GetTymSchemaFieldListVo>>("/tymSchemaField/getTymSchemaFieldList", dto);
  },

  /**
   * 获取类型映射方案字段表详情
   */
  getTymSchemaFieldDetails: async (dto: CommonIdDto): Promise<GetTymSchemaFieldDetailsVo> => {
    const result = await Http.postEntity<Result<GetTymSchemaFieldDetailsVo>>("/tymSchemaField/getTymSchemaFieldDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增类型映射方案字段表
   */
  addTymSchemaField: async (dto: AddTymSchemaFieldDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/tymSchemaField/addTymSchemaField", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑类型映射方案字段表
   */
  editTymSchemaField: async (dto: EditTymSchemaFieldDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/tymSchemaField/editTymSchemaField", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除类型映射方案字段表
   */
  removeTymSchemaField: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/tymSchemaField/removeTymSchemaField", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
