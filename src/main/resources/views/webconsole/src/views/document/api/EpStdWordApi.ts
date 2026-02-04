import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

import axios from "axios";

export interface GetEpStdWordListDto extends PageQuery {
  sourceName?: string | null; //中文简称
  sourceNameFull?: string | null; //中文全称
  targetName?: string | null; //英文简称
  targetNameFull?: string | null; //英文全称
  remark?: string | null; //备注
}

export interface GetEpStdWordListVo {
  id: string; //标准词ID
  sourceName: string; //中文简称
  sourceNameFull: string | null; //中文全称
  targetName: string; //英文简称
  targetNameFull: string | null; //英文全称
  remark: string | null; //备注
  createTime: string; //创建时间
  creatorId: string; //创建人ID
  updateTime: string; //更新时间
  updaterId: string; //更新人ID
}

export interface GetEpStdWordDetailsVo {
  id: string; //标准词ID
  sourceName: string; //中文简称
  sourceNameFull: string | null; //中文全称
  targetName: string; //英文简称
  targetNameFull: string | null; //英文全称
  remark: string | null; //备注
}

export interface AddEpStdWordDto {
  sourceName: string; //中文简称
  sourceNameFull?: string | null; //中文全称
  targetName: string; //英文简称
  targetNameFull?: string | null; //英文全称
  remark?: string | null; //备注
}

export interface EditEpStdWordDto {
  id: string; //标准词ID
  sourceName: string; //中文简称
  sourceNameFull?: string | null; //中文全称
  targetName: string; //英文简称
  targetNameFull?: string | null; //英文全称
  remark?: string | null; //备注
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

  /**
   * 导出标准词
   */
  exportEpStdWord: async (dto: GetEpStdWordListDto): Promise<void> => {
    const response = await axios.post(`/epStdWord/exportEpStdWord`, dto, {
      responseType: "blob",
      headers: {
        "AE-Request-With": "XHR",
      },
    });

    const contentDisposition = response.headers["content-disposition"];
    let filename = "标准词导出.xlsx";

    if (contentDisposition) {
      const filenameMatch = contentDisposition.match(/filename\*=UTF-8''(.+)/);
      if (filenameMatch && filenameMatch[1]) {
        filename = decodeURIComponent(filenameMatch[1]);
      }
    }

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
  },
};
