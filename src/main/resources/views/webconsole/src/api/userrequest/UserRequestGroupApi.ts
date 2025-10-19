import type Result from "@/commons/entity/Result.ts";
import Http from "@/commons/Http.ts";
import type { GetSimpleFilterListVo } from "../SimpleFilterApi.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";

export interface AddUserRequestGroupDto {
  parentId: number | null; //父级ID 为空表示根节点
  name: string; //请求组名称
}

export interface EditUserRequestGroupDto {
  id: number; //请求组ID
  name: string; //请求组名称
  description: string | null; //请求组描述
  simpleFilterIds: string[]; //应用的简单过滤器
}

export interface GetUserRequestGroupDetailsVo {
  id: number; //请求组ID
  name: string; //请求组名称
  description: string | null; //请求组描述
  simpleFilters: GetSimpleFilterListVo[]; //基本过滤器列表
}

export default {
  addUserRequestGroup: async (dto: AddUserRequestGroupDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequestGroup/addUserRequestGroup", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  editUserRequestGroup: async (dto: EditUserRequestGroupDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequestGroup/editUserRequestGroup", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  getUserRequestGroupDetails: async (dto: CommonIdDto): Promise<GetUserRequestGroupDetailsVo> => {
    var result = await Http.postEntity<Result<GetUserRequestGroupDetailsVo>>("/userRequestGroup/getUserRequestGroupDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },
};
