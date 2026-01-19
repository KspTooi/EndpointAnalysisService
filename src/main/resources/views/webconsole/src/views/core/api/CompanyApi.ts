import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

export interface GetCurrentUserCompanyListDto extends PageQuery {
  name?: string | null; // 公司名
}

export interface GetCurrentUserCompanyListVo {
  id?: string | null; // 公司ID
  founderName?: string | null; // 创始人名称
  ceoName?: string | null; // 现任CEO名称
  name?: string | null; // 公司名
  description?: string | null; // 公司描述
  memberCount?: number | null; // 成员数量
  createTime?: string | null; // 创建时间
  updateTime?: string | null; // 更新时间
  isActive?: number | null; // 当前用户是否已激活 0:否 1:是
  isCeo?: number | null; // 当前用户是否是该公司CEO 0:否 1:是
}

export interface AddCompanyDto {
  name?: string | null; // 公司名
  description?: string | null; // 公司描述
}

export interface EditCompanyDto {
  id?: string | null; // 公司ID
  name?: string | null; // 公司名
  description?: string | null; // 公司描述
}

export interface GetCompanyDetailsVo {
  id?: string | null; // 公司ID
  name?: string | null; // 公司名
  description?: string | null; // 公司描述
  createTime?: string | null; // 创建时间
  updateTime?: string | null; // 更新时间
}

export interface ResignCeoDto {
  companyId?: string | null; // 公司ID
  newCeoUserId?: string | null; // CEO移交用户ID
}

export default {
  /**
   * 获取我的/我加入的团队列表
   */
  getCurrentUserCompanyList: async (dto: GetCurrentUserCompanyListDto): Promise<PageResult<GetCurrentUserCompanyListVo>> => {
    return await Http.postEntity<PageResult<GetCurrentUserCompanyListVo>>("/company/getCurrentUserCompanyList", dto);
  },

  /**
   * 退出团队
   */
  leaveCompany: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/leaveCompany", dto);
  },

  /**
   * 新增团队
   */
  addCompany: async (dto: AddCompanyDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/addCompany", dto);
  },

  /**
   * 编辑团队
   */
  editCompany: async (dto: EditCompanyDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/editCompany", dto);
  },

  /**
   * 获取团队详情
   */
  getCompanyDetails: async (dto: CommonIdDto): Promise<Result<GetCompanyDetailsVo>> => {
    return await Http.postEntity<Result<GetCompanyDetailsVo>>("/company/getCompanyDetails", dto);
  },

  /**
   * 删除团队
   */
  removeCompany: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/removeCompany", dto);
  },

  /**
   * 激活团队
   */
  activateCompany: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/activateCompany", dto);
  },

  /**
   * 辞去CEO职位
   */
  resignCEO: async (dto: ResignCeoDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/company/resignCEO", dto);
  },
};
