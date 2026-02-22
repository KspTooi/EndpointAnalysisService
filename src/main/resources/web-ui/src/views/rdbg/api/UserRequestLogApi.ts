import type Result from "@/commons/entity/Result.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import Http from "@/commons/Http.ts";

export interface GetUserRequestLogListDto extends PageQuery {
  userRequestId: string | null;
}

export interface GetUserRequestLogListVo {
  id: string;
  requestId: string;
  method: string;
  url: string;
  source: string;
  status: number;
  statusCode: number;
  requestTime: string;
  responseTime: string;
}

export interface GetUserRequestLogDetailsVo {
  id: string | null;
  requestId: string | null;
  method: string | null;
  url: string | null;
  source: string | null;
  requestHeaders: HttpHeaderVo[] | null;
  requestBodyLength: number | null;
  requestBodyType: string | null;
  requestBody: string | null;
  responseHeaders: HttpHeaderVo[] | null;
  responseBodyLength: number | null;
  responseBodyType: string | null;
  responseBody: string | null;
  statusCode: number | null;
  redirectUrl: string | null;
  status: number | null;
  requestTime: string | null;
  responseTime: string | null;
}

export interface HttpHeaderVo {
  k: string;
  v: string;
}

export default {
  /**
   * 获取用户请求日志列表
   * @param dto 查询条件
   * @returns 用户请求日志列表
   */
  getUserRequestLogList: async (dto: GetUserRequestLogListDto): Promise<PageResult<GetUserRequestLogListVo>> => {
    return await Http.postEntity<PageResult<GetUserRequestLogListVo>>("/userRequestLog/getUserRequestLogList", dto);
  },

  /**
   * 获取用户请求详情
   * @param id 用户请求ID
   * @returns 用户请求详情
   */
  getUserRequestDetails: async (id: string): Promise<GetUserRequestLogDetailsVo> => {
    const result = await Http.postEntity<Result<GetUserRequestLogDetailsVo>>("/userRequestLog/getUserRequestLogDetails", { id });
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取最新的用户请求详情
   * @param id 用户请求ID
   * @returns 用户请求详情
   */
  getLastUserRequestLogDetails: async (id: string): Promise<GetUserRequestLogDetailsVo> => {
    const result = await Http.postEntity<Result<GetUserRequestLogDetailsVo>>("/userRequestLog/getLastUserRequestLogDetails", { id });
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },
};
