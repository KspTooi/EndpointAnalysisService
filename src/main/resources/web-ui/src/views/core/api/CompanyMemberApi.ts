import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

export interface GetCompanyMemberListDto extends PageQuery {
  companyId: string | null; // 公司ID
  username?: string | null; // 用户名称 模糊查询
  role?: number | null; // 职务 0:CEO 1:成员
}

export interface GetCompanyMemberListVo {
  id?: string | null; // 记录ID
  companyId?: string | null; // 公司ID
  userId?: string | null; // 用户ID
  username?: string | null; // 用户名称
  role?: number | null; // 职务 0:CEO 1:成员
  joinedTime?: string | null; // 加入时间
}

export interface AddCompanyMemberDto {
  companyId?: string | null; // 公司ID
  userId?: string | null; // 用户ID
  role?: number | null; // 职务 0:CEO 1:成员
}

export interface FireCompanyMemberDto {
  companyId?: string | null; // 公司ID
  userId?: string | null; // 用户ID
}

export interface EditCompanyMemberDto {
  id?: string | null; // 记录ID
  role?: number | null; // 职务 0:CEO 1:成员
}

export interface GetCompanyMemberDetailsVo {
  id?: string | null; // 记录ID
  companyId?: string | null; // 公司ID
  userId?: string | null; // 用户ID
  role?: number | null; // 职务 0:CEO 1:成员
  joinedTime?: string | null; // 加入时间
}

export interface GetCurrentUserActiveCompanyMemberListDto extends PageQuery {
  username?: string | null; // 用户名称 模糊查询
  role?: number | null; // 职务 0:CEO 1:成员
}

export interface GetCurrentUserActiveCompanyMemberListVo {
  companyName?: string | null; // 公司名称
  companyId?: string | null; // 公司ID
  members?: PageResult<GetCompanyMemberListVo> | null; // 成员列表
}

export default {
  /**
   * 获取公司成员列表
   */
  getCompanyMemberList: async (dto: GetCompanyMemberListDto): Promise<PageResult<GetCompanyMemberListVo>> => {
    return await Http.postEntity<PageResult<GetCompanyMemberListVo>>("/companyMember/getCompanyMemberList", dto);
  },

  /**
   * 获取当前用户激活的公司成员列表
   */
  getCurrentUserActiveCompanyMemberList: async (dto: GetCurrentUserActiveCompanyMemberListDto): Promise<Result<GetCurrentUserActiveCompanyMemberListVo>> => {
    return await Http.postEntity<Result<GetCurrentUserActiveCompanyMemberListVo>>("/companyMember/getCurrentUserActiveCompanyMemberList", dto);
  },

  /**
   * 新增公司成员
   */
  addCompanyMember: async (dto: AddCompanyMemberDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/companyMember/addCompanyMember", dto);
  },

  /**
   * 编辑公司成员
   */
  editCompanyMember: async (dto: EditCompanyMemberDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/companyMember/editCompanyMember", dto);
  },

  /**
   * 获取公司成员详情
   */
  getCompanyMemberDetails: async (dto: CommonIdDto): Promise<Result<GetCompanyMemberDetailsVo>> => {
    return await Http.postEntity<Result<GetCompanyMemberDetailsVo>>("/companyMember/getCompanyMemberDetails", dto);
  },

  /**
   * 删除公司成员
   */
  removeCompanyMember: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/companyMember/removeCompanyMember", dto);
  },

  /**
   * 开除成员
   */
  fireCompanyMember: async (dto: FireCompanyMemberDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/companyMember/fireCompanyMember", dto);
  },
};
