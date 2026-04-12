import { computed, onMounted, reactive, ref, watch, type Ref } from "vue";
import type { GetMenuDetailsVo, GetMenuTreeDto, GetMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import MenuApi from "@/views/core/api/MenuApi.ts";
import { Result } from "@/commons/model/Result";
import { ElMessage, ElMessageBox, type FormInstance, type TableInstance } from "element-plus";
import QueryPersistService from "@/commons/service/QueryPersistService.ts";
import ComMenuService from "@/soa/com-series/service/ComMenuService.ts";

export default {
  /**
   * 菜单列表打包
   * @param listTableRef 列表表格引用
   */
  useMenuList(listTableRef: Ref<TableInstance>) {
    //先加载菜单服务
    const { loadMenus } = ComMenuService.useMenuService();

    const listForm = ref<GetMenuTreeDto>({
      name: "",
      kind: null,
      permissionCode: "",
    });

    const listExpand = ref(true);
    const listData = ref<GetMenuTreeVo[]>([]);
    const listLoading = ref(true);
    const fullMenuTree = ref<GetMenuTreeVo[]>([]);

    /**
     * 加载菜单列表
     */
    const loadList = async (): Promise<void> => {
      listLoading.value = true;
      const result = await MenuApi.getMenuTree(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        QueryPersistService.persistQuery("menu-manager", listForm.value);
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询条件
     */
    const resetList = async (): Promise<void> => {
      listForm.value.name = "";
      listForm.value.kind = null;
      listForm.value.permissionCode = "";
      QueryPersistService.clearQuery("menu-manager");
      await loadList();
    };

    /**
     * 删除菜单
     */
    const removeList = async (id: string): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定删除该菜单吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await MenuApi.removeMenu({ id });
        await loadList();

        //通知左侧菜单重新加载
        loadMenus();
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      }
    };

    /**
     * 展开/收起全部
     */
    const listExpandToggle = (): void => {
      if (listExpand.value) {
        listExpand.value = false;
        listExpandToggleInner(listData.value, false);
        return;
      }
      listExpandToggleInner(listData.value, true);
      listExpand.value = true;
    };

    /**
     * 加载不带条件的完整菜单树用于父级选择
     */
    const loadFullMenuTree = async (): Promise<void> => {
      const result = await MenuApi.getMenuTree({});
      if (Result.isSuccess(result)) {
        fullMenuTree.value = result.data;
      }
    };

    /**
     * 递归展开/收起子级
     * @param data 菜单数据
     * @param expand 是否展开
     */
    const listExpandToggleInner = (data: GetMenuTreeVo[], expand: boolean): void => {
      for (const item of data) {
        listTableRef.value?.toggleRowExpansion(item, expand);
        if (item.children != undefined && item.children.length > 0) {
          listExpandToggleInner(item.children, expand);
        }
      }
    };

    //初始化
    onMounted(async () => {
      //加载查询条件
      QueryPersistService.loadQuery("menu-manager", listForm.value);
      await loadList();
    });

    return {
      listForm,
      listExpand,
      listData,
      listLoading,
      fullMenuTree,
      loadList,
      resetList,
      removeList,
      loadFullMenuTree,
      listExpandToggle,
    };
  },

  /**
   * 菜单模态框打包
   * @param modalFormRef 模态框表单引用
   * @param loadList 列表加载函数
   * @param fullMenuTree 完整菜单树数据
   * @param loadFullMenuTree 加载完整菜单树函数
   */
  useMenuModal(
    modalFormRef: Ref<FormInstance>,
    loadList: () => void,
    fullMenuTree: Ref<GetMenuTreeVo[]>,
    loadFullMenuTree: () => Promise<void>
  ) {
    //先加载菜单服务
    const { loadMenus } = ComMenuService.useMenuService();

    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
    const modalCurrentRow = ref<GetMenuTreeVo | null>(null);
    const modalForm = reactive<GetMenuDetailsVo>({
      id: "",
      parentId: "",
      name: "",
      kind: 0,
      path: "",
      icon: "",
      hide: 0,
      permissionCode: "",
      seq: 0,
      remark: "",
    });
    const modalFormLabel = computed(() => {
      if (modalForm.kind == 0) {
        return "目录";
      }
      if (modalForm.kind == 1) {
        return "菜单";
      }
      if (modalForm.kind == 2) {
        return "按钮";
      }
      return "";
    });

    const modalRules = {
      name: [
        { required: true, message: "请输入菜单名称", trigger: "blur" },
        { min: 2, max: 128, message: "菜单名称长度必须在2-128个字符之间", trigger: "blur" },
      ],
      kind: [{ required: true, message: "请选择菜单类型", trigger: "blur" }],
      path: [
        { required: true, message: "请输入菜单路径", trigger: "blur" },
        { max: 500, message: "菜单路径长度不能超过500个字符", trigger: "blur" },
      ],
      permissionCode: [{ max: 500, message: "所需权限长度不能超过500个字符", trigger: "blur" }],
      remark: [{ max: 500, message: "备注长度不能超过500个字符", trigger: "blur" }],
      seq: [
        { required: true, message: "请输入排序", trigger: "blur" },
        { type: "number", min: 0, max: 655350, message: "排序只能在0-655350之间", trigger: "blur" },
      ],
      icon: [
        { required: true, message: "请选择菜单图标", trigger: "change" },
        { max: 80, message: "菜单图标长度不能超过80个字符", trigger: "blur" },
      ],
      hide: [{ required: true, message: "请选择是否隐藏", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param currentRow 当前行
     */
    const openModal = async (mode: "add" | "edit" | "add-item", currentRow: GetMenuTreeVo | null): Promise<void> => {
      // 打开模态框时加载完整菜单树用于选择父级
      await loadFullMenuTree();

      modalMode.value = mode;
      modalCurrentRow.value = currentRow;
      resetModal();

      if (mode === "add") {
        modalForm.parentId = "";
      }

      if (mode === "add-item" && currentRow) {
        modalForm.parentId = currentRow.id;

        //当前选项是菜单 则首选按钮
        if (modalCurrentRow.value?.kind == 1) {
          modalForm.kind = 2;
        }
      }

      //如果是编辑模式则需要加载详情数据
      if (mode === "edit" && currentRow) {
        const ret = await MenuApi.getMenuDetails({ id: currentRow.id });

        if (Result.isSuccess(ret)) {
          modalForm.id = ret.data.id;
          const parentId = ret.data.parentId;
          modalForm.parentId = "";
          if (parentId != null) {
            modalForm.parentId = parentId;
          }
          modalForm.name = ret.data.name;
          modalForm.kind = ret.data.kind;
          modalForm.path = ret.data.path;
          modalForm.icon = ret.data.icon;
          modalForm.hide = ret.data.hide;
          modalForm.permissionCode = ret.data.permissionCode;
          modalForm.seq = ret.data.seq;
          modalForm.remark = ret.data.remark;
        }

        if (Result.isError(ret)) {
          ElMessage.error(ret.message);
          return;
        }
      }

      modalVisible.value = true;
    };

    /**
     * 重置模态框表单
     * @param force 硬重置，不保留父级ID、菜单类型
     */
    const resetModal = (force: boolean = false): void => {
      modalForm.id = "";
      modalForm.name = "";
      modalForm.path = "";
      modalForm.icon = "";
      modalForm.hide = 0;
      modalForm.permissionCode = "";
      modalForm.seq = 0;
      modalForm.remark = "";

      if (force) {
        modalForm.parentId = "";
        modalForm.kind = 0;
      }
    };

    /**
     * 提交模态框表单
     */
    const submitModal = async (): Promise<void> => {
      //先校验表单
      try {
        await modalFormRef?.value?.validate();
      } catch {
        return;
      }

      modalLoading.value = true;

      //提交表单
      try {
        if (modalMode.value === "add" || modalMode.value === "add-item") {
          await MenuApi.addMenu(modalForm);
          ElMessage.success("操作成功");
          const parentId = modalForm.parentId;
          modalForm.parentId = parentId;
          resetModal();
          modalVisible.value = false;
        }

        if (modalMode.value === "edit") {
          await MenuApi.editMenu(modalForm);
          ElMessage.success("操作成功");
          modalVisible.value = false;
        }
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      } finally {
        modalLoading.value = false;
        await loadList();
      }

      //通知左侧菜单重新加载
      loadMenus();
    };

    /**
     * 计算菜单树用于父级选择
     */
    const menuTreeForSelect = computed(() => {
      const currentMenu = modalForm;
      const isEditMode = modalMode.value === "edit";

      const filter = (menuTree: GetMenuTreeVo[]): GetMenuTreeVo[] => {
        return menuTree
          .filter((item) => item.kind !== 2) // 按钮不能作为父级
          .map((item) => {
            let disabled = false;

            // 编辑时，节点自身不能作为父级
            if (isEditMode && item.id === currentMenu.id) {
              disabled = true;
            }

            // 根据当前操作的菜单类型，判断父级是否可选
            // 0-目录 1-菜单 2-按钮

            // 当前是目录，父级只能是目录或根节点
            if (currentMenu.kind === 0) {
              if (item.kind !== 0) {
                disabled = true;
              }
            }

            // 当前是菜单，父级只能是目录
            if (currentMenu.kind === 1) {
              if (item.kind !== 0) {
                disabled = true;
              }
            }

            // 当前是按钮，父级只能是菜单
            if (currentMenu.kind === 2) {
              if (item.kind !== 1) {
                disabled = true;
              }
            }

            return {
              id: item.id,
              parentId: item.parentId,
              name: item.name,
              kind: item.kind,
              path: item.path,
              icon: item.icon,
              hide: item.hide,
              permissionCode: item.permissionCode,
              missingPermission: item.missingPermission,
              seq: item.seq,
              disabled,
              children: item.children ? filter(item.children) : [],
            };
          });
      };

      let rootDisabled = false;
      // 菜单和按钮不能直接挂在根节点下，目录可以挂在根节点下
      if (currentMenu.kind === 1 || currentMenu.kind === 2) {
        rootDisabled = true;
      }

      return [
        {
          id: "",
          name: "根节点",
          disabled: rootDisabled,
          children: filter(fullMenuTree.value),
        },
      ];
    });

    /**
     * 监听菜单类型变化 用于模态框改变类型时清空菜单路径
     */
    watch(
      () => modalForm.kind,
      (newVal: number | null | undefined) => {
        if (newVal == 0) {
          modalForm.path = "";
        }
      },
      { immediate: true }
    );

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalCurrentRow,
      modalForm,
      modalFormLabel,
      modalRules,
      menuTreeForSelect,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
