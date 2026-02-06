import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetNoticeListDto extends PageQuery {
  title?: string; // 标题
  kind?: number; // 种类: 0公告, 1业务提醒, 2私信
  content?: string; // 通知内容
  priority?: number; // 优先级: 0:低 1:中 2:高
  category?: string; // 业务类型/分类
  senderName?: string; // 发送人姓名
}

/**
 * 列表VO
 */
export interface GetNoticeListVo {
  id: number; // 主键ID
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  priority: number; // 优先级: 0:低 1:中 2:高
  category: string; // 业务类型/分类
  senderName: string; // 发送人姓名
}

/**
 * 详情VO
 */
export interface GetNoticeDetailsVo {
  id: number; // 主键ID
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  content: string; // 通知内容
  priority: number; // 优先级: 0:低 1:中 2:高
  category: string; // 业务类型/分类
  senderId: number; // 发送人ID (NULL为系统)
  senderName: string; // 发送人姓名
  forward: string; // 跳转URL/路由地址
  params: string; // 动态参数 (JSON格式)
  createTime: string; // 创建时间
}

/**
 * 新增DTO
 */
export interface AddNoticeDto {
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  content?: string; // 通知内容
  priority: number; // 优先级: 0:低 1:中 2:高
  category?: string; // 业务类型/分类
}

/**
 * 编辑DTO
 */
export interface EditNoticeDto {
  id: number; // 主键ID
  title: string; // 标题
  kind: number; // 种类: 0公告, 1业务提醒, 2私信
  content?: string; // 通知内容
  priority: number; // 优先级: 0:低 1:中 2:高
  category?: string; // 业务类型/分类
}

export default {
  /**
   * 获取消息表列表
   */
  getNoticeList: async (dto: GetNoticeListDto): Promise<PageResult<GetNoticeListVo>> => {
    return await Http.postEntity<PageResult<GetNoticeListVo>>("/notice/getNoticeList", dto);
  },

  /**
   * 获取消息表详情
   */
  getNoticeDetails: async (dto: CommonIdDto): Promise<GetNoticeDetailsVo> => {
    const result = await Http.postEntity<Result<GetNoticeDetailsVo>>("/notice/getNoticeDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增消息表
   */
  addNotice: async (dto: AddNoticeDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/notice/addNotice", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑消息表
   */
  editNotice: async (dto: EditNoticeDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/notice/editNotice", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除消息表
   */
  removeNotice: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/notice/removeNotice", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
