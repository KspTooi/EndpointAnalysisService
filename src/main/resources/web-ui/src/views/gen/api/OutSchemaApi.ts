import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetOutSchemaListDto extends PageQuery {
  name?: string; // 输出方案名称
  modelName?: string; // 模型名称
  tableName?: string; // 数据源表名
}

/**
 * 列表VO
 */
export interface GetOutSchemaListVo {
  id: string; // 主键ID
  name: string; // 输出方案名称
  modelName: string; // 模型名称
  tableName: string; // 数据源表名
  fieldCountOrigin: number; // 字段数(原始)
  fieldCountPoly: number; // 字段数(聚合)
}

/**
 * 详情VO
 */
export interface GetOutSchemaDetailsVo {
  id: string; // 主键ID
  dataSourceId: string; // 数据源ID
  typeSchemaId: string; // 类型映射方案ID
  inputScmId: string; // 输入SCM ID
  outputScmId: string; // 输出SCM ID
  name: string; // 输出方案名称
  modelName: string; // 模型名称
  tableName: string; // 数据源表名
  removeTablePrefix: string; // 移除表前缀
  permCodePrefix: string; // 权限码前缀
  policyOverride: number; // 写出策略 0:不覆盖 1:覆盖
  baseInput: string; // 输入基准路径
  baseOutput: string; // 输出基准路径
  remark: string; // 备注
}

/**
 * 新增DTO
 */
export interface AddOutSchemaDto {
  dataSourceId: string; // 数据源ID
  typeSchemaId: string; // 类型映射方案ID
  inputScmId: string; // 输入SCM ID
  outputScmId: string; // 输出SCM ID
  name: string; // 输出方案名称
  modelName: string; // 模型名称
  tableName: string; // 数据源表名
  removeTablePrefix: string; // 移除表前缀
  permCodePrefix: string; // 权限码前缀
  policyOverride: number; // 写出策略 0:不覆盖 1:覆盖
  baseInput: string; // 输入基准路径
  baseOutput: string; // 输出基准路径
  remark: string; // 备注
}

/**
 * 编辑DTO
 */
export interface EditOutSchemaDto {
  id: string; // 主键ID
  dataSourceId: string; // 数据源ID
  typeSchemaId: string; // 类型映射方案ID
  inputScmId: string; // 输入SCM ID
  outputScmId: string; // 输出SCM ID
  name: string; // 输出方案名称
  modelName: string; // 模型名称
  tableName: string; // 数据源表名
  removeTablePrefix: string; // 移除表前缀
  permCodePrefix: string; // 权限码前缀
  policyOverride: number; // 写出策略 0:不覆盖 1:覆盖
  baseInput: string; // 输入基准路径
  baseOutput: string; // 输出基准路径
  remark: string; // 备注
}

export default {
  /**
   * 获取输出方案列表
   */
  getOutSchemaList: async (dto: GetOutSchemaListDto): Promise<PageResult<GetOutSchemaListVo>> => {
    return await Http.postEntity<PageResult<GetOutSchemaListVo>>("/outSchema/getOutSchemaList", dto);
  },

  /**
   * 获取输出方案详情
   */
  getOutSchemaDetails: async (dto: CommonIdDto): Promise<GetOutSchemaDetailsVo> => {
    const result = await Http.postEntity<Result<GetOutSchemaDetailsVo>>("/outSchema/getOutSchemaDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增输出方案
   */
  addOutSchema: async (dto: AddOutSchemaDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outSchema/addOutSchema", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑输出方案
   */
  editOutSchema: async (dto: EditOutSchemaDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outSchema/editOutSchema", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除输出方案
   */
  removeOutSchema: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outSchema/removeOutSchema", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 执行输出方案
   */
  executeOutSchema: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outSchema/executeOutSchema", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
