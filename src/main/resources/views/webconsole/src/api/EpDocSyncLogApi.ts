
import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result";

export interface GetEpDocSyncLogListDto extends PageQuery {
    epDocId: string;
}

export interface GetEpDocSyncLogListVo {
    id: string;
    epDocId: string;
    hash: string;
    pullUrl: string;
    status: string;
    createTime: string;
}

export default {
    
    /**
     * 获取端点文档拉取记录列表
     */
    getEpSyncLogList: async (dto: GetEpDocSyncLogListDto): Promise<PageResult<GetEpDocSyncLogListVo>> => {
        return await Http.postEntity<PageResult<GetEpDocSyncLogListVo>>('/epdocSyncLog/getEpSyncLogList', dto);
    },

}
