import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

export interface AddEndpointDto {
  parentId?: string | null; // 父级ID
  name?: string | null; // 端点名称
  description?: string | null; // 端点描述
  path?: string | null; // 端点路径
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface EditEndpointDto {
  id?: string | null; // 主键id
  parentId?: string | null; // 父级ID
  name?: string | null; // 端点名称
  description?: string | null; // 端点描述
  path?: string | null; // 端点路径
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface GetEndpointDetailsVo {
  id?: string | null; // 主键id
  parentId?: string | null; // 父级ID
  name?: string | null; // 端点名称
  description?: string | null; // 端点描述
  path?: string | null; // 端点路径
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface GetEndpointTreeDto {
  name?: string | null; // 端点名称
  path?: string | null; // 端点路径
}

export interface GetEndpointTreeVo {
  id?: string | null; // 主键id
  parentId?: string | null; // 父级ID
  name?: string | null; // 端点名称
  description?: string | null; // 端点描述
  path?: string | null; // 端点路径
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
  cached?: number | null; // 是否已被缓存 0:否 1:是
  missingPermission?: number | null; // 是否缺失权限节点 0:否 1:完全缺失 2:部分缺失
  children: GetEndpointTreeVo[]; // 子端点
}

export default {
  /**
   * 获取端点树
   */
  getEndpointTree: async (dto: GetEndpointTreeDto): Promise<Result<GetEndpointTreeVo[]>> => {
    return await Http.postEntity<Result<GetEndpointTreeVo[]>>("/endpoint/getEndpointTree", dto);
  },

  /**
   * 添加端点
   */
  addEndpoint: async (dto: AddEndpointDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/endpoint/addEndpoint", dto);
  },

  /**
   * 编辑端点
   */
  editEndpoint: async (dto: EditEndpointDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/endpoint/editEndpoint", dto);
  },

  /**
   * 获取端点详情
   */
  getEndpointDetails: async (dto: CommonIdDto): Promise<Result<GetEndpointDetailsVo>> => {
    return await Http.postEntity<Result<GetEndpointDetailsVo>>("/endpoint/getEndpointDetails", dto);
  },

  /**
   * 删除端点
   */
  removeEndpoint: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/endpoint/removeEndpoint", dto);
  },

  /**
   * 清空端点缓存
   */
  clearEndpointCache: async (): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/endpoint/clearEndpointCache", {});
  },
};
