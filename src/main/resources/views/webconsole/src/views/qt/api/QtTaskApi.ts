import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetQtTaskListDto extends PageQuery {
  groupId?: string; // 任务分组ID
  name?: string; // 任务名
  status?: number; // 0:正常 1:暂停
}

/**
 * 列表VO
 */
export interface GetQtTaskListVo {
  id: string; // 任务ID
  groupName: string; // 任务分组名
  name: string; // 任务名
  kind: number; // 0:本地BEAN 1:远程HTTP
  cron: string; // CRON表达式
  target: string; // 调用目标(BEAN代码或HTTP地址)
  expireTime: string; // 任务有效期截止
  status: number; // 0:正常 1:暂停
  createTime: string; // 创建时间
}

/**
 * 详情VO
 */
export interface GetQtTaskDetailsVo {
  id: string; // 任务ID
  groupId: string; // 任务分组ID
  groupName: string; // 任务分组名
  name: string; // 任务名
  kind: number; // 0:本地BEAN 1:远程HTTP
  cron: string; // CRON表达式
  target: string; // 调用目标(BEAN代码或HTTP地址)
  targetParam: string; // 调用参数JSON
  reqMethod: string; // 请求方法
  concurrent: number; // 并发执行 0:允许 1:禁止
  misfirePolicy: number; // 过期策略 0:放弃执行 1:立即执行 2:全部执行
  expireTime: string; // 任务有效期截止
  status: number; // 0:正常 1:暂停
  createTime: string; // 创建时间
}

/**
 * 新增DTO
 */
export interface AddQtTaskDto {
  groupId: string; // 任务分组ID
  name: string; // 任务名
  kind: number; // 0:本地BEAN 1:远程HTTP
  cron: string; // CRON表达式
  target: string; // 调用目标(BEAN代码或HTTP地址)
  targetParam: string; // 调用参数JSON
  reqMethod: string; // 请求方法
  concurrent: number; // 并发执行 0:允许 1:禁止
  misfirePolicy: number; // 过期策略 0:放弃执行 1:立即执行 2:全部执行
  expireTime: string; // 任务有效期截止
  status: number; // 0:正常 1:暂停
}

/**
 * 编辑DTO
 */
export interface EditQtTaskDto {
  id: string; // 任务ID
  groupId: string; // 任务分组ID
  groupName: string; // 任务分组名
  name: string; // 任务名
  kind: number; // 0:本地BEAN 1:远程HTTP
  cron: string; // CRON表达式
  target: string; // 调用目标(BEAN代码或HTTP地址)
  targetParam: string; // 调用参数JSON
  reqMethod: string; // 请求方法
  concurrent: number; // 并发执行 0:允许 1:禁止
  misfirePolicy: number; // 过期策略 0:放弃执行 1:立即执行 2:全部执行
  expireTime: string; // 任务有效期截止
  status: number; // 0:正常 1:暂停
}

export default {
  /**
   * 获取任务调度表列表
   */
  getQtTaskList: async (dto: GetQtTaskListDto): Promise<PageResult<GetQtTaskListVo>> => {
    return await Http.postEntity<PageResult<GetQtTaskListVo>>("/qtTask/getQtTaskList", dto);
  },

  /**
   * 获取任务调度表详情
   */
  getQtTaskDetails: async (dto: CommonIdDto): Promise<GetQtTaskDetailsVo> => {
    const result = await Http.postEntity<Result<GetQtTaskDetailsVo>>("/qtTask/getQtTaskDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增任务调度表
   */
  addQtTask: async (dto: AddQtTaskDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTask/addQtTask", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑任务调度表
   */
  editQtTask: async (dto: EditQtTaskDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTask/editQtTask", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除任务调度表
   */
  removeQtTask: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTask/removeQtTask", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
