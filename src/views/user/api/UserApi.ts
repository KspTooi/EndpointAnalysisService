import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetUserListDto extends PageQuery {
  username?: String; // 用户名
  password?: String; // 密码
  nickname?: String; // 昵称
  gender?: Integer; // 性别 0:男 1:女 2:不愿透露
  phone?: String; // 手机号码
  email?: String; // 邮箱
  loginCount?: String; // 登录次数
  status?: Integer; // 用户状态 0:正常 1:封禁
  lastLoginTime?: LocalDateTime; // 最后登录时间
  rootId?: Long; // 所属企业ID
  rootName?: String; // 所属企业名称
  deptId?: Long; // 部门ID
  deptName?: String; // 部门名称
  activeCompanyId?: Long; // 已激活的公司ID(兼容字段)
  activeEnvId?: Long; // 已激活的环境ID(兼容字段)
  avatarAttachId?: Long; // 用户头像附件ID
  isSystem?: Integer; // 内置用户 0:否 1:是
  dataVersion?: Long; // 数据版本号
}

/**
 * 列表VO
 */
export interface GetUserListVo {
  id: Long; // 用户ID
  username: String; // 用户名
  password: String; // 密码
  nickname: String; // 昵称
  gender: Integer; // 性别 0:男 1:女 2:不愿透露
  phone: String; // 手机号码
  email: String; // 邮箱
  loginCount: String; // 登录次数
  status: Integer; // 用户状态 0:正常 1:封禁
  lastLoginTime: LocalDateTime; // 最后登录时间
  rootId: Long; // 所属企业ID
  rootName: String; // 所属企业名称
  deptId: Long; // 部门ID
  deptName: String; // 部门名称
  activeCompanyId: Long; // 已激活的公司ID(兼容字段)
  activeEnvId: Long; // 已激活的环境ID(兼容字段)
  avatarAttachId: Long; // 用户头像附件ID
  isSystem: Integer; // 内置用户 0:否 1:是
  dataVersion: Long; // 数据版本号
  createTime: LocalDateTime; // 创建时间
  creatorId: Long; // 创建人ID
  updateTime: LocalDateTime; // 修改时间
  updaterId: Long; // 更新人ID
  deleteTime: LocalDateTime; // 删除时间 为NULL未删
}

/**
 * 详情VO
 */
export interface GetUserDetailsVo {
  id: Long; // 用户ID
  username: String; // 用户名
  password: String; // 密码
  nickname: String; // 昵称
  gender: Integer; // 性别 0:男 1:女 2:不愿透露
  phone: String; // 手机号码
  email: String; // 邮箱
  loginCount: String; // 登录次数
  status: Integer; // 用户状态 0:正常 1:封禁
  lastLoginTime: LocalDateTime; // 最后登录时间
  rootId: Long; // 所属企业ID
  rootName: String; // 所属企业名称
  deptId: Long; // 部门ID
  deptName: String; // 部门名称
  activeCompanyId: Long; // 已激活的公司ID(兼容字段)
  activeEnvId: Long; // 已激活的环境ID(兼容字段)
  avatarAttachId: Long; // 用户头像附件ID
  isSystem: Integer; // 内置用户 0:否 1:是
  dataVersion: Long; // 数据版本号
  createTime: LocalDateTime; // 创建时间
  creatorId: Long; // 创建人ID
  updateTime: LocalDateTime; // 修改时间
  updaterId: Long; // 更新人ID
  deleteTime: LocalDateTime; // 删除时间 为NULL未删
}

/**
 * 新增DTO
 */
export interface AddUserDto {
  username: String; // 用户名
  password: String; // 密码
  nickname: String; // 昵称
  gender: Integer; // 性别 0:男 1:女 2:不愿透露
  phone: String; // 手机号码
  email: String; // 邮箱
  loginCount: String; // 登录次数
  status: Integer; // 用户状态 0:正常 1:封禁
  lastLoginTime: LocalDateTime; // 最后登录时间
  rootId: Long; // 所属企业ID
  rootName: String; // 所属企业名称
  deptId: Long; // 部门ID
  deptName: String; // 部门名称
  activeCompanyId: Long; // 已激活的公司ID(兼容字段)
  activeEnvId: Long; // 已激活的环境ID(兼容字段)
  avatarAttachId: Long; // 用户头像附件ID
  isSystem: Integer; // 内置用户 0:否 1:是
  dataVersion: Long; // 数据版本号
}

/**
 * 编辑DTO
 */
export interface EditUserDto {
  id: Long; // 用户ID
  username: String; // 用户名
  password: String; // 密码
  nickname: String; // 昵称
  gender: Integer; // 性别 0:男 1:女 2:不愿透露
  phone: String; // 手机号码
  email: String; // 邮箱
  loginCount: String; // 登录次数
  status: Integer; // 用户状态 0:正常 1:封禁
  lastLoginTime: LocalDateTime; // 最后登录时间
  rootId: Long; // 所属企业ID
  rootName: String; // 所属企业名称
  deptId: Long; // 部门ID
  deptName: String; // 部门名称
  activeCompanyId: Long; // 已激活的公司ID(兼容字段)
  activeEnvId: Long; // 已激活的环境ID(兼容字段)
  avatarAttachId: Long; // 用户头像附件ID
  isSystem: Integer; // 内置用户 0:否 1:是
  dataVersion: Long; // 数据版本号
}

export default {
  /**
   * 获取${model.comment}列表
   */
  getUserList: async (dto: GetUserListDto): Promise<PageResult<GetUserListVo>> => {
    return await Http.postEntity<PageResult<GetUserListVo>>("/user/getUserList", dto);
  },

  /**
   * 获取${model.comment}详情
   */
  getUserDetails: async (dto: CommonIdDto): Promise<GetUserDetailsVo> => {
    const result = await Http.postEntity<Result<GetUserDetailsVo>>("/user/getUserDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增${model.comment}
   */
  addUser: async (dto: AddUserDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/user/addUser", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑${model.comment}
   */
  editUser: async (dto: EditUserDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/user/editUser", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除${model.comment}
   */
  removeUser: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/user/removeUser", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
