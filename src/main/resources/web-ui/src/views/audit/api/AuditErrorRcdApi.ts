import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetAuditErrorRcdListDto extends PageQuery {
  errorCode?: string; // 错误代码
  requestUri?: string; // 请求地址
  userId?: string; // 操作人ID
  userName?: string; // 操作人用户名
  errorType?: string; // 异常类型
  errorMessage?: string; // 异常简述
}

/**
 * 列表VO
 */
export interface GetAuditErrorRcdListVo {
  id: string; // 错误ID
  errorCode: string; // 错误代码
  requestUri: string; // 请求地址
  userName: string; // 操作人用户名
  errorType: string; // 异常类型
  createTime: string; // 创建时间
}

/**
 * 详情VO
 */
export interface GetAuditErrorRcdDetailsVo {
  id: string; // 错误ID
  errorCode: string; // 错误代码
  requestUri: string; // 请求地址
  userId: string; // 操作人ID
  userName: string; // 操作人用户名
  errorType: string; // 异常类型
  errorMessage: string; // 异常简述
  errorStackTrace: string; // 完整堆栈信息
  createTime: string; // 创建时间
}

export default {
  /**
   * 获取系统错误记录列表
   */
  getAuditErrorRcdList: async (dto: GetAuditErrorRcdListDto): Promise<PageResult<GetAuditErrorRcdListVo>> => {
    return await Http.postEntity<PageResult<GetAuditErrorRcdListVo>>("/auditErrorRcd/getAuditErrorRcdList", dto);
  },

  /**
   * 获取系统错误记录详情
   */
  getAuditErrorRcdDetails: async (dto: CommonIdDto): Promise<GetAuditErrorRcdDetailsVo> => {
    const result = await Http.postEntity<Result<GetAuditErrorRcdDetailsVo>>("/auditErrorRcd/getAuditErrorRcdDetails", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 删除系统错误记录
   */
  removeAuditErrorRcd: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/auditErrorRcd/removeAuditErrorRcd", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
