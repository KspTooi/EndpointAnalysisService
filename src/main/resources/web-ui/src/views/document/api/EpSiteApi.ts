import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetEpSiteListDto extends PageQuery {
  id?: string | null; //站点ID
  name?: string | null; //站点名称
  address?: string | null; //地址
  username?: string | null; //账户
  remark?: string | null; //备注
  seq?: number | null; //排序
  createTime?: string | null; //创建时间
}

export interface GetEpSiteListVo {
  id: string; //站点ID
  name: string; //站点名称
  address: string | null; //地址
  username: string | null; //账户
  password: string | null; //密码
  remark: string | null; //备注
  seq: number; //排序
  createTime: string; //创建时间
}

export interface GetEpSiteDetailsVo {
  id: string; //站点ID
  name: string; //站点名称
  address: string | null; //地址
  username: string | null; //账户
  password: string | null; //密码
  remark: string | null; //备注
  seq: number; //排序
}

export interface AddEpSiteDto {
  name: string; //站点名称
  address?: string | null; //地址
  username?: string | null; //账户
  password?: string | null; //密码
  remark?: string | null; //备注
  seq?: number | null; //排序
}

export interface EditEpSiteDto {
  id: string; //站点ID
  name: string; //站点名称
  address?: string | null; //地址
  username?: string | null; //账户
  password?: string | null; //密码
  remark?: string | null; //备注
  seq?: number | null; //排序
}

export default {
  /**
   * 获取站点列表
   */
  getEpSiteList: async (dto: GetEpSiteListDto): Promise<PageResult<GetEpSiteListVo>> => {
    return await Http.postEntity<PageResult<GetEpSiteListVo>>("/epSite/getEpSiteList", dto);
  },

  /**
   * 获取站点详情
   */
  getEpSiteDetails: async (dto: CommonIdDto): Promise<GetEpSiteDetailsVo> => {
    var result = await Http.postEntity<Result<GetEpSiteDetailsVo>>("/epSite/getEpSiteDetails", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 添加站点
   */
  addEpSite: async (dto: AddEpSiteDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epSite/addEpSite", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑站点
   */
  editEpSite: async (dto: EditEpSiteDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epSite/editEpSite", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除站点
   */
  removeEpSite: async (dto: CommonIdDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/epSite/removeEpSite", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 导出站点
   */
  exportEpSite: async (dto: GetEpSiteListDto): Promise<void> => {
    const response = await Http.axios().post(`/epSite/exportEpSite`, dto, {
      responseType: "blob",
      headers: {
        "AE-Request-With": "XHR",
      },
    });

    const contentDisposition = response.headers["content-disposition"];
    let filename = "站点导出.xlsx";

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
