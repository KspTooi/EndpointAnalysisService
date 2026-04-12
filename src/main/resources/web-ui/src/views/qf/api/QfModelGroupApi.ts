import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/model/PageResult.ts";
import type CommonIdDto from "@/commons/model/CommonIdDto.ts";
import type PageQuery from "@/commons/model/PageQuery.ts";
import type Result from "@/commons/model/Result.ts";

/**
 * 查询流程模型分组列表Dto
 */
export interface GetQfModelGroupListDto extends PageQuery {
  name?: string; // 组名称
  code?: string; // 组编码
}

/**
 * 查询流程模型分组列表Vo
 */
export interface GetQfModelGroupListVo {
  id: string; // 主键ID
  name: string; // 组名称
  code: string; // 组编码
  seq: number; // 排序
  createTime: string; // 创建时间
}

/**
 * 查询流程模型分组详情Vo
 */
export interface GetQfModelGroupDetailsVo {
  id: string; // 主键ID
  name: string; // 组名称
  code: string; // 组编码
  remark: string; // 备注
  seq: number; // 排序
}

/**
 * 新增流程模型分组Dto
 */
export interface AddQfModelGroupDto {
  name: string; // 组名称
  code: string; // 组编码
  remark: string; // 备注
  seq: number; // 排序
}

/**
 * 编辑流程模型分组Dto
 */
export interface EditQfModelGroupDto {
  id: string; // 主键ID
  name: string; // 组名称
  code: string; // 组编码
  remark: string; // 备注
  seq: number; // 排序
}

export default {
  /**
   * 获取流程模型分组列表
   */
  getQfModelGroupList: async (dto: GetQfModelGroupListDto): Promise<PageResult<GetQfModelGroupListVo>> => {
    return await Http.postEntity<PageResult<GetQfModelGroupListVo>>("/qfModelGroup/getQfModelGroupList", dto);
  },

  /**
   * 获取流程模型分组详情
   */
  getQfModelGroupDetails: async (dto: CommonIdDto): Promise<GetQfModelGroupDetailsVo> => {
    const result = await Http.postEntity<Result<GetQfModelGroupDetailsVo>>("/qfModelGroup/getQfModelGroupDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增流程模型分组
   */
  addQfModelGroup: async (dto: AddQfModelGroupDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qfModelGroup/addQfModelGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑流程模型分组
   */
  editQfModelGroup: async (dto: EditQfModelGroupDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qfModelGroup/editQfModelGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除流程模型分组
   */
  removeQfModelGroup: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qfModelGroup/removeQfModelGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
