import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

export interface AddMenuDto {
  parentId?: string | null; // 父级ID null:根节点
  name?: string | null; // 菜单名称
  description?: string | null; // 菜单描述
  kind?: number | null; // 资源类型 0:菜单 1:接口
  menuKind?: number | null; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath?: string | null; // 菜单路径
  menuQueryParam?: string | null; // 菜单查询参数
  menuIcon?: string | null; // 菜单图标
  menuHidden?: number | null; // 是否隐藏 0:否 1:是(menuKind = 1/2时生效)
  menuBtnId?: string | null; // 按钮ID
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface EditMenuDto {
  id?: string | null; // 菜单ID
  parentId?: string | null; // 父级ID null:根节点
  name?: string | null; // 菜单名称
  description?: string | null; // 菜单描述
  kind?: number | null; // 资源类型 0:菜单 1:接口
  menuKind?: number | null; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath?: string | null; // 菜单路径
  menuQueryParam?: string | null; // 菜单查询参数
  menuIcon?: string | null; // 菜单图标
  menuHidden?: number | null; // 是否隐藏 0:否 1:是(menuKind = 1/2时生效)
  menuBtnId?: string | null; // 按钮ID
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface GetMenuDetailsVo {
  id?: string | null; // 菜单ID
  parentId?: string | null; // 父级ID null:根节点
  name?: string | null; // 菜单名称
  description?: string | null; // 菜单描述
  kind?: number | null; // 资源类型 0:菜单 1:接口
  menuKind?: number | null; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath?: string | null; // 菜单路径
  menuQueryParam?: string | null; // 菜单查询参数
  menuIcon?: string | null; // 菜单图标
  menuHidden?: number | null; // 是否隐藏 0:否 1:是(menuKind = 1/2时生效)
  menuBtnId?: string | null; // 按钮ID
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
}

export interface GetMenuTreeDto {
  name?: string | null; // 菜单名称/描述(模糊)
  menuKind?: number | null; // 菜单类型
  permission?: string | null; // 权限(模糊)
}

export interface GetMenuTreeVo {
  id?: string | null; // 菜单ID
  parentId?: string | null; // 父级ID null:根节点
  name?: string | null; // 菜单名称
  kind?: number | null; // 资源类型 0:菜单 1:接口
  menuKind?: number | null; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath?: string | null; // 菜单路径
  menuQueryParam?: string | null; // 菜单查询参数
  menuIcon?: string | null; // 菜单图标
  menuHidden?: number | null; // 是否隐藏 0:否 1:是(menuKind = 1/2时生效)
  menuBtnId?: string | null; // 按钮ID
  permission?: string | null; // 所需权限
  missingPermission?: number | null; // 是否缺失权限节点 0:否 1:完全缺失 2:部分缺失
  seq?: number | null; // 排序
  children: GetMenuTreeVo[]; // 子菜单
}

export interface GetUserMenuTreeVo {
  id?: string | null; // 菜单ID
  parentId?: string | null; // 父级ID null:根节点
  name?: string | null; // 菜单名称
  menuIcon?: string | null; // 菜单图标
  menuKind?: number | null; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath?: string | null; // 菜单路径
  menuHidden?: number | null; // 是否隐藏 0:否 1:是(menuKind = 1/2时生效)
  menuBtnId?: string | null; // 按钮ID
  permission?: string | null; // 所需权限
  seq?: number | null; // 排序
  children: GetUserMenuTreeVo[]; // 子菜单
}

export default {
  /**
   * 获取用户菜单树
   */
  getUserMenuTree: async (): Promise<Result<GetUserMenuTreeVo[]>> => {
    return await Http.postEntity<Result<GetUserMenuTreeVo[]>>("/menu/getUserMenuTree", {});
  },

  /**
   * 获取菜单树
   */
  getMenuTree: async (dto: GetMenuTreeDto): Promise<Result<GetMenuTreeVo[]>> => {
    return await Http.postEntity<Result<GetMenuTreeVo[]>>("/menu/getMenuTree", dto);
  },

  /**
   * 新增菜单
   */
  addMenu: async (dto: AddMenuDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/menu/addMenu", dto);
  },

  /**
   * 编辑菜单
   */
  editMenu: async (dto: EditMenuDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/menu/editMenu", dto);
  },

  /**
   * 获取菜单详情
   */
  getMenuDetails: async (dto: CommonIdDto): Promise<Result<GetMenuDetailsVo>> => {
    return await Http.postEntity<Result<GetMenuDetailsVo>>("/menu/getMenuDetails", dto);
  },

  /**
   * 删除菜单
   */
  removeMenu: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/menu/removeMenu", dto);
  },
};
