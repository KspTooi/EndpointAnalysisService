import Http from "@/commons/Http.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GroupPermissionDefinitionVo {
  id: string; // 权限节点ID
  code: string; // 权限节点标识
  name: string; // 权限节点名称
  has: number; // 当前组是否拥有 0:拥有 1:不拥有
}

export interface GetGroupListDto extends PageQuery {
  keyword?: string; // 模糊匹配 组标识、组名称、组描述
  status?: number; // 组状态：0:禁用 1:启用
}

export interface GetGroupListVo {
  id: string; // 组ID
  code: string; // 组标识
  name: string; // 组名称
  memberCount: number; // 成员数量
  permissionCount: number; // 权限节点数量
  isSystem: number; // 系统内置组 0:否 1:是
  status: number; // 组状态：0-禁用，1-启用
  createTime: string; // 创建时间
}

export interface GetGroupDetailsVo {
  id: string; // 组ID
  code: string; // 组标识
  name: string; // 组名称
  remark: string; // 组描述
  isSystem: number; // 系统内置组 0:否 1:是
  status: number; // 组状态：0-禁用，1-启用
  seq: number; // 排序号
  rowScope: number; // 数据权限 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
  deptIds?: string[]; // 部门ID列表
  permissions: GroupPermissionDefinitionVo[]; // 权限节点列表
}

export interface AddGroupDto {
  code: string; // 组标识
  name: string; // 组名称
  remark?: string; // 组描述
  status: number; // 组状态：0-禁用，1-启用
  seq: number; // 排序号
  rowScope: number; // 数据权限 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
  deptIds?: string[]; // 部门ID列表
  permissionIds?: string[]; // 权限ID列表
}

export interface EditGroupDto {
  id: string; // 组ID
  code: string; // 组标识
  name: string; // 组名称
  remark?: string; // 组描述
  status: number; // 组状态：0-禁用，1-启用
  seq: number; // 排序号
  rowScope: number; // 数据权限 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
  deptIds?: string[]; // 部门ID列表
  permissionIds?: string[]; // 权限ID列表
}

export interface GetGroupPermissionMenuViewDto {
  groupId: string; // 组ID
  keyword?: string; // 模糊匹配 菜单名称、菜单路径
  hasPermission?: number | null; // 是否已授权 0:否 1:是
}

export interface GetGroupPermissionMenuViewVo {
  id: string; // 菜单ID
  parentId: string; // 父级ID
  name: string; // 菜单名称
  menuIcon: string; // 菜单图标
  menuKind: number; // 菜单类型
  menuPath: string; // 菜单路径
  menuBtnId: string; // 按钮ID
  permission: string; // 所需权限
  missingPermission: number; // 是否缺失权限节点 0:否 1:是 2:部分缺失
  hasPermission: number; // 当前用户是否有权限 0:否 1:是
  seq: number; // 排序
  children: GetGroupPermissionMenuViewVo[]; // 子菜单
}

export interface GetGroupPermissionNodeDto extends PageQuery {
  groupId: string; // 组ID
  keyword?: string | null; // 模糊匹配 权限节点名称
  hasPermission?: number | null; // 是否已授权 0:否 1:是
}

export interface GetGroupPermissionNodeVo {
  id: string; // 权限ID
  name: string; // 权限名称
  code: string; // 权限标识
  remark: string; // 权限描述
  seq: number; // 排序号
  hasPermission: number; // 是否已授权 0:否 1:是
}

export interface GrantAndRevokeDto {
  groupId: string; // 组ID
  permissionCodes: string[]; // 权限代码列表
  type: number; // 类型 0:授权 1:取消授权
}

export default {
  /**
   * 获取组列表
   */
  getGroupList: async (dto: GetGroupListDto): Promise<PageResult<GetGroupListVo>> => {
    return await Http.postEntity<PageResult<GetGroupListVo>>("/group/getGroupList", dto);
  },

  /**
   * 获取组详情
   */
  getGroupDetails: async (dto: CommonIdDto): Promise<GetGroupDetailsVo> => {
    var result = await Http.postEntity<Result<GetGroupDetailsVo>>("/group/getGroupDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增组
   */
  addGroup: async (dto: AddGroupDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/group/addGroup", dto);
  },

  /**
   * 编辑组
   */
  editGroup: async (dto: EditGroupDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/group/editGroup", dto);
  },

  /**
   * 批量授权或取消授权
   */
  grantAndRevoke: async (dto: GrantAndRevokeDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/group/grantAndRevoke", dto);
  },

  /**
   * 获取组权限菜单视图
   */
  getGroupPermissionMenuView: async (dto: GetGroupPermissionMenuViewDto): Promise<GetGroupPermissionMenuViewVo[]> => {
    var result = await Http.postEntity<Result<GetGroupPermissionMenuViewVo[]>>("/group/getGroupPermissionMenuView", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取组权限节点视图
   */
  getGroupPermissionNodeView: async (dto: GetGroupPermissionNodeDto): Promise<PageResult<GetGroupPermissionNodeVo>> => {
    return await Http.postEntity<PageResult<GetGroupPermissionNodeVo>>("/group/getGroupPermissionNodeView", dto);
  },

  /**
   * 删除组
   */
  removeGroup: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/group/removeGroup", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
