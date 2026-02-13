import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

/**
 * 查询用户通知记录列表DTO
 */
export interface GetUserNoticeRcdListDto extends PageQuery {}

/**
 * 用户通知记录列表VO
 */
export interface GetUserNoticeRcdListVo {
  id: string; // RCD主键ID
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  priority: number; // 优先级: 0:低 1:中 2:高
  category: string; // 业务类型/分类
  senderName: string; // 发送人姓名
  forward: string; // 跳转URL/路由地址
  params: string; // 动态参数 (JSON格式)
  createTime: string; // 下发时间
}

/**
 * 用户通知记录详情VO
 */
export interface GetNoticeRcdDetailsVo {
  id: string; // RCD主键ID
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  content: string; // 通知内容
  priority: number; // 优先级: 0:低 1:中 2:高
  category: string; // 业务类型/分类
  senderName: string; // 发送人姓名
  forward: string; // 跳转URL/路由地址
  params: string; // 动态参数 (JSON格式)
  createTime: string; // 创建时间
}

export default {
  /**
   * 获取当前用户未读通知数量
   */
  getUserNoticeCount: async (): Promise<number> => {
    const result = await Http.postEntity<Result<number>>("/noticeRcd/getUserNoticeCount", {});
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 阅读用户通知记录
   */
  readUserNoticeRcd: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/noticeRcd/readNoticeRcd", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 查询用户通知记录列表
   */
  getUserNoticeRcdList: async (dto: GetUserNoticeRcdListDto): Promise<PageResult<GetUserNoticeRcdListVo>> => {
    return await Http.postEntity<PageResult<GetUserNoticeRcdListVo>>("/noticeRcd/getNoticeRcdList", dto);
  },

  /**
   * 查询用户通知记录详情
   */
  getUserNoticeRcdDetails: async (dto: CommonIdDto): Promise<GetNoticeRcdDetailsVo> => {
    const result = await Http.postEntity<Result<GetNoticeRcdDetailsVo>>("/noticeRcd/getUserNoticeRcdDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 删除用户通知记录
   */
  removeNoticeRcd: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/noticeRcd/removeNoticeRcd", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
