import type PageResult from "@/commons/entity/PageResult.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

export interface GetUserRequestEnvListDto extends PageQuery {
  name?: string | null; // 用户请求环境名称
  envKey?: string | null; // 环境键
}

export interface GetUserRequestEnvListVo {
  id: string; // 用户请求环境ID
  name: string; // 用户请求环境名称
  active: number; // 是否激活 0:是 1:否
  remark: string | null; // 备注
  updateTime: string; // 更新时间
}

export interface AddUserRequestEnvDto {
  name?: string | null; // 用户请求环境名称
  active?: number | null; // 是否激活 0:是 1:否
  remark?: string | null; // 备注
}

export interface EditUserRequestEnvDto {
  id?: string | null; // 用户请求环境ID
  name?: string | null; // 用户请求环境名称
  active?: number | null; // 是否激活 0:是 1:否
  remark?: string | null; // 备注
}

export interface GetUserRequestEnvDetailsVo {
  id?: string | null; // 用户请求环境ID
  name?: string | null; // 用户请求环境名称
  active?: number | null; // 是否激活 0:是 1:否
  remark?: string | null; // 备注
  updateTime?: string | null; // 更新时间
}

export default {
  /**
   * 获取当前用户的环境列表
   */
  getUserRequestEnvList: async (dto: GetUserRequestEnvListDto): Promise<PageResult<GetUserRequestEnvListVo>> => {
    return await Http.postEntity<PageResult<GetUserRequestEnvListVo>>("/userRequestEnv/getUserRequestEnvList", dto);
  },

  /**
   * 新增当前用户的环境
   */
  addUserRequestEnv: async (dto: AddUserRequestEnvDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnv/addUserRequestEnv", dto);
  },

  /**
   * 编辑当前用户的环境
   */
  editUserRequestEnv: async (dto: EditUserRequestEnvDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnv/editUserRequestEnv", dto);
  },
  /**
   * 获取当前用户的环境详情
   */

  getUserRequestEnvDetails: async (dto: CommonIdDto): Promise<Result<GetUserRequestEnvDetailsVo>> => {
    return await Http.postEntity<Result<GetUserRequestEnvDetailsVo>>("/userRequestEnv/getUserRequestEnvDetails", dto);
  },
  /**
   * 删除当前用户的环境
   */
  removeUserRequestEnv: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/userRequestEnv/removeUserRequestEnv", dto);
  },
};
