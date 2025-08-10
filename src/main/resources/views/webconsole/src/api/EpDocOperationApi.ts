import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";


export interface GetEpDocOperationTagListDto {
    epDocId: number; // 端点文档ID
    epDocVersionId: number | null; // 端点文档版本ID null为最新版本
}

export interface GetEpDocOperationTagListVo {
    tag: string; // 标签
    apiCount: number; // 接口数量
    operationDefineList: GetEpDocOperationTagListOperationDefineVo[]; // 接口定义
}

export interface GetEpDocOperationTagListOperationDefineVo {
    id: number; // 接口ID
    name: string; // 接口名称
    method: string; // 请求方法
}


export interface GetEpDocOperationDetailsVo {
    id: number; // 接口ID
    path: string; // 接口路径
    method: string; // 请求方法
    summary: string; // 接口摘要
    description: string; // 接口描述
    operationId: string; // 唯一操作ID
    reqBody: BodySchema; // 请求体
    resBody: BodySchema; // 响应体
}

export interface BodySchema {
    schemaName: string; // 对象名称
    params: BodySchemaParam[]; // 对象属性列表
}

export interface BodySchemaParam {
    name: string; // 参数名
    type: string; // 参数类型 integer string boolean object array
    description: string; // 参数描述
    defaultValue: string; // 参数默认值
    required: boolean; // 参数是否必填
    isArray: boolean; // 参数是否为数组
    schema: BodySchema; // 参数嵌套对象 为null代表是简单对象 否则是嵌套对象
}

export default {
    /**
     * 获取端点文档接口标签列表
     */
    getEpDocOperationTagList: async (dto: GetEpDocOperationTagListDto): Promise<GetEpDocOperationTagListVo[]> => {
        var result = await Http.postEntity<Result<GetEpDocOperationTagListVo[]>>('/epdoc/getEpDocOperationTagList', dto);
        if (result.code == 0) {
            return result.data;
        }
        throw new Error(result.message);
    },

    /**
     * 获取端点文档接口详情
     */
    getEpDocOperationDetails: async (dto: CommonIdDto): Promise<GetEpDocOperationDetailsVo> => {
        var result = await Http.postEntity<Result<GetEpDocOperationDetailsVo>>('/epdoc/getEpDocOperationDetails', dto);
        if (result.code == 0) {
            return result.data;
        }
        throw new Error(result.message);
    },
}



