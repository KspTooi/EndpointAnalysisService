import type PageResult from "@/commons/entity/PageResult.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

export interface GetUserRequestEnvStorageListDto extends PageQuery {
  requestEnvId?: string | null; // 用户环境ID
  name?: string | null; // 变量名
  status?: number | null; // 状态 0:启用 1:禁用
}

export interface GetUserRequestEnvStorageListVo {
  id: string; // 共享存储ID
  envId: string; // 环境ID
  name: string; // 变量名
  initValue: string; // 初始值
  value: string; // 当前值
  status: number; // 状态 0:启用 1:禁用
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
}

export interface AddUserRequestEnvStorageDto {
  requestEnvId?: string | null; // 用户环境ID
  name?: string | null; // 变量名
  initValue?: string | null; // 初始值
  value?: string | null; // 当前值
  status?: number | null; // 状态 0:启用 1:禁用
}

export interface EditUserRequestEnvStorageDto {
  id?: string | null; // 共享存储ID
  name?: string | null; // 变量名
  initValue?: string | null; // 初始值
  value?: string | null; // 当前值
  status?: number | null; // 状态 0:启用 1:禁用
}

export interface GetUserRequestEnvStorageDetailsVo {
  id?: string | null; // 共享存储ID
  envId?: string | null; // 环境ID
  name?: string | null; // 变量名
  initValue?: string | null; // 初始值
  value?: string | null; // 当前值
  status?: number | null; // 状态 0:启用 1:禁用
  createTime?: string | null; // 创建时间
  updateTime?: string | null; // 更新时间
}

export default {
  /**
   * 查询环境共享存储列表
   */
  getUserRequestEnvStorageList: async (dto: GetUserRequestEnvStorageListDto): Promise<PageResult<GetUserRequestEnvStorageListVo>> => {
    return await Http.postEntity<PageResult<GetUserRequestEnvStorageListVo>>("/userRequestEnvStorage/getUserRequestEnvStorageList", dto);
  },

  /**
   * 新增环境共享存储
   */
  addUserRequestEnvStorage: async (dto: AddUserRequestEnvStorageDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnvStorage/addUserRequestEnvStorage", dto);
  },

  /**
   * 编辑环境共享存储
   */
  editUserRequestEnvStorage: async (dto: EditUserRequestEnvStorageDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnvStorage/editUserRequestEnvStorage", dto);
  },

  /**
   * 查询环境共享存储详情
   */
  getUserRequestEnvStorageDetails: async (dto: CommonIdDto): Promise<Result<GetUserRequestEnvStorageDetailsVo>> => {
    return await Http.postEntity<Result<GetUserRequestEnvStorageDetailsVo>>("/userRequestEnvStorage/getUserRequestEnvStorageDetails", dto);
  },

  /**
   * 删除环境共享存储
   */
  removeUserRequestEnvStorage: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnvStorage/removeUserRequestEnvStorage", dto);
  },
};
