import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

export interface UserGroupVo {
  id: string; // 用户组ID
  name: string; // 用户组名称
  description: string; // 用户组描述
  sortOrder: number; // 排序顺序
  isSystem: boolean; // 是否为系统内置组
  hasGroup: boolean; // 用户是否属于此组
}

export interface UserPermissionVo {
  id: string; // 权限ID
  permKey: string; // 权限键
  name: string; // 权限名称
  description: string; // 权限描述
  isSystem: boolean; // 是否为系统内置权限
}

export interface GetUserListDto extends PageQuery {
  username?: string; // 用户名查询
  status?: number | null; // 用户状态查询: 0-正常, 1-封禁
}

export interface GetUserListVo {
  id: string; // 用户ID
  username: string; // 用户名
  nickname: string; // 用户昵称
  email: string; // 用户邮箱
  createTime: string; // 创建时间
  lastLoginTime: string; // 最后登录时间
  status: number; // 用户状态
}

export interface GetUserDetailsVo {
  id: string; // 用户ID
  username: string; // 用户名
  nickname: string; // 用户昵称
  email: string; // 用户邮箱
  status: number; // 用户状态
  createTime: string; // 创建时间
  lastLoginTime: string; // 最后登录时间
  groups: UserGroupVo[]; // 用户组列表
  permissions: UserPermissionVo[]; // 用户权限列表
}

export interface AddUserDto {
  username: string; // 用户名
  password: string; // 用户密码
  nickname?: string; // 用户昵称
  email?: string; // 用户邮箱
  status?: number; // 用户状态：0-禁用，1-启用
  groupIds?: string[]; // 用户组ID列表
}

export interface EditUserDto {
  id: string; // 用户ID
  username: string; // 用户名
  password?: string; // 用户密码，编辑时可选
  nickname?: string; // 用户昵称
  email?: string; // 用户邮箱
  status?: number; // 用户状态：0-禁用，1-启用
  groupIds?: string[]; // 用户组ID列表
}

export default {
  /**
   * 获取用户列表
   */
  getUserList: async (dto: GetUserListDto): Promise<PageResult<GetUserListVo>> => {
    return await Http.postEntity<PageResult<GetUserListVo>>("/user/getUserList", dto);
  },

  /**
   * 获取用户详情
   */
  getUserDetails: async (dto: CommonIdDto): Promise<GetUserDetailsVo> => {
    var result = await Http.postEntity<Result<GetUserDetailsVo>>("/user/getUserDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增用户
   */
  addUser: async (dto: AddUserDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/user/addUser", dto);
  },

  /**
   * 编辑用户
   */
  editUser: async (dto: EditUserDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/user/editUser", dto);
  },

  /**
   * 删除用户
   */
  removeUser: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/user/removeUser", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
