/**
 * @Getter
@Setter
public class GetRouteServerListDto extends PageQuery {

    @Schema(description = "服务器名称")
    private String name;

    @Schema(description = "服务器主机")
    private String host;

    @Schema(description = "服务器端口")
    private Integer port;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "服务器状态 0:禁用 1:启用")
    private Integer status;

}

 */

import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";
import Http from "@/commons/Http.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageResult from "@/commons/entity/PageResult.ts";

export interface GetRouteServerListDto extends PageQuery {
  name?: string | null; //服务器名称
  host?: string | null; //服务器主机
  port?: number | null; //服务器端口
  remark?: string | null; //备注
  status?: number | null; //服务器状态 0:禁用 1:启用
}

export interface GetRouteServerListVo {
  id: string; //路由服务器ID
  name: string; //服务器名称
  host: string; //服务器主机
  port: number; //服务器端口
  remark: string; //备注
  status: number; //服务器状态 0:禁用 1:启用
  updateTime: string; //更新时间
}

export interface AddRouteServerDto {
  name: string; //服务器名称
  host: string; //服务器主机
  port: number; //服务器端口
  remark: string; //备注
  status: number; //服务器状态 0:禁用 1:启用
}

export interface EditRouteServerDto {
  id: string; //路由服务器ID
  name: string; //服务器名称
  host: string; //服务器主机
  port: number; //服务器端口
  remark: string; //备注
  status: number; //服务器状态 0:禁用 1:启用
}

export interface GetRouteServerDetailsVo {
  id: string; //路由服务器ID
  name: string; //服务器名称
  host: string; //服务器主机
  port: number; //服务器端口
  remark: string; //备注
  status: number; //服务器状态 0:禁用 1:启用
  createTime: string; //创建时间
  updateTime: string; //更新时间
}

export default {
  /**
   * 获取路由服务器列表
   */
  getRouteServerList: async (dto: GetRouteServerListDto): Promise<PageResult<GetRouteServerListVo>> => {
    return await Http.postEntity<PageResult<GetRouteServerListVo>>("/routeServer/getRouteServerList", dto);
  },

  /**
   * 新增路由服务器
   */
  addRouteServer: async (dto: AddRouteServerDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeServer/addRouteServer", dto);
  },

  /**
   * 编辑路由服务器
   */
  editRouteServer: async (dto: EditRouteServerDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeServer/editRouteServer", dto);
  },

  /**
   * 获取路由服务器详情
   */
  getRouteServerDetails: async (dto: CommonIdDto): Promise<Result<GetRouteServerDetailsVo>> => {
    return await Http.postEntity<Result<GetRouteServerDetailsVo>>("/routeServer/getRouteServerDetails", dto);
  },

  /**
   * 删除路由服务器
   */
  removeRouteServer: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/routeServer/removeRouteServer", dto);
  },
};
