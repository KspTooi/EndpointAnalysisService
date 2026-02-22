import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetNoticeTemplateListDto extends PageQuery {
  name?: string; // 模板名称
  code?: string; // 模板唯一编码 (业务调用用)
  content?: string; // 模板内容 (含占位符)
  status?: number; // 状态: 0启用, 1禁用
  remark?: string; // 备注
}

/**
 * 列表VO
 */
export interface GetNoticeTemplateListVo {
  id: string; // 主键ID
  name: string; // 模板名称
  code: string; // 模板唯一编码 (业务调用用)
  status: number; // 状态: 0启用, 1禁用
  remark: string; // 备注
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
}

/**
 * 详情VO
 */
export interface GetNoticeTemplateDetailsVo {
  id: string; // 主键ID
  name: string; // 模板名称
  code: string; // 模板唯一编码 (业务调用用)
  content: string; // 模板内容 (含占位符)
  status: number; // 状态: 0启用, 1禁用
  remark: string; // 备注
}

/**
 * 新增DTO
 */
export interface AddNoticeTemplateDto {
  name: string; // 模板名称
  code: string; // 模板唯一编码 (业务调用用)
  content: string; // 模板内容 (含占位符)
  status: number; // 状态: 0启用, 1禁用
  remark: string; // 备注
}

/**
 * 编辑DTO
 */
export interface EditNoticeTemplateDto {
  id: string; // 主键ID
  name: string; // 模板名称
  code: string; // 模板唯一编码 (业务调用用)
  content: string; // 模板内容 (含占位符)
  status: number; // 状态: 0启用, 1禁用
  remark: string; // 备注
}

export default {
  /**
   * 获取通知模板表列表
   */
  getNoticeTemplateList: async (dto: GetNoticeTemplateListDto): Promise<PageResult<GetNoticeTemplateListVo>> => {
    return await Http.postEntity<PageResult<GetNoticeTemplateListVo>>("/noticeTemplate/getNoticeTemplateList", dto);
  },

  /**
   * 获取通知模板表详情
   */
  getNoticeTemplateDetails: async (dto: CommonIdDto): Promise<GetNoticeTemplateDetailsVo> => {
    const result = await Http.postEntity<Result<GetNoticeTemplateDetailsVo>>("/noticeTemplate/getNoticeTemplateDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增通知模板表
   */
  addNoticeTemplate: async (dto: AddNoticeTemplateDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/noticeTemplate/addNoticeTemplate", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑通知模板表
   */
  editNoticeTemplate: async (dto: EditNoticeTemplateDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/noticeTemplate/editNoticeTemplate", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除通知模板表
   */
  removeNoticeTemplate: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/noticeTemplate/removeNoticeTemplate", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
