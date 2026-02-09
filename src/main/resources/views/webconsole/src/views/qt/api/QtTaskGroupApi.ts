import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetQtTaskGroupListDto extends PageQuery {
  name?: string; // 分组名
  remark?: string; // 分组备注
}

/**
 * 列表VO
 */
export interface GetQtTaskGroupListVo {
  id: string; // 
  name: string; // 分组名
  remark: string; // 分组备注
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
  deleteTime: string; // 删除时间 NULL未删
}

/**
 * 详情VO
 */
export interface GetQtTaskGroupDetailsVo {
  id: string; // 
  name: string; // 分组名
  remark: string; // 分组备注
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
  deleteTime: string; // 删除时间 NULL未删
}

/**
 * 新增DTO
 */
export interface AddQtTaskGroupDto {
  name: string; // 分组名
  remark: string; // 分组备注
}

/**
 * 编辑DTO
 */
export interface EditQtTaskGroupDto {
  id: string; // 
  name: string; // 分组名
  remark: string; // 分组备注
}

export default {
  /**
   * 获取任务分组列表
   */
  getQtTaskGroupList: async (dto: GetQtTaskGroupListDto): Promise<PageResult<GetQtTaskGroupListVo>> => {
    return await Http.postEntity<PageResult<GetQtTaskGroupListVo>>("/qtTaskGroup/getQtTaskGroupList", dto);
  },

  /**
   * 获取任务分组详情
   */
  getQtTaskGroupDetails: async (dto: CommonIdDto): Promise<GetQtTaskGroupDetailsVo> => {
    const result = await Http.postEntity<Result<GetQtTaskGroupDetailsVo>>("/qtTaskGroup/getQtTaskGroupDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增任务分组
   */
  addQtTaskGroup: async (dto: AddQtTaskGroupDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTaskGroup/addQtTaskGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑任务分组
   */
  editQtTaskGroup: async (dto: EditQtTaskGroupDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTaskGroup/editQtTaskGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除任务分组
   */
  removeQtTaskGroup: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTaskGroup/removeQtTaskGroup", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
