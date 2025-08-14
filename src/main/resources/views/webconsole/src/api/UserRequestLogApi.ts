import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type PageQuery from "@/commons/entity/PageQuery";
import type PageResult from "@/commons/entity/PageResult";
import Http from "@/commons/Http";

export interface GetUserRequestLogListDto extends PageQuery { 
    userRequestId: string | null
}
    
export interface GetUserRequestLogListVo { 
    id: string
    requestId: string
    method: string
    url: string
    source: string
    status: number
    statusCode: number
    requestTime: string
    responseTime: string
}


export default {

    /**
     * 获取用户请求日志列表
     * @param dto 查询条件
     * @returns 用户请求日志列表
     */
    getUserRequestLogList: async (dto: GetUserRequestLogListDto): Promise<PageResult<GetUserRequestLogListVo>> => {
        return await Http.postEntity<PageResult<GetUserRequestLogListVo>>('/userRequestLog/getUserRequestLogList', dto)
    }
}
        