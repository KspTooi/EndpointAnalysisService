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
  forwardType: number; //桥接目标类型 0:直接 1:路由
  forwardUrl: string; //桥接目标URL
  autoStart: number; //自动运行 0:否 1:是
  status: number; //中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败
}

export interface AddRelayServerDto {
  name: string | null; //中继服务器名称
  host: string | null; //中继服务器主机
  port: number | null; //中继服务器端口
  forwardType: number | null; //桥接目标类型 0:直接 1:路由
  routeRules: RelayServerRouteRuleDto[] | null; //路由规则列表
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
  forwardType: number | null; //桥接目标类型 0:直接 1:路由
  routeRules: RelayServerRouteRuleDto[] | null; //路由规则列表
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
  forwardType: number | null; //桥接目标类型 0:直接 1:路由
  routeRules: RelayServerRouteRuleVo[] | null; //路由规则列表
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

export interface RelayServerRouteRuleDto {
  routeRuleId: string | null; //路由规则ID
  seq: number | null; //序号
}

export interface RelayServerRouteRuleVo {
  routeRuleId: string | null; //路由规则ID
  routeRuleName: string | null; //路由规则名称
  seq: number | null; //序号
}

export interface GetRelayServerRouteStateVo {
  targetHost: string | null; //目标主机
  targetPort: number | null; //目标端口
  hitCount: number | null; //流过该规则的请求数量
  isBreaked: number | null; //是否已熔断 0:否 1:是
}

export interface ResetRelayServerBreakerDto {
  id: string; //中继服务器ID
  host: string | null; //主机 不填写则重置所有主机的熔断状态
  port: number | null; //端口
  kind: number | null; //0:复位熔断 1:置为熔断
}

export default {
  /**
   * 获取中继服务器列表
   * @param dto 查询条件
   * @returns 中继服务器列表
   */
  getRelayServerList: async (dto: GetRelayServerListDto): Promise<RestPageableView<GetRelayServerListVo>> => {
    const ret = await Http.postEntity<RestPageableView<GetRelayServerListVo>>("/relayServer/getRelayServerList", dto);
    return ret;
  },

  /**
   * 添加中继服务器
   * @param dto 中继服务器信息
   * @returns 中继服务器ID
   */
  addRelayServer: async (dto: AddRelayServerDto): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/addRelayServer", dto);
  },

  /**
   * 编辑中继服务器
   * @param dto 中继服务器信息
   * @returns 中继服务器ID
   */
  editRelayServer: async (dto: EditRelayServerDto): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/editRelayServer", dto);
  },

  /**
   * 获取中继服务器详情
   * @param id 中继服务器ID
   * @returns 中继服务器详情
   */
  getRelayServerDetails: async (id: string): Promise<GetRelayServerDetailsVo> => {
    const ret = await Http.postEntity<Result<GetRelayServerDetailsVo>>("/relayServer/getRelayServerDetails", { id: id });
    return ret.data;
  },

  /**
   * 删除中继服务器
   * @param id 中继服务器ID
   * @returns 删除结果
   */
  removeRelayServer: async (id: string): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/removeRelayServer", { id: id });
  },

  /**
   * 启动中继服务器
   * @param id 中继服务器ID
   * @returns 启动结果
   */
  startRelayServer: async (id: string): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/startRelayServer", { id: id });
  },

  /**
   * 停止中继服务器
   * @param id 中继服务器ID
   * @returns 停止结果
   */
  stopRelayServer: async (id: string): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/stopRelayServer", { id: id });
  },

  /**
   * 获取中继服务器路由状态
   * @param id 中继服务器ID
   * @returns 中继服务器路由状态
   */
  getRelayServerRouteState: async (id: string): Promise<GetRelayServerRouteStateVo[]> => {
    const ret = await Http.postEntity<Result<GetRelayServerRouteStateVo[]>>("/relayServer/getRelayServerRouteState", { id: id });
    return ret.data;
  },

  /**
   * 重置中继服务器熔断器
   * @param dto 重置参数
   * @returns 重置结果
   */
  resetRelayServerBreaker: async (dto: ResetRelayServerBreakerDto): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/relayServer/resetRelayServerBreaker", dto);
  },
};
