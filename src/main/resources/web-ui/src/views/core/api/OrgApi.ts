import Http from "@/commons/Http.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetOrgTreeDto {
  name?: string; // 组织机构名称
}

export interface GetOrgTreeVo {
  id: string; // 主键id
  rootId: string; // 一级组织ID
  parentId: string | null; // 上级组织ID NULL顶级组织
  kind: number; // 0:部门 1:企业
  name: string; // 组织机构名称
  seq: number; // 排序
  children: GetOrgTreeVo[]; // 子组织
}

export interface GetOrgDetailsVo {
  id: string; // 主键id
  rootId: string; // 一级组织ID
  parentId: string | null; // 上级组织ID NULL顶级组织
  kind: number; // 0:部门 1:企业
  name: string; // 组织机构名称
  principalId: string | null; // 主管ID
  principalName: string; // 主管名称
  seq: number; // 排序
}

export interface AddOrgDto {
  parentId?: string | null; // 上级组织ID NULL顶级组织
  kind: number; // 0:部门 1:企业
  name: string; // 组织机构名称
  principalId?: string | null; // 主管ID 企业不允许填
  seq: number; // 排序
}

export interface EditOrgDto {
  id: string; // 主键id
  parentId?: string | null; // 上级组织ID NULL顶级组织
  name: string; // 组织机构名称
  principalId?: string | null; // 主管ID
  seq: number; // 排序
}

export default {
  /**
   * 获取组织机构树
   */
  getOrgTree: async (dto: GetOrgTreeDto): Promise<GetOrgTreeVo[]> => {
    var result = await Http.postEntity<Result<GetOrgTreeVo[]>>("/org/getOrgTree", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取组织机构详情
   */
  getOrgDetails: async (dto: CommonIdDto): Promise<GetOrgDetailsVo> => {
    var result = await Http.postEntity<Result<GetOrgDetailsVo>>("/org/getOrgDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增组织机构
   */
  addOrg: async (dto: AddOrgDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/org/addOrg", dto);
  },

  /**
   * 编辑组织机构
   */
  editOrg: async (dto: EditOrgDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/org/editOrg", dto);
  },

  /**
   * 删除组织机构
   */
  removeOrg: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/org/removeOrg", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
