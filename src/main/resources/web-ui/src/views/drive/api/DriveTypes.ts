import Http from "@/commons/Http.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";

export interface GetEntryListDto extends PageQuery {
  driveSpaceId: string; //云盘空间ID
  directoryId: string | null; //目录ID
  keyword: string | null; //关键词
}

export interface GetEntryListVo {
  dirId: string; //目录ID
  dirName: string; //目录名称
  dirParentId: string | null; //父级目录ID 为NULL顶级
  items: GetEntryListItemVo[]; //条目列表
  total: string; //条目总数
  paths: GetEntryListPathVo[]; //当前目录路径(至多10层)
}

export interface GetEntryListItemVo {
  id: string; //条目ID
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

export interface GetEntrySignVo {
  params: string; //签名参数串
  isBatch: number; //是否批量签名 0:否 1:是
}

export interface MoveEntryDto {
  targetId: string | null; //目标ID 为NULL顶级
  entryIds: string[] | null; //条目ID列表 为NULL表示拖拽到顶级
  mode: number | null; //移动模式 0:覆盖 1:跳过
}

export interface CheckEntryMoveVo {
  canMove: number; //是否可以移动 0:是 1:名称冲突 2:不可移动
  message: string; //提示信息
  conflictNames: string[]; //存在冲突的条目名称列表
}

export interface CurrentDirPo {
  id: string; //当前目录ID 为NULL顶级
  name: string; //当前目录名称
  parentId: string; //当前目录父级ID 为NULL顶级
}

/**
 * 云盘条目对象
 */
export interface EntryPo {
  id: string; //条目ID
  parentId: string | null; //父级ID
  name: string; //条目名称
  kind: number; //条目类型 0:文件 1:文件夹
  attachId: string | null; //文件附件ID
  attachSize: string; //文件附件大小
  attachSuffix: string | null; //文件附件类型
  createTime: string; //创建时间
  updateTime: string; //更新时间
}

export interface GetEntryListPathVo {
  id: string; //条目ID
  name: string; //条目名称
}
