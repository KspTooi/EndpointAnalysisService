import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetOutBlueprintListDto extends PageQuery {
  name?: string; // 蓝图名称
  projectName?: string; // 项目名称
  code?: string; // 蓝图编码
}

/**
 * 列表VO
 */
export interface GetOutBlueprintListVo {
  id: string; // 主键ID
  name: string; // 蓝图名称
  projectName: string; // 项目名称
  code: string; // 蓝图编码
  scmUrl: string; // SCM仓库地址
  createTime: string; // 创建时间
}

/**
 * 详情VO
 */
export interface GetOutBlueprintDetailsVo {
  id: string; // 主键ID
  name: string; // 蓝图名称
  projectName: string; // 项目名称
  code: string; // 蓝图编码
  scmUrl: string; // SCM仓库地址
  scmAuthKind: number; // SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT
  scmUsername: string; // SCM用户名
  scmPassword: string; // SCM密码
  scmPk: string; // SSH KEY
  scmBranch: string; // SCM分支
  scmBasePath: string; // 基准路径
  remark: string; // 蓝图备注
}

/**
 * 新增DTO
 */
export interface AddOutBlueprintDto {
  name: string; // 蓝图名称
  projectName: string; // 项目名称
  code: string; // 蓝图编码
  scmUrl: string; // SCM仓库地址
  scmAuthKind: number; // SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT
  scmUsername: string; // SCM用户名
  scmPassword: string; // SCM密码
  scmPk: string; // SSH KEY
  scmBranch: string; // SCM分支
  scmBasePath: string; // 基准路径
  remark: string; // 蓝图备注
}

/**
 * 编辑DTO
 */
export interface EditOutBlueprintDto {
  id: string; // 主键ID
  name: string; // 蓝图名称
  projectName: string; // 项目名称
  code: string; // 蓝图编码
  scmUrl: string; // SCM仓库地址
  scmAuthKind: number; // SCM认证方式 0:公开 1:账号密码 2:SSH KEY 3:PAT
  scmUsername: string; // SCM用户名
  scmPassword: string; // SCM密码
  scmPk: string; // SSH KEY
  scmBranch: string; // SCM分支
  scmBasePath: string; // 基准路径
  remark: string; // 蓝图备注
}

export default {
  /**
   * 获取输出蓝图表列表
   */
  getOutBlueprintList: async (dto: GetOutBlueprintListDto): Promise<PageResult<GetOutBlueprintListVo>> => {
    return await Http.postEntity<PageResult<GetOutBlueprintListVo>>("/outBlueprint/getOutBlueprintList", dto);
  },

  /**
   * 获取输出蓝图表详情
   */
  getOutBlueprintDetails: async (dto: CommonIdDto): Promise<GetOutBlueprintDetailsVo> => {
    const result = await Http.postEntity<Result<GetOutBlueprintDetailsVo>>("/outBlueprint/getOutBlueprintDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增输出蓝图表
   */
  addOutBlueprint: async (dto: AddOutBlueprintDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outBlueprint/addOutBlueprint", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑输出蓝图表
   */
  editOutBlueprint: async (dto: EditOutBlueprintDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outBlueprint/editOutBlueprint", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除输出蓝图表
   */
  removeOutBlueprint: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/outBlueprint/removeOutBlueprint", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
