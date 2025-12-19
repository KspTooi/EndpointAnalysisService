import type PageQuery from "@/commons/entity/PageQuery";
import type RestPageableView from "@/commons/entity/RestPageableView";
import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface GetEntryListDto extends PageQuery {
  parentId: string | null; //父级ID
  keyword: string | null; //关键词
}

export interface GetEntryListVo {
  id: string; //条目ID
  parentId: string | null; //父级ID
  name: string; //条目名称
  kind: number; //条目类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
  attachSize: number; //文件附件大小
  attachSuffix: string | null; //文件附件类型
  createTime: string; //创建时间
}

export interface AddEntryDto {
  parentId: string | null; //父级ID
  name: string; //条目名称
  kind: number; //条目类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
}

export default {
  /**
   * 获取驱动器条目列表
   * @param dto 查询条件
   * @returns 驱动器条目列表
   */
  getEntryList: async (dto: GetEntryListDto): Promise<RestPageableView<GetEntryListVo>> => {
    const ret = await Http.postEntity<RestPageableView<GetEntryListVo>>("/drive/getEntryList", dto);
    return ret;
  },

  /**
   * 新增驱动器条目
   * @param dto 新增条目
   * @returns 新增结果
   */
  addEntry: async (dto: AddEntryDto): Promise<Result<string>> => {
    const ret = await Http.postEntity<Result<string>>("/drive/addEntry", dto);
    return ret;
  },
};
