import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetPostListDto extends PageQuery {
  name?: string; // 岗位名称
  code?: string; // 岗位编码
  seq?: number; // 岗位排序
}

/**
 * 列表VO
 */
export interface GetPostListVo {
  id: string; // 岗位ID
  name: string; // 岗位名称
  code: string; // 岗位编码
  seq: number; // 岗位排序
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
  deleteTime: string; // 删除时间 NULL未删
}

/**
 * 详情VO
 */
export interface GetPostDetailsVo {
  id: string; // 岗位ID
  name: string; // 岗位名称
  code: string; // 岗位编码
  seq: number; // 岗位排序
  createTime: string; // 创建时间
  creatorId: string; // 创建人ID
  updateTime: string; // 更新时间
  updaterId: string; // 更新人ID
  deleteTime: string; // 删除时间 NULL未删
}

/**
 * 新增DTO
 */
export interface AddPostDto {
  name: string; // 岗位名称
  code: string; // 岗位编码
  seq: number; // 岗位排序
}

/**
 * 编辑DTO
 */
export interface EditPostDto {
  id: string; // 岗位ID
  name: string; // 岗位名称
  code: string; // 岗位编码
  seq: number; // 岗位排序
}

export default {
  /**
   * 获取岗位表列表
   */
  getPostList: async (dto: GetPostListDto): Promise<PageResult<GetPostListVo>> => {
    return await Http.postEntity<PageResult<GetPostListVo>>("/post/getPostList", dto);
  },

  /**
   * 获取岗位表详情
   */
  getPostDetails: async (dto: CommonIdDto): Promise<GetPostDetailsVo> => {
    const result = await Http.postEntity<Result<GetPostDetailsVo>>("/post/getPostDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增岗位表
   */
  addPost: async (dto: AddPostDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/post/addPost", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑岗位表
   */
  editPost: async (dto: EditPostDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/post/editPost", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除岗位表
   */
  removePost: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/post/removePost", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
