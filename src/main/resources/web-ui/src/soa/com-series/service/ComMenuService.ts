import { Result } from "@/commons/model/Result.ts";
import type { GetUserMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import MenuApi from "@/views/core/api/MenuApi.ts";
import { defineStore } from "pinia";
import { ElMessage } from "element-plus";
import { computed, ref } from "vue";
import { useRoute } from "vue-router";

export default {
  /**
   * 使用菜单存储
   */
  useMenuStore() {
    const menuStore = defineStore("menuStore", {
      state: () => ({
        //菜单树数据
        menuTree: [] as GetUserMenuTreeVo[],

        //菜单展开数据(记录哪些菜单是展开的)
        menuOpened: [] as string[],
      }),

      //持久化菜单展开数据
      persist: {
        key: "menu_opened",
        pick: ["menuOpened"],
      },

      getters: {
        getMenuTree: (state) => state.menuTree,
        getMenuOpened: (state) => state.menuOpened,
      },
      actions: {
        /**
         * 设置菜单树
         * @param menuTree 菜单树数据
         */
        setMenuTree(menuTree: GetUserMenuTreeVo[]) {
          this.menuTree = menuTree;
        },
      },
    });

    return menuStore();
  },

  /**
   * 使用菜单服务
   */
  useMenuService() {
    //先获取菜单存储
    const menuStore = this.useMenuStore();

    //获取路由
    const route = useRoute();

    //加载菜单树是否正在加载
    const loading = ref(false);

    /**
     * 加载菜单树到菜单存储
     */
    const loadMenuTree = async () => {
      loading.value = true;
      try {
        const result = await MenuApi.getUserMenuTree();
        if (Result.isSuccess(result)) {
          menuStore.setMenuTree(result.data);
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        console.log("加载菜单树完成");
        loading.value = false;
      }
    };

    /**
     * 展开菜单
     * @param path 路径
     */
    const expandMenu = (menuId: string) => {
      menuStore.menuOpened.push(menuId);
    };

    /**
     * 折叠菜单
     * @param menuId 菜单ID
     */
    const collapseMenu = (menuId: string) => {
      menuStore.menuOpened.splice(menuStore.menuOpened.indexOf(menuId), 1);
    };

    /**
     * 根据路径获取菜单
     * @param path 路径
     * @returns 菜单对象GetUserMenuTreeVo | null:未找到
     */
    const getMenuByPath = (path: string, nodes?: GetUserMenuTreeVo[]): GetUserMenuTreeVo | null => {
      const list = nodes ?? menuStore.getMenuTree;
      for (const node of list) {
        if (node.menuPath === path) {
          return node;
        }
        if (node.children?.length) {
          const found = getMenuByPath(path, node.children);
          if (found) {
            return found;
          }
        }
      }
      return null;
    };

    /**
     * 过滤目录类型菜单
     * @param menuTree 菜单树数据
     * @returns 过滤后的目录类型菜单数据
     */
    const filterDirectoryMenu = (menuTree: GetUserMenuTreeVo[]): GetUserMenuTreeVo[] => {
      return menuTree.filter((item) => item.menuKind === 0);
    };

    /**
     * 过滤菜单项类型菜单
     * @param menuTree 菜单树数据
     * @returns 过滤后的菜单类型菜单数据
     */
    const filterItemMenu = (menuTree: GetUserMenuTreeVo[]): GetUserMenuTreeVo[] => {
      return menuTree.filter((item) => item.menuKind === 1);
    };

    /**
     * 根据路由路径计算当前活动菜单ID
     */
    const getMenuIdByPath = (items: GetUserMenuTreeVo[], path: string): any => {
      for (const item of items) {
        if (item.menuPath === path) {
          return item.id;
        }
        if (item.children?.length) {
          const foundId = getMenuIdByPath(item.children, path);
          if (foundId) return foundId;
        }
      }
      return "";
    };

    /**
     * 当前活动菜单ID
     */
    const activeMenuId = computed(() => {
      return getMenuIdByPath(menuStore.getMenuTree, route.path);
    });

    return {
      loading,
      loadMenuTree,
      getMenuByPath,
      filterDirectoryMenu,
      filterItemMenu,
      expandMenu,
      collapseMenu,
      activeMenuId,
    };
  },
};
