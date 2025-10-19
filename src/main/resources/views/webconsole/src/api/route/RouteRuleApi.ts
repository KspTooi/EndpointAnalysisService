import type PageResult from "@/commons/entity/PageResult";
import Http from "@/commons/Http";
import type Result from "@/commons/entity/Result";
import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type PageQuery from "@/commons/entity/PageQuery";

export interface GetRouteRuleListDto extends PageQuery {
  name: string | null; // 路由规则名
  matchType: number | null; // 匹配类型 0:全部 1:IP地址
  matchValue: string | null; // 匹配值
}

export interface GetRouteRuleListVo {
  id: string; // 路由规则ID
  name: string; // 路由规则名
  routeServerName: string; // 目标服务器名称
  matchType: number; // 匹配类型 0:全部 1:IP地址
  matchKey: string; // 匹配键
  matchOperator: number; // 匹配操作 0:等于
  matchValue: string; // 匹配值
  remark: string | null; // 策略描述
  updateTime: string; // 更新时间
}

export interface AddRouteRuleDto {
  name?: string | null; // 路由规则名
  matchType?: number | null; // 匹配类型 0:全部 1:IP地址
  matchKey?: string | null; // 匹配键
  matchOperator?: number | null; // 匹配操作 0:等于
  matchValue?: string | null; // 匹配值
  routeServerId?: string | null; // 目标服务器ID
  remark?: string | null; // 策略描述
}

export interface EditRouteRuleDto {
  id?: string | null; // 路由规则ID
  name?: string | null; // 路由规则名
  matchType?: number | null; // 匹配类型 0:全部 1:IP地址
  matchKey?: string | null; // 匹配键
  matchOperator?: number | null; // 匹配操作 0:等于
  matchValue?: string | null; // 匹配值
  routeServerId?: string | null; // 目标服务器ID
  remark?: string | null; // 策略描述
}

export interface GetRouteRuleDetailsVo {
  id?: string | null; // 路由规则ID
  name?: string | null; // 路由规则名
  matchType?: number | null; // 匹配类型 0:全部 1:IP地址
  matchKey?: string | null; // 匹配键
  matchOperator?: number | null; // 匹配操作 0:等于
  matchValue?: string | null; // 匹配值
  routeServerId?: string | null; // 目标服务器ID
  remark?: string | null; // 策略描述
  updateTime?: string | null; // 更新时间
}

export default {
  /**
   * 获取路由规则列表
   */
  getRouteRuleList: async (dto: GetRouteRuleListDto): Promise<PageResult<GetRouteRuleListVo>> => {
    return await Http.postEntity<PageResult<GetRouteRuleListVo>>("/routeRule/getRouteRuleList", dto);
  },

  /**
   * 新增路由规则
   */
  addRouteRule: async (dto: AddRouteRuleDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeRule/addRouteRule", dto);
  },

  /**
   * 编辑路由规则
   */
  editRouteRule: async (dto: EditRouteRuleDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeRule/editRouteRule", dto);
  },
  /**
   * 获取路由规则详情
   */

  getRouteRuleDetails: async (dto: CommonIdDto): Promise<Result<GetRouteRuleDetailsVo>> => {
    return await Http.postEntity<Result<GetRouteRuleDetailsVo>>("/routeRule/getRouteRuleDetails", dto);
  },
  /**
   * 删除路由规则
   */
  removeRouteRule: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeRule/removeRouteRule", dto);
  },
};
