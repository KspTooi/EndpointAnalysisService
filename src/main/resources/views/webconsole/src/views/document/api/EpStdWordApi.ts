import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetEpStdWordListDto extends PageQuery {
  sourceName?: string | null;
  sourceNameFull?: string | null;
  targetName?: string | null;
  targetNameFull?: string | null;
  remark?: string | null;
}

export interface GetEpStdWordListVo {
  id: string;
  sourceName: string;
  sourceNameFull: string | null;
  targetName: string;
  targetNameFull: string | null;
  remark: string | null;
  createTime: string;
  creatorId: string;
  updateTime: string;
  updaterId: string;
}

export interface GetEpStdWordDetailsVo {
  id: string;
  sourceName: string;
  sourceNameFull: string | null;
  targetName: string;
  targetNameFull: string | null;
  remark: string | null;
}

export interface AddEpStdWordDto {
  sourceName: string;
  sourceNameFull?: string | null;
  targetName: string;
  targetNameFull?: string | null;
  remark?: string | null;
}

export interface EditEpStdWordDto {
  id: string;
  sourceName: string;
  sourceNameFull?: string | null;
  targetName: string;
  targetNameFull?: string | null;
  remark?: string | null;
}

export default {
  /**
   * 获取标准词列表
   */
  getEpStdWordList: async (dto: GetEpStdWordListDto): Promise<PageResult<GetEpStdWordListVo>> => {
    return await Http.postEntity<PageResult<GetEpStdWordListVo>>("/epStdWord/getEpStdWordList", dto);
  },

  /**
   * 获取标准词详情
   */
  getEpStdWordDetails: async (dto: CommonIdDto): Promise<GetEpStdWordDetailsVo> => {
    var result = await Http.postEntity<Result<GetEpStdWordDetailsVo>>("/epStdWord/getEpStdWordDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 添加标准词
   */
  addEpStdWord: async (dto: AddEpStdWordDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epStdWord/addEpStdWord", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑标准词
   */
  editEpStdWord: async (dto: EditEpStdWordDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epStdWord/editEpStdWord", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除标准词
   */
  removeEpStdWord: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epStdWord/removeEpStdWord", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
