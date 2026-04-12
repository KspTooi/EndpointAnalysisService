import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/model/PageResult.ts";
import type CommonIdDto from "@/commons/model/CommonIdDto.ts";
import type PageQuery from "@/commons/model/PageQuery.ts";
import type Result from "@/commons/model/Result.ts";
import type CustomizeTagJson from "@/commons/model/json/CustomizeTagJson";

/**
 * 查询提示词列表Dto
 */
export interface GetPromptListDto extends PageQuery {
  name?: string; // 名称
  tags?: string; // 标签(CTJ)
}

/**
 * 查询提示词列表Vo
 */
export interface GetPromptListVo {
  id: string; // 主键ID
  name: string; // 名称
  tags: string; // 标签(CTJ)
  paramCount: number; // 参数数量
  version: number; // 版本号
  createTime: string; // 创建时间
}

/**
 * 查询提示词详情Vo
 */
export interface GetPromptDetailsVo {
  id: string; // 主键ID
  name: string; // 名称
  tags: CustomizeTagJson[]; // 标签(CTJ)
  content: string; // 内容
}

/**
 * 新增提示词Dto
 */
export interface AddPromptDto {
  name: string; // 名称
  tags: CustomizeTagJson[]; // 标签(CTJ)
  content: string; // 内容
}

/**
 * 编辑提示词Dto
 */
export interface EditPromptDto {
  id: string; // 主键ID
  name: string; // 名称
  tags: CustomizeTagJson[]; // 标签(CTJ)
  content: string; // 内容
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
