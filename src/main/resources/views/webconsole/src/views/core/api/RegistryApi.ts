import Http from "@/commons/Http.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 查询列表DTO
 */
export interface GetRegistryListDto extends PageQuery {
  keyPath: string; // 节点Key的全路径
}

/**
 * 列表项VO (用于 getRegistryEntryList)
 */
export interface GetRegistryEntryListVo {
  id: string; // ID
  parentId: string; // 父级项ID NULL顶级
  nkey: string; // 节点Key
  nvalueKind: number; // 数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)
  nvalue: string; // 节点Value
  label: string; // 节点标签
  remark: string; // 说明
  status: number; // 状态 0:正常 1:停用
  seq: number; // 排序
  createTime: string; // 创建时间
}

/**
 * 新增DTO
 */
export interface AddRegistryDto {
  parentId?: string; // 父级项ID NULL顶级（节点可为null）
  kind: number; // 类型 0:节点 1:条目
  nkey: string; // 节点Key
  nvalueKind?: number; // 数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)（节点不需要）
  nvalue?: string; // 节点Value（节点不需要）
  label?: string; // 节点标签
  remark?: string; // 说明
  metadata?: string; // 元数据JSON
  status?: number; // 状态 0:正常 1:停用（节点不需要）
  seq: number; // 排序
}

/**
 * 编辑DTO
 */
export interface EditRegistryDto {
  id: string; // ID
  nvalueKind?: number; // 数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)
  nvalue?: string; // 条目Value
  label?: string; // 条目标签
  seq: number; // 排序
  remark?: string; // 说明
}

/**
 * 注册表节点树VO
 */
export interface GetRegistryNodeTreeVo {
  id: string; // ID
  parentId: string; // 父级项ID NULL顶级
  keyPath: string; // 节点Key的全路径
  nkey: string; // 节点Key
  label: string; // 节点标签
  seq: number; // 排序
  children?: GetRegistryNodeTreeVo[]; // 子级节点
}

/**
 * 注册表详情VO (包含新增和编辑所需的所有字段)
 */
export interface GetRegistryDetailsVo {
  id?: string; // ID
  parentId?: string; // 父级项ID NULL顶级
  kind: number; // 类型 0:节点 1:条目
  nkey: string; // 节点Key
  nvalueKind: number; // 数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)
  nvalue: string; // 节点Value
  label: string; // 节点标签
  remark: string; // 说明
  metadata: string; // 元数据JSON
  status: number; // 状态 0:正常 1:停用
  seq: number; // 排序
}

/**
 * 注册表 API 接口
 */
export default {
  /**
   * 获取注册表条目列表
   * @param dto 查询参数
   * @returns 注册表条目 VO 数组
   */
  getRegistryEntryList: async (dto: GetRegistryListDto): Promise<GetRegistryEntryListVo[]> => {
    const result = await Http.postEntity<Result<GetRegistryEntryListVo[]>>("/registry/getRegistryEntryList", dto);
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取注册表节点树
   * @returns 注册表节点树 VO 数组
   */
  getRegistryNodeTree: async (): Promise<GetRegistryNodeTreeVo[]> => {
    const result = await Http.postEntity<Result<GetRegistryNodeTreeVo[]>>("/registry/getRegistryNodeTree", {});
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 新增注册表条目或节点
   * @param dto 新增参数
   * @returns 成功消息
   */
  addRegistry: async (dto: AddRegistryDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/registry/addRegistry", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 编辑注册表条目
   * @param dto 编辑参数
   * @returns 成功消息
   */
  editRegistry: async (dto: EditRegistryDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/registry/editRegistry", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /**
   * 删除注册表条目
   * @param dto 删除参数（含 ID）
   * @returns 成功消息
   */
  removeRegistry: async (dto: CommonIdDto): Promise<string> => {
    const result = await Http.postEntity<Result<string>>("/registry/removeRegistry", dto);
    if (result.code === 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
