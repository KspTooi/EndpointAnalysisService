import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetEpDocListDto extends PageQuery {}

export interface GetEpDocListVo {
  id: string;
  relayServerId: number;
  relayServerName: string;
  docPullUrl: string;
  pullTime: string;
  createTime: string;
}

export interface GetEpDocDetailsVo {
  id: string;
  relayServerId: number | null;
  relayServerName: string;
  docPullUrl: string;
  pullTime: string;
  createTime: string;
}

export interface AddEpDocDto {
  relayServerId: number | null;
  docPullUrl: string;
}

export interface EditEpDocDto {
  id: string;
  relayServerId: number | null;
  docPullUrl: string;
}

export default {
  /**
   * 获取中继通道文档拉取配置列表
   */
  getEpDocList: async (dto: GetEpDocListDto): Promise<PageResult<GetEpDocListVo>> => {
    return await Http.postEntity<PageResult<GetEpDocListVo>>("/epdoc/getEpDocList", dto);
  },

  /**
   * 获取中继通道文档拉取配置详情
   */
  getEpDocDetails: async (dto: CommonIdDto): Promise<GetEpDocDetailsVo> => {
    var result = await Http.postEntity<Result<GetEpDocDetailsVo>>("/epdoc/getEpDocDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 添加中继通道文档拉取配置
   */
  addEpDoc: async (dto: AddEpDocDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epdoc/addEpDoc", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑中继通道文档拉取配置
   */
  editEpDoc: async (dto: EditEpDocDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epdoc/editEpDoc", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除中继通道文档拉取配置
   */
  removeEpDoc: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epdoc/removeEpDoc", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 拉取文档
   */
  pullEpDoc: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epdoc/pullEpDoc", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
