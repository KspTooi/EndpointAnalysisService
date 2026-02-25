import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetDriveSpaceListDto extends PageQuery {
  rootId?: string; // 租户ID
  deptId?: string; // 部门ID
  name?: string; // 空间名称
  remark?: string; // 空间描述
  quotaLimit?: string; // 配额限制(bytes)
  quotaUsed?: string; // 已用配额(bytes)
  status?: number; // 状态 0:正常 1:归档
}

/**
 * 列表VO
 */
export interface GetDriveSpaceListVo {
  id: string; // 空间ID
  rootId: string; // 租户ID
  deptId: string; // 部门ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  quotaUsed: string; // 已用配额(bytes)
  status: number; // 状态 0:正常 1:归档
  createTime: string; // 创建时间
  creatorId: string; // 创建人
  updateTime: string; // 更新时间
  updaterId: string; // 更新人
  deleteTime: string; // 删除时间 为NULL未删除
}

/**
 * 详情VO
 */
export interface GetDriveSpaceDetailsVo {
  id: string; // 空间ID
  rootId: string; // 租户ID
  deptId: string; // 部门ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  quotaUsed: string; // 已用配额(bytes)
  status: number; // 状态 0:正常 1:归档
  createTime: string; // 创建时间
  creatorId: string; // 创建人
  updateTime: string; // 更新时间
  updaterId: string; // 更新人
  deleteTime: string; // 删除时间 为NULL未删除
}

/**
 * 新增DTO
 */
export interface AddDriveSpaceDto {
  rootId: string; // 租户ID
  deptId: string; // 部门ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  quotaUsed: string; // 已用配额(bytes)
  status: number; // 状态 0:正常 1:归档
}

/**
 * 编辑DTO
 */
export interface EditDriveSpaceDto {
  id: string; // 空间ID
  rootId: string; // 租户ID
  deptId: string; // 部门ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  quotaUsed: string; // 已用配额(bytes)
  status: number; // 状态 0:正常 1:归档
}

export default {
  /**
   * 获取云盘空间列表
   */
  getDriveSpaceList: async (dto: GetDriveSpaceListDto): Promise<PageResult<GetDriveSpaceListVo>> => {
    return await Http.postEntity<PageResult<GetDriveSpaceListVo>>("/driveSpace/getDriveSpaceList", dto);
  },

  /**
   * 获取云盘空间详情
   */
  getDriveSpaceDetails: async (dto: CommonIdDto): Promise<GetDriveSpaceDetailsVo> => {
    const result = await Http.postEntity<Result<GetDriveSpaceDetailsVo>>("/driveSpace/getDriveSpaceDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增云盘空间
   */
  addDriveSpace: async (dto: AddDriveSpaceDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/driveSpace/addDriveSpace", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑云盘空间
   */
  editDriveSpace: async (dto: EditDriveSpaceDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/driveSpace/editDriveSpace", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除云盘空间
   */
  removeDriveSpace: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/driveSpace/removeDriveSpace", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
