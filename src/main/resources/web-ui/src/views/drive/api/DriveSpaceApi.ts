import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetDriveSpaceListDto extends PageQuery {
  name?: string; // 空间名称
  remark?: string; // 空间描述
  status?: number; // 状态 0:正常 1:归档
}

/**
 * 列表VO
 */
export interface GetDriveSpaceListVo {
  id: string; // 空间ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  quotaUsed: string; // 已用配额(bytes)
  memberCount: number; // 成员数量
  maName: string; // 主管理员名称
  myRole: number; // 我在该空间的角色,0:主管理员 1:行政管理员 2:编辑者 3:查看者
  status: number; // 状态 0:正常 1:归档
}

/**
 * 成员详情VO
 */
export interface GetDriveSpaceMemberDetailsVo {
  id: string; // 成员ID
  driveSpaceId: string; // 云盘空间ID
  memberName: string; // 成员名称
  memberKind: number; // 成员类型 0:用户 1:部门
  memberId: string; // 成员ID
  role: number; // 成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者
}

/**
 * 详情VO
 */
export interface GetDriveSpaceDetailsVo {
  id: string; // 空间ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  status: number; // 状态 0:正常 1:归档
  members: GetDriveSpaceMemberDetailsVo[]; // 成员列表
}

/**
 * 新增成员DTO
 */
export interface AddDriveSpaceMemberDto {
  memberKind: number; // 成员类型 0:用户 1:部门
  memberId: string; // 成员ID
  role: number; // 成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者
}

/**
 * 新增DTO
 */
export interface AddDriveSpaceDto {
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
  status: number; // 状态 0:正常 1:归档
  members: AddDriveSpaceMemberDto[]; // 成员列表
}

/**
 * 编辑成员DTO
 */
export interface EditDriveSpaceMembersDto {
  driveSpaceId: string; // 云盘空间ID
  memberId: string; // 成员ID
  memberKind: number; // 成员类型 0:用户 1:部门
  role: number; // 成员角色 1:行政管理员 2:编辑者 3:查看者
  action: number; // 操作类型 0:添加/修改成员 1:删除成员
}

/**
 * 编辑DTO
 */
export interface EditDriveSpaceDto {
  id: string; // 空间ID
  name: string; // 空间名称
  remark: string; // 空间描述
  quotaLimit: string; // 配额限制(bytes)
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
   * 编辑云盘空间成员
   */
  editDriveSpaceMembers: async (dto: EditDriveSpaceMembersDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/driveSpace/editDriveSpaceMembers", dto);
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
