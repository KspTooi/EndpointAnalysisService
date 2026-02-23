import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetSessionListDto extends PageQuery {
  userName: string | null; // 用户名
}

export interface GetSessionListVo {
  id: string; // 会话ID
  username: string; // 用户名
  createTime: string; // 登入时间
  rsMax: number; // 最大RowScope等级 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
  expiresAt: string; // 过期时间
}

export interface GetSessionDetailsVo {
  id: string; // 会话ID
  username: string; // 用户名
  createTime: string; // 登入时间
  expiresAt: string; // 过期时间
  permissions: string[]; // 权限节点
  rsMax: number; // 最大RowScope等级 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门
  rsDeptNames: string[]; // RowScope允许访问的部门名称列表
}

export default {
  /**
   * 获取会话列表
   */
  getSessionList: async (dto: GetSessionListDto): Promise<PageResult<GetSessionListVo>> => {
    return await Http.postEntity<PageResult<GetSessionListVo>>("/session/getSessionList", dto);
  },

  /**
   * 获取会话详情
   */
  getSessionDetails: async (dto: CommonIdDto): Promise<GetSessionDetailsVo> => {
    var result = await Http.postEntity<Result<GetSessionDetailsVo>>("/session/getSessionDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 关闭会话
   */
  closeSession: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/session/closeSession", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
