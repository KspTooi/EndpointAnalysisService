import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetOutModelPolyListDto extends PageQuery {
  id?: string; // 主键ID
  outputSchemaId?: string; // 输出方案ID
  outputModelOriginId?: string; // 原始字段ID
  name?: string; // 聚合字段名
  kind?: string; // 聚合数据类型
  length?: string; // 聚合长度
  require?: number; // 聚合必填 0:否 1:是
  policyCrudJson?: string; // 聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW
  policyQuery?: number; // 聚合查询策略 0:等于 1:模糊
  policyView?: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  placeholder?: string; // placeholder
  seq?: number; // 聚合排序
}

/**
 * 列表VO
 */
export interface GetOutModelPolyListVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string; // 聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  placeholder: string; // placeholder
  seq: number; // 聚合排序
}

/**
 * 详情VO
 */
export interface GetOutModelPolyDetailsVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string; // 聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  placeholder: string; // placeholder
  seq: number; // 聚合排序
}

/**
 * 新增DTO
 */
export interface AddOutModelPolyDto {
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string; // 聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  placeholder: string; // placeholder
  seq: number; // 聚合排序
}

/**
 * 编辑DTO
 */
export interface EditOutModelPolyDto {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string; // 聚合可见性策略 ADD、EDIT、LIST_QUERY、LIST_VIEW
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  placeholder: string; // placeholder
  seq: number; // 聚合排序
}

export default {
  /**
   * 获取输出方案聚合模型表列表
   */
  getOutModelPolyList: async (dto: GetOutModelPolyListDto): Promise<PageResult<GetOutModelPolyListVo>> => {
    return await Http.postEntity<PageResult<GetOutModelPolyListVo>>("/outModelPoly/getOutModelPolyList", dto);
  },

  /**
   * 获取输出方案聚合模型表详情
   */
  getOutModelPolyDetails: async (dto: CommonIdDto): Promise<GetOutModelPolyDetailsVo> => {
    const result = await Http.postEntity<Result<GetOutModelPolyDetailsVo>>("/outModelPoly/getOutModelPolyDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增输出方案聚合模型表
   */
  addOutModelPoly: async (dto: AddOutModelPolyDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/addOutModelPoly", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑输出方案聚合模型表
   */
  editOutModelPoly: async (dto: EditOutModelPolyDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/editOutModelPoly", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除输出方案聚合模型表
   */
  removeOutModelPoly: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/removeOutModelPoly", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
