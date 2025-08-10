import type PageQuery from "@/commons/entity/PageQuery";
import Http from "@/commons/Http";
import type RestPageableView from "@/commons/entity/RestPageableView";
import type Result from "@/commons/entity/Result";


export interface GetOriginRequestDto {
    requestId: string | null;
}

export interface GetOriginRequestVo {
    id: string; //数据ID
    requestId: string; //请求ID
    method: string; //请求方法
    url: string; //请求URL
    source: string; //来源
    status: number; //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    statusCode: number; //HTTP响应状态码
    requestTime: string; //发起请求时间
    responseTime: string; //响应时间
}

export interface GetReplayRequestListDto extends PageQuery{
    originRequestId: string | null; //原始请求ID
    relayServerId: string | null; //中继通道ID
    requestId: string | null; //请求ID
    method: string | null; //请求方法
    url: string | null; //请求URL
    source: string | null; //来源
    status: number | null; //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
}

export interface GetReplayRequestListVo {
    data: GetReplayRequestListVo[] | { id: string; requestId: string; method: string; url: string; source: string; status: number; statusCode: number; requestTime: string; responseTime: string; }[];
    total: number;
    id: string; //数据ID
    requestId: string; //请求ID
    method: string; //请求方法
    url: string; //请求URL
    source: string; //来源
    status: number; //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    statusCode: number; //HTTP响应状态码
    requestTime: string; //发起请求时间
    responseTime: string; //响应时间
}

export interface GetReplayRequestDetailsVo {
    id: string; //数据ID
    requestId: string; //请求ID
    method: string; //请求方法
    url: string; //请求URL
    source: string; //来源
    requestHeaders: string; //请求头
    requestBodyLength: number; //请求体长度
    requestBodyType: string; //请求体类型
    requestBody: string; //请求体
    responseHeaders: string; //响应头
    responseBodyLength: number; //响应体长度
    responseBodyType: string; //响应体类型
    responseBody: string; //响应体
    statusCode: number; //HTTP响应状态码
    redirectUrl: string; //重定向URL
    status: number; //状态 0:正常 1:HTTP失败 2:业务失败 3:连接超时
    requestTime: string; //发起请求时间
    responseTime: string; //响应时间
}

export default {

    replayRequest: async (requestId: string): Promise<void> => {
        await Http.postEntity<void>('/replayRequest/replayRequest', { requestId: requestId });
    },


    /**
     * 获取原始请求
     * @param dto 请求参数
     * @returns 
     */
    getOriginRequest: async (dto: GetOriginRequestDto): Promise<GetOriginRequestVo> => {
        const res = await Http.postEntity<Result<GetOriginRequestVo>>('/replayRequest/getOriginRequest', dto);
        if(res.code === 0){
            return res.data;
        }
        throw new Error(res.message);
    },

    /**
     * 获取回放请求列表
     * @param dto 请求参数
     * @returns 
     */
    getReplayRequestList: async (dto: GetReplayRequestListDto): Promise<RestPageableView<GetReplayRequestListVo>> => {
        return await Http.postEntity<RestPageableView<GetReplayRequestListVo>>('/replayRequest/getReplayRequestList', dto);
    },

    /**
     * 获取回放请求详情
     * @param id 请求ID
     * @returns 
     */
    getReplayRequestDetails: async (id: string): Promise<GetReplayRequestDetailsVo> => {
        const res = await Http.postEntity<Result<GetReplayRequestDetailsVo>>('/replayRequest/getReplayRequestDetails', { id: id });
        if(res.code === 0){
            return res.data;
        }
        throw new Error(res.message);
    },
}
