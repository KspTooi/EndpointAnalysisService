import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/model/PageResult.ts";
import type CommonIdDto from "@/commons/model/CommonIdDto.ts";
import type Result from "@/commons/model/Result.ts";

/**
 * 查询聚合模型列表 DTO（对应后端 GetPolyModelListDto）
 */
export interface GetPolyModelListDto {
  outputSchemaId?: string; // 输出方案ID
}

/**
 * 聚合模型列表 VO（对应后端 GetPolyModelListVo）
 */
export interface GetPolyModelListVo {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string[]; // 聚合可见性策略 AD、ED、DV、LD、LV
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  remark: string; // 聚合字段备注
  seq: number; // 聚合排序
}

/**
 * 新增 DTO（对应后端 AddPolyModelDto）
 */
export interface AddPolyModelDto {
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string[]; // 聚合可见性策略 AD、ED、DV、LD、LV
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  remark: string; // 聚合字段备注
  seq: number; // 聚合排序
}

/**
 * 编辑 DTO（对应后端 EditPolyModelDto）
 */
export interface EditPolyModelDto {
  id: string; // 主键ID
  outputSchemaId: string; // 输出方案ID
  outputModelOriginId: string; // 原始字段ID
  name: string; // 聚合字段名
  kind: string; // 聚合数据类型
  length: string; // 聚合长度
  require: number; // 聚合必填 0:否 1:是
  policyCrudJson: string[]; // 聚合可见性策略 AD、ED、DV、LD、LV
  policyQuery: number; // 聚合查询策略 0:等于 1:模糊
  policyView: number; // 聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT
  remark: string; // 聚合字段备注
  seq: number; // 聚合排序
}

export default {
  /**
   * 获取输出方案聚合模型列表
   */
  getPolyModelList: async (dto: GetPolyModelListDto): Promise<PageResult<GetPolyModelListVo>> => {
    return await Http.postEntity<PageResult<GetPolyModelListVo>>("/outModelPoly/getPolyModelList", dto);
  },

  /**
   * 新增输出方案聚合模型
   */
  addPolyModel: async (dto: AddPolyModelDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/addPolyModel", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑输出方案聚合模型
   */
  editPolyModel: async (dto: EditPolyModelDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/editPolyModel", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除输出方案聚合模型
   */
  removePolyModel: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/removePolyModel", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 从原始模型同步聚合模型（输出方案ID）
   */
  syncPolyModelFromOriginBySchema: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outModelPoly/syncPolyModelFromOriginBySchema", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
