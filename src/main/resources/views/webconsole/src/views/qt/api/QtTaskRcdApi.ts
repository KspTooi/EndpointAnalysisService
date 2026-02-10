import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetQtTaskRcdListDto extends PageQuery {
  id?: string; //调度日志ID
  taskId?: string; //任务ID
  taskName?: string; //任务名
  groupName?: string; //分组名
  target?: string; //调用目标
  targetParam?: string; //调用目标参数
  targetResult?: string; //调用目标返回内容(错误时为异常堆栈)
  status?: number; //运行状态 0:正常 1:失败 2:超时 3:已调度
  startTime?: string; //运行开始时间
  endTime?: string; //运行结束时间
  costTime?: number; //耗时(MS)
  createTime?: string; //创建时间
}

/**
 * 列表VO
 */
export interface GetQtTaskRcdListVo {
  id: string; //调度日志ID
  taskName: string; //任务名
  groupName: string; //分组名
  target: string; //调用目标
  status: number; //运行状态 0:正常 1:失败 2:超时 3:已调度
  startTime: string; //运行开始时间
  endTime: string; //运行结束时间
  costTime: number; //耗时(MS)
}

/**
 * 详情VO
 */
export interface GetQtTaskRcdDetailsVo {
  id: string; //调度日志ID
  taskId: string; //任务ID
  taskName: string; //任务名
  groupName: string; //分组名
  target: string; //调用目标
  targetParam: string; //调用目标参数
  targetResult: string; //调用目标返回内容(错误时为异常堆栈)
  status: number; //运行状态 0:正常 1:失败 2:超时 3:已调度
  startTime: string; //运行开始时间
  endTime: string; //运行结束时间
  costTime: number; //耗时(MS)
}

export default {
  /**
   * 获取任务调度日志表列表
   */
  getQtTaskRcdList: async (dto: GetQtTaskRcdListDto): Promise<PageResult<GetQtTaskRcdListVo>> => {
    return await Http.postEntity<PageResult<GetQtTaskRcdListVo>>("/qtTaskRcd/getQtTaskRcdList", dto);
  },

  /**
   * 获取任务调度日志表详情
   */
  getQtTaskRcdDetails: async (dto: CommonIdDto): Promise<GetQtTaskRcdDetailsVo> => {
    const result = await Http.postEntity<Result<GetQtTaskRcdDetailsVo>>("/qtTaskRcd/getQtTaskRcdDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 删除任务调度日志表（支持批量删除）
   */
  removeQtTaskRcd: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/qtTaskRcd/removeQtTaskRcd", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
