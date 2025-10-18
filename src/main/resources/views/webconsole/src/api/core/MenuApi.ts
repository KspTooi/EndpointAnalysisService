/**
@Getter
@Setter
public class GetMenuTreeVo {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "子菜单")
    private List<GetMenuTreeVo> children;

}

 */

import type CommonIdDto from "@/commons/entity/CommonIdDto";
import Http from "@/commons/Http";
import type Result from "@/commons/entity/Result";

export interface GetUserMenuTreeVo {
  id: string; // 菜单ID
  parentId: string; // 父级ID -1:根节点
  name: string; // 菜单名称
  kind: number; // 资源类型 0:菜单 1:接口
  menuKind: number; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath: string; // 菜单路径
  menuQueryParam: string; // 菜单查询参数
  menuIcon: string; // 菜单图标
  permission: string; // 所需权限
  seq: number; // 排序
  children: GetUserMenuTreeVo[]; // 子菜单
}

export interface AddMenuDto {
  parentId: string; // 父级ID -1:根节点
  name: string; // 菜单名称
  description: string; // 菜单描述
  kind: number; // 资源类型 0:菜单 1:接口
  menuKind: number; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath: string; // 菜单路径
  menuQueryParam: string; // 菜单查询参数
  menuIcon: string; // 菜单图标
  permission: string; // 所需权限
  seq: number; // 排序
}

export interface EditMenuDto {
  id: string; // 菜单ID
  parentId: string; // 父级ID -1:根节点
  name: string; // 菜单名称
  description: string; // 菜单描述
  kind: number; // 资源类型 0:菜单 1:接口
  menuKind: number; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath: string; // 菜单路径
  menuQueryParam: string; // 菜单查询参数
  menuIcon: string; // 菜单图标
  permission: string; // 所需权限
  seq: number; // 排序
}

export interface GetMenuDetailsVo {
  id: string; // 菜单ID
  parentId: string; // 父级ID -1:根节点
  name: string; // 菜单名称
  description: string; // 菜单描述
  kind: number; // 资源类型 0:菜单 1:接口
  menuKind: number; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath: string; // 菜单路径
  menuQueryParam: string; // 菜单查询参数
  menuIcon: string; // 菜单图标
  permission: string; // 所需权限
  seq: number; // 排序
}

export interface GetMenuTreeDto {
  name: string; // 菜单名称/描述(模糊)
  menuKind: number | null; // 菜单类型
  permission: string; // 权限(模糊)
}

export interface GetMenuTreeVo {
  id: string; // 菜单ID
  parentId: string; // 父级ID -1:根节点
  name: string; // 菜单名称
  kind: number; // 资源类型 0:菜单 1:接口
  menuKind: number; // 菜单类型 0:目录 1:菜单 2:按钮
  menuPath: string; // 菜单路径
  menuQueryParam: string; // 菜单查询参数
  menuIcon: string; // 菜单图标
  permission: string; // 所需权限
  seq: number; // 排序
  children: GetMenuTreeVo[]; // 子菜单
}

export default {
  /**
   * 获取用户菜单树
   */
  getUserMenuTree: async (): Promise<Result<GetUserMenuTreeVo[]>> => {
    return await Http.postEntity<Result<GetUserMenuTreeVo[]>>("/menu/getUserMenuTree", {});
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
   * 获取菜单树
   */
  getMenuTree: async (dto: GetMenuTreeDto): Promise<Result<GetMenuTreeVo[]>> => {
    return await Http.postEntity<Result<GetMenuTreeVo[]>>("/menu/getMenuTree", dto);
  },

  /**
   * 删除菜单
   */
  removeMenu: async (dto: CommonIdDto): Promise<Result<string>> => {
    return await Http.postEntity<Result<string>>("/menu/removeMenu", dto);
  },
};
