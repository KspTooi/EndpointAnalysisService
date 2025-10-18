import type PageResult from "@/commons/entity/PageResult";
import Http from "@/commons/Http";
import type Result from "@/commons/entity/Result";
import type CommonIdDto from "@/commons/entity/CommonIdDto";
import type PageQuery from "@/commons/entity/PageQuery";

export interface GetRouteGroupListDto extends PageQuery {
  name: string | null; // 组名称
  remark: string | null; // 备注
  loadBalance: number | null; // 负载均衡策略 0:轮询 1:随机 2:权重
  autoDegradation: number | null; // 自动降级 0:开启 1:关闭
}

export interface GetRouteGroupListVo {
  id: string; // 路由组ID
  name: string; // 组名称
  remark: string | null; // 备注
  loadBalance: number; // 负载均衡策略 0:轮询 1:随机 2:权重
  autoDegradation: number; // 自动降级 0:开启 1:关闭
}

export interface AddRouteGroupDto {
  name: string; // 组名称
  remark: string | null; // 备注
  loadBalance: number; // 负载均衡策略 0:轮询 1:随机 2:权重
  autoDegradation: number; // 自动降级 0:开启 1:关闭
}

export interface EditRouteGroupDto {
  id: string | null; // 路由组ID
  name: string; // 组名称
  remark: string | null; // 备注
  loadBalance: number; // 负载均衡策略 0:轮询 1:随机 2:权重
  autoDegradation: number; // 自动降级 0:开启 1:关闭
}

export interface GetRouteGroupDetailsVo {
  id: string | null; // 路由组ID
  name: string; // 组名称
  remark: string | null; // 备注
  loadBalance: number; // 负载均衡策略 0:轮询 1:随机 2:权重
  autoDegradation: number; // 自动降级 0:开启 1:关闭
}

export default {
  /**
   * 获取路由组列表
   */
  getRouteGroupList: async (dto: GetRouteGroupListDto): Promise<PageResult<GetRouteGroupListVo>> => {
    return await Http.postEntity<PageResult<GetRouteGroupListVo>>("/routeGroup/getRouteGroupList", dto);
  },

  /**
   * 新增路由组
   */
  addRouteGroup: async (dto: AddRouteGroupDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeGroup/addRouteGroup", dto);
  },

  /**
   * 编辑路由组
   */
  editRouteGroup: async (dto: EditRouteGroupDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeGroup/editRouteGroup", dto);
  },

  /**
   * 获取路由组详情
   */
  getRouteGroupDetails: async (dto: CommonIdDto): Promise<Result<GetRouteGroupDetailsVo>> => {
    return await Http.postEntity<Result<GetRouteGroupDetailsVo>>("/routeGroup/getRouteGroupDetails", dto);
  },

  /**
   * 删除路由组
   */
  removeRouteGroup: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeGroup/removeRouteGroup", dto);
  },
};
