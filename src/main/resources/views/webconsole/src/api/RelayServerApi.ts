import type PageQuery from "@/commons/entity/PageQuery.ts";
import Http from "@/commons/Http.ts";
import type RestPageableView from "@/commons/entity/RestPageableView.ts";
import type Result from "@/commons/entity/Result.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";

export interface GetRelayServerListDto extends PageQuery {
    name: string | null; //中继服务器名称
    forwardUrl: string | null; //桥接目标URL
}

export interface GetRelayServerListVo {
    id: string; //中继服务器ID
    name: string; //中继服务器名称
    host: string; //中继服务器主机
    port: number; //中继服务器端口
    forwardUrl: string; //桥接目标URL
    autoStart: number; //自动运行 0:否 1:是
    status: number; //中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败
}

export interface AddRelayServerDto {
    name: string | null; //中继服务器名称
    host: string | null; //中继服务器主机
    port: number | null; //中继服务器端口
    forwardUrl: string | null; //桥接目标URL
    autoStart: number | null; //自动运行 0:否 1:是
    overrideRedirect: number | null; //覆盖桥接目标的重定向 0:否 1:是
    overrideRedirectUrl: string | null; //覆盖桥接目标的重定向URL
    requestIdStrategy: number | null; //请求ID策略 0:随机生成 1:从请求头获取
    requestIdHeaderName: string | null; //请求ID头名称
    bizErrorStrategy: number | null; //业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定
    bizErrorCodeField: string | null; //业务错误码字段(JSONPath)
    bizSuccessCodeValue: string | null; //业务成功码值(正确时返回的值)
}

export interface EditRelayServerDto {
    id: string | null; //中继服务器ID
    name: string | null; //中继服务器名称
    host: string | null; //中继服务器主机
    port: number | null; //中继服务器端口
    forwardUrl: string | null; //桥接目标URL
    autoStart: number | null; //自动运行 0:否 1:是
    overrideRedirect: number | null; //覆盖桥接目标的重定向 0:否 1:是
    overrideRedirectUrl: string | null; //覆盖桥接目标的重定向URL
    requestIdStrategy: number | null; //请求ID策略 0:随机生成 1:从请求头获取
    requestIdHeaderName: string | null; //请求ID头名称
    bizErrorStrategy: number | null; //业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定
    bizErrorCodeField: string | null; //业务错误码字段(JSONPath)
    bizSuccessCodeValue: string | null; //业务成功码值(正确时返回的值)
}

export interface GetRelayServerDetailsVo {
    id: string | null; //中继服务器ID
    name: string | null; //中继服务器名称
    host: string | null; //中继服务器主机
    port: number | null; //中继服务器端口
    forwardUrl: string | null; //桥接目标URL
    autoStart: number | null; //自动运行 0:否 1:是
    status: number | null; //中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败
    errorMessage: string | null; //启动失败原因
    overrideRedirect: number | null; //覆盖桥接目标的重定向 0:否 1:是
    overrideRedirectUrl: string | null; //覆盖桥接目标的重定向URL
    requestIdStrategy: number | null; //请求ID策略 0:随机生成 1:从请求头获取
    requestIdHeaderName: string | null; //请求ID头名称
    bizErrorStrategy: number | null; //业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定
    bizErrorCodeField: string | null; //业务错误码字段(JSONPath)
    bizSuccessCodeValue: string | null; //业务成功码值(正确时返回的值)
    createTime: string | null; //创建时间
}

export default {
    
    /**
     * 获取中继服务器列表
     * @param dto 查询条件
     * @returns 中继服务器列表
     */
    getRelayServerList: async (dto: GetRelayServerListDto): Promise<RestPageableView<GetRelayServerListVo>> => {
        const ret = await Http.postEntity<RestPageableView<GetRelayServerListVo>>('/relayServer/getRelayServerList', dto);
        return ret;
    },

    /**
     * 添加中继服务器
     * @param dto 中继服务器信息
     * @returns 中继服务器ID
     */
    addRelayServer: async (dto: AddRelayServerDto): Promise<Result<void>> => {
        return await Http.postEntity<Result<void>>('/relayServer/addRelayServer', dto);
    },

    /**
     * 编辑中继服务器
     * @param dto 中继服务器信息
     * @returns 中继服务器ID
     */
    editRelayServer: async (dto: EditRelayServerDto): Promise<Result<void>> => {
        return await Http.postEntity<Result<void>>('/relayServer/editRelayServer', dto);
    },

    /**
     * 获取中继服务器详情
     * @param id 中继服务器ID
     * @returns 中继服务器详情
     */
    getRelayServerDetails: async (id: string): Promise<GetRelayServerDetailsVo> => {
        const ret = await Http.postEntity<Result<GetRelayServerDetailsVo>>('/relayServer/getRelayServerDetails', { id: id });
        return ret.data;
    },

    /**
     * 删除中继服务器
     * @param id 中继服务器ID
     * @returns 删除结果
     */
    removeRelayServer: async (id: string): Promise<Result<void>> => {
        return await Http.postEntity<Result<void>>('/relayServer/removeRelayServer', { id: id });
    },

    /**
     * 启动中继服务器
     * @param id 中继服务器ID
     * @returns 启动结果
     */
    startRelayServer: async (id: string): Promise<Result<void>> => {
        return await Http.postEntity<Result<void>>('/relayServer/startRelayServer', { id: id });
    },

    /**
     * 停止中继服务器
     * @param id 中继服务器ID
     * @returns 停止结果
     */
    stopRelayServer: async (id: string): Promise<Result<void>> => {
        return await Http.postEntity<Result<void>>('/relayServer/stopRelayServer', { id: id });
    },
}
