import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetPromptListDto extends PageQuery {
  name?: String; // 名称
  tags?: String; // 标签(CTJ)
}

/**
 * 列表VO
 */
export interface GetPromptListVo {
  id: Long; // 主键ID
  name: String; // 名称
  tags: String; // 标签(CTJ)
  paramCount: String; // 参数数量
  version: String; // 版本号
  createTime: LocalDateTime; // 创建时间
}

/**
 * 详情VO
 */
export interface GetPromptDetailsVo {
  id: Long; // 主键ID
  name: String; // 名称
  tags: String; // 标签(CTJ)
  content: String; // 内容
}

/**
 * 新增DTO
 */
export interface AddPromptDto {
  name: String; // 名称
  tags: String; // 标签(CTJ)
  content: String; // 内容
}

/**
 * 编辑DTO
 */
export interface EditPromptDto {
  id: Long; // 主键ID
  name: String; // 名称
  tags: String; // 标签(CTJ)
  content: String; // 内容
}

export default {
  /**
   * 获取提示词列表
   */
  getPromptList: async (dto: GetPromptListDto): Promise<PageResult<GetPromptListVo>> => {
    return await Http.postEntity<PageResult<GetPromptListVo>>("/prompt/getPromptList", dto);
  },

  /**
   * 获取提示词详情
   */
  getPromptDetails: async (dto: CommonIdDto): Promise<GetPromptDetailsVo> => {
    const result = await Http.postEntity<Result<GetPromptDetailsVo>>("/prompt/getPromptDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增提示词
   */
  addPrompt: async (dto: AddPromptDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/prompt/addPrompt", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑提示词
   */
  editPrompt: async (dto: EditPromptDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/prompt/editPrompt", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除提示词
   */
  removePrompt: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/prompt/removePrompt", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
