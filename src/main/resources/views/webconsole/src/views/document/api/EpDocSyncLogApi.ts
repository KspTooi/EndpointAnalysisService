
import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetEpDocSyncLogListDto extends PageQuery {
    epDocId: string; //端点文档ID
}

export interface GetEpDocSyncLogListVo {
    id: string; //记录ID
    epDocId: string; //端点文档ID
    hash: string; //文档MD5值
    pullUrl: string; //拉取地址
    newVersionCreated: string; //是否创建了新版本 0:否 1:是
    newVersionNum: string; //新版本号，如果创建了新版本，则记录新版本号
    status: string; //状态 0:成功 1:失败
    createTime: string; //拉取时间
}

export default {
    
    /**
     * 获取端点文档拉取记录列表
     */
    getEpSyncLogList: async (dto: GetEpDocSyncLogListDto): Promise<PageResult<GetEpDocSyncLogListVo>> => {
        return await Http.postEntity<PageResult<GetEpDocSyncLogListVo>>('/epdocSyncLog/getEpSyncLogList', dto);
    },

}
