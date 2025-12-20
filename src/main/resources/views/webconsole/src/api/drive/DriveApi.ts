import type CommonIdDto from "@/commons/entity/CommonIdDto";
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
  attachSize: string; //文件附件大小
  attachSuffix: string | null; //文件附件类型
  createTime: string; //创建时间
}

export interface AddEntryDto {
  parentId: string | null; //父级ID
  name: string; //条目名称
  kind: number; //条目类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
}

export interface GetEntryDetailsVo {
  id: string; //条目ID
  companyId: string; //团队ID
  parentId: string | null; //父级ID
  name: string; //条目名称
  kind: number; //条目类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
  attachSize: string; //文件附件大小
  attachSuffix: string | null; //文件附件类型
  createTime: string; //创建时间
  updateTime: string; //更新时间
  creatorId: string; //创建人
  updaterId: string; //更新人
}

export interface GetDriveInfoVo {
  totalCapacity: string; //总容量
  usedCapacity: string; //已使用容量
  objectCount: string; //对象数量
}

export interface CopyEntryDto {
  entryIds: string[]; //条目ID列表
  parentId: string | null; //父级ID
}

export interface RenameEntryDto {
  entryId: string; //条目ID
  name: string; //条目名称
}

export default {
  /**
   * 获取云盘信息
   * @returns 云盘信息
   */
  getDriveInfo: async (): Promise<Result<GetDriveInfoVo>> => {
    const ret = await Http.postEntity<Result<GetDriveInfoVo>>("/drive/entry/getDriveInfo", {});
    return ret;
  },

  /**
   * 获取云盘条目列表
   * @param dto 查询条件
   * @returns 云盘条目列表
   */
  getEntryList: async (dto: GetEntryListDto): Promise<RestPageableView<GetEntryListVo>> => {
    const ret = await Http.postEntity<RestPageableView<GetEntryListVo>>("/drive/entry/getEntryList", dto);
    return ret;
  },

  /**
   * 新增云盘条目
   * @param dto 新增条目
   * @returns 新增结果
   */
  addEntry: async (dto: AddEntryDto): Promise<Result<string>> => {
    const ret = await Http.postEntity<Result<string>>("/drive/entry/addEntry", dto);
    return ret;
  },

  /**
   * 复制云盘条目
   * @param dto 复制条目
   * @returns 复制结果
   */
  copyEntry: async (dto: CopyEntryDto): Promise<Result<string>> => {
    const ret = await Http.postEntity<Result<string>>("/drive/entry/copyEntry", dto);
    return ret;
  },

  /**
   * 重命名云盘条目
   * @param dto 重命名条目
   * @returns 重命名结果
   */
  renameEntry: async (dto: RenameEntryDto): Promise<Result<string>> => {
    const ret = await Http.postEntity<Result<string>>("/drive/entry/renameEntry", dto);
    return ret;
  },

  /**
   * 获取云盘条目详情
   * @param dto 查询条件
   * @returns 云盘条目详情
   */
  getEntryDetails: async (dto: CommonIdDto): Promise<Result<GetEntryDetailsVo>> => {
    const ret = await Http.postEntity<Result<GetEntryDetailsVo>>("/drive/entry/getEntryDetails", dto);
    return ret;
  },

  /**
   * 删除云盘条目
   * @param dto 删除条件
   * @returns 删除结果
   */
  deleteEntry: async (dto: CommonIdDto): Promise<Result<string>> => {
    const ret = await Http.postEntity<Result<string>>("/drive/entry/removeEntry", dto);
    return ret;
  },
};
