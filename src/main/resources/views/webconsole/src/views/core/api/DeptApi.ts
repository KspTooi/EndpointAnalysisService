import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetDeptListDto extends PageQuery {
  name?: string; // 部门名
  status?: number | null; // 部门状态 0:正常 1:禁用
}

export interface GetDeptListVo {
  id: string; // 部门ID
  parentId: string | null; // 父级部门 NULL为顶级
  name: string; // 部门名
  principalId: string | null; // 负责人ID
  principalName: string; // 负责人名称
  status: number; // 部门状态 0:正常 1:禁用
  seq: number; // 排序
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
}

export interface GetDeptTreeVo {
  id: string; // 部门ID
  parentId: string | null; // 父级部门 NULL为顶级
  name: string; // 部门名
  principalId: string | null; // 负责人ID
  principalName: string; // 负责人名称
  status: number; // 部门状态 0:正常 1:禁用
  seq: number; // 排序
  children: GetDeptTreeVo[]; // 子部门列表
}

export interface GetDeptDetailsVo {
  id: string; // 部门ID
  parentId: string | null; // 父级部门 NULL为顶级
  name: string; // 部门名
  principalId: string | null; // 负责人ID
  principalName: string; // 负责人名称
  status: number; // 部门状态 0:正常 1:禁用
  seq: number; // 排序
}

export interface AddDeptDto {
  parentId?: string | null; // 父级部门 NULL为顶级
  name: string; // 部门名
  principalId?: string | null; // 负责人ID
  status: number; // 部门状态 0:正常 1:禁用
  seq: number; // 排序
}

export interface EditDeptDto {
  id: string; // 部门ID
  parentId?: string | null; // 父级部门 NULL为顶级
  name: string; // 部门名
  principalId?: string | null; // 负责人ID
  status: number; // 部门状态 0:正常 1:禁用
  seq: number; // 排序
}

export default {
  /**
   * 获取部门树
   */
  getDeptTree: async (): Promise<GetDeptTreeVo[]> => {
    var result = await Http.postEntity<Result<GetDeptTreeVo[]>>("/dept/getDeptTree", {});
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取部门列表
   */
  getDeptList: async (dto: GetDeptListDto): Promise<PageResult<GetDeptListVo>> => {
    return await Http.postEntity<PageResult<GetDeptListVo>>("/dept/getDeptList", dto);
  },

  /**
   * 获取部门详情
   */
  getDeptDetails: async (dto: CommonIdDto): Promise<GetDeptDetailsVo> => {
    var result = await Http.postEntity<Result<GetDeptDetailsVo>>("/dept/getDeptDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增部门
   */
  addDept: async (dto: AddDeptDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/dept/addDept", dto);
  },

  /**
   * 编辑部门
   */
  editDept: async (dto: EditDeptDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/dept/editDept", dto);
  },

  /**
   * 删除部门
   */
  removeDept: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/dept/removeDept", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
