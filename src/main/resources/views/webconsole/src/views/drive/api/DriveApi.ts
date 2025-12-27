import type CommonIdDto from "@/commons/entity/CommonIdDto.ts"
import type Result from "@/commons/entity/Result.ts"
import Http from "@/commons/Http.ts"
import type {
  AddEntryDto,
  CheckEntryMoveVo, CopyEntryDto,
  GetDriveInfoVo, GetEntryDetailsVo,
  GetEntryListDto,
  GetEntryListVo,
  GetEntrySignVo,
  MoveEntryDto, RenameEntryDto,
} from "./DriveTypes"

export default {
  /**
   * 获取云盘信息
   * @returns 云盘信息
   */
  getDriveInfo: async (): Promise<Result<GetDriveInfoVo>> => {
    return await Http.postEntity<Result<GetDriveInfoVo>>("/drive/entry/getDriveInfo", {});
  },

  /**
   * 获取云盘条目列表
   * @param dto 查询条件
   * @returns 云盘条目列表
   */
  getEntryList: async (dto: GetEntryListDto): Promise<Result<GetEntryListVo>> => {
    return await Http.postEntity<Result<GetEntryListVo>>("/drive/entry/getEntryList", dto);
  },

  /**
   * 新增云盘条目
   * @param dto 新增条目
   * @returns 新增结果
   */
  addEntry: async (dto: AddEntryDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/drive/entry/addEntry", dto);
  },

  /**
   * 复制云盘条目
   * @param dto 复制条目
   * @returns 复制结果
   */
  copyEntry: async (dto: CopyEntryDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/drive/entry/copyEntry", dto);
  },

  /**
   * 重命名云盘条目
   * @param dto 重命名条目
   * @returns 重命名结果
   */
  renameEntry: async (dto: RenameEntryDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/drive/entry/renameEntry", dto);
  },

  /**
   * 获取云盘条目详情
   * @param dto 查询条件
   * @returns 云盘条目详情
   */
  getEntryDetails: async (dto: CommonIdDto): Promise<Result<GetEntryDetailsVo>> => {
    return await Http.postEntity<Result<GetEntryDetailsVo>>("/drive/entry/getEntryDetails", dto);
  },

  /**
   * 删除云盘条目
   * @param dto 删除条件
   * @returns 删除结果
   */
  deleteEntry: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/drive/entry/removeEntry", dto);
  },

  /**
   * 获取条目对象签名
   * @param dto 查询条件
   * @returns 条目对象签名
   */
  getEntrySign: async (dto: CommonIdDto): Promise<Result<GetEntrySignVo>> => {
    return await Http.postEntity<Result<GetEntrySignVo>>("/drive/object/access/getEntrySign", dto);
  },

  /**
   * 检查条目移动
   * @param dto 检查条目移动
   * @returns 检查条目移动结果
   */
  checkEntryMove: async (dto: MoveEntryDto): Promise<Result<CheckEntryMoveVo>> => {
    return await Http.postEntity<Result<CheckEntryMoveVo>>("/drive/entry/checkEntryMove", dto);
  },

  /**
   * 移动条目
   * @param dto 移动条目
   * @returns 移动结果
   */
  moveEntry: async (dto: MoveEntryDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/drive/entry/moveEntry", dto);
  },
};
