import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetConfigListDto extends PageQuery {
  keyword: string | null; // 配置键/值/描述
  userName: string | null; // 所有者名称
}

export interface GetConfigListVo {
  id: string; // 配置ID
  userName: string; // 所有者名称
  configKey: string; // 配置键
  configValue: string; // 配置值
  description: string; // 配置描述
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
}

export interface GetConfigDetailsVo {
  id: string; // 配置ID
  userId: string; // 用户ID
  userName: string; // 所有者名称
  configKey: string; // 配置键
  configValue: string; // 配置值
  description: string; // 配置描述
  createTime: string; // 创建时间
  updateTime: string; // 更新时间
}

export interface SaveConfigDto {
  id?: string; // 配置ID
  configKey: string; // 配置键
  configValue: string; // 配置值
  description?: string; // 配置描述
}

export default {
  /**
   * 获取配置列表
   */
  getConfigList: async (dto: GetConfigListDto): Promise<PageResult<GetConfigListVo>> => {
    return await Http.postEntity<PageResult<GetConfigListVo>>("/config/getConfigList", dto);
  },

  /**
   * 获取配置详情
   */
  getConfigDetails: async (dto: CommonIdDto): Promise<GetConfigDetailsVo> => {
    var result = await Http.postEntity<Result<GetConfigDetailsVo>>("/config/getConfigDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 保存配置
   */
  saveConfig: async (dto: SaveConfigDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/config/saveConfig", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除配置
   */
  removeConfig: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/config/removeConfig", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
