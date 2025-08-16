import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type PageQuery from "@/commons/entity/PageQuery";
import type RestPageableView from "@/commons/entity/RestPageableView";
import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface GetSimpleFilterListDto extends PageQuery {
  name: string | null; // 过滤器名称
  direction: number | null; // 过滤器方向 0:请求过滤器 1:响应过滤器
  status: number | null; // 状态 0:启用 1:禁用
}

export interface GetSimpleFilterListVo {
  id: string; // 过滤器ID
  name: string; // 过滤器名称
  direction: number; // 过滤器方向 0:请求过滤器 1:响应过滤器
  status: number; // 状态 0:启用 1:禁用
  triggerCount: number; // 触发器数量
  operationCount: number; // 操作数量
  createTime: string; // 创建时间
}

//新增过滤器触发器
export interface AddSimpleFilterTriggerDto {
  name: string; // 触发器名称
  target: number; // 目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法
  kind: number; // 条件 0:包含 1:不包含 2:等于 3:不等于
  tk: string; // 目标键
  tv: string; // 比较值
  seq: number; // 排序
}

//新增过滤器操作
export interface AddSimpleFilterOperationDto {
  name: string; // 操作名称
  kind: number; // 类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL
  target: number; // 目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)
  f: string; // 原始键
  t: string; // 目标键
  seq: number; // 排序
}

export interface AddSimpleFilterDto {
  name: string; // 过滤器名称
  direction: number; // 过滤器方向 0:请求过滤器 1:响应过滤器
  status: number; // 状态 0:启用 1:禁用
  triggers: AddSimpleFilterTriggerDto[]; // 触发器列表
  operations: AddSimpleFilterOperationDto[]; // 操作列表
}

//编辑过滤器触发器
export interface EditSimpleFilterTriggerDto {
  id: string | null; // 触发器ID 新增时为null
  name: string; // 触发器名称
  target: number; // 目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法
  kind: number; // 条件 0:包含 1:不包含 2:等于 3:不等于
  tk: string; // 目标键
  tv: string; // 比较值
}

//编辑过滤器操作
export interface EditSimpleFilterOperationDto {
  id: string | null; // 操作ID 新增时为null
  name: string; // 操作名称
  kind: number; // 类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL
  target: number; // 目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)
  f: string; // 原始键
  t: string; // 目标键
}

//编辑过滤器
export interface EditSimpleFilterDto {
  id: string; // 过滤器ID
  name: string; // 过滤器名称
  direction: number; // 过滤器方向 0:请求过滤器 1:响应过滤器
  status: number; // 状态 0:启用 1:禁用
  triggers: EditSimpleFilterTriggerDto[]; // 触发器列表
  operations: EditSimpleFilterOperationDto[]; // 操作列表
}

//查询过滤器触发器详情
export interface GetSimpleFilterTriggerDetailsVo {
  id: string; // 触发器ID
  name: string; // 触发器名称
  target: number; // 目标 0:标头 1:JSON载荷 2:URL 3:HTTP方法
  kind: number; // 条件 0:包含 1:不包含 2:等于 3:不等于
  tk: string; // 目标键
  tv: string; // 比较值
  seq: number; // 排序
}

//查询过滤器操作详情
export interface GetSimpleFilterOperationDetailsVo {
  id: string; // 操作ID
  name: string; // 操作名称
  kind: number; // 类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL
  target: number; // 目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)
  f: string; // 原始键
  t: string; // 目标键
  seq: number; // 排序
}

//查询过滤器详情
export interface GetSimpleFilterDetailsVo {
  id: string; // 过滤器ID
  name: string; // 过滤器名称
  direction: number; // 过滤器方向 0:请求过滤器 1:响应过滤器
  status: number; // 状态 0:启用 1:禁用
  triggers: GetSimpleFilterTriggerDetailsVo[]; // 触发器列表
  operations: GetSimpleFilterOperationDetailsVo[]; // 操作列表
}

export default {
  /**
   * 获取过滤器列表
   * @param dto 查询条件
   * @returns 过滤器列表
   */
  getSimpleFilterList: async (dto: GetSimpleFilterListDto): Promise<RestPageableView<GetSimpleFilterListVo>> => {
    return await Http.postEntity<RestPageableView<GetSimpleFilterListVo>>("/simpleFilter/getSimpleFilterList", dto);
  },

  /**
   * 新增过滤器
   * @param dto 过滤器信息
   * @returns 过滤器ID
   */
  addSimpleFilter: async (dto: AddSimpleFilterDto): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/simpleFilter/addSimpleFilter", dto);
  },

  /**
   * 编辑过滤器
   * @param dto 过滤器信息
   * @returns 过滤器ID
   */
  editSimpleFilter: async (dto: EditSimpleFilterDto): Promise<Result<void>> => {
    return await Http.postEntity<Result<void>>("/simpleFilter/editSimpleFilter", dto);
  },

  /**
   * 查询过滤器详情
   * @param dto 过滤器ID
   * @returns 过滤器详情
   */
  getSimpleFilterDetails: async (dto: CommonIdDto): Promise<GetSimpleFilterDetailsVo> => {
    const ret = await Http.postEntity<Result<GetSimpleFilterDetailsVo>>("/simpleFilter/getSimpleFilterDetails", dto);
    if (ret.code === 0) {
      return ret.data;
    }
    throw new Error(ret.message);
  },
};
