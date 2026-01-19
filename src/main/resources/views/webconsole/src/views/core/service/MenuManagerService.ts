import { computed, onMounted, reactive, ref, watch, type Ref } from "vue";
import type { GetMenuDetailsVo, GetMenuTreeDto, GetMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import MenuApi from "@/views/core/api/MenuApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance, type TableInstance } from "element-plus";
import { EventHolder } from "@/store/EventHolder";
import QueryPersistService from "@/service/QueryPersistService";

export default {
  /**
   * 菜单列表打包
   * @param listTableRef 列表表格引用
   */
  useMenuList(listTableRef: Ref<TableInstance>) {
    const listForm = ref<GetMenuTreeDto>({
      name: "",
      menuKind: null,
      permission: "",
    });

    const listExpand = ref(true);
    const listData = ref<GetMenuTreeVo[]>([]);
    const listLoading = ref(true);
    const fullMenuTree = ref<GetMenuTreeVo[]>([]);

    /**
     * 加载菜单列表
     */
    const loadList = async () => {
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
      await loadFullMenuTree();
    };

    /**
     * 重置查询条件
     */
    const resetList = async () => {
      listForm.value.name = "";
      listForm.value.menuKind = null;
      listForm.value.permission = "";
      QueryPersistService.clearQuery("menu-manager");
      await loadList();
    };

    /**
     * 删除菜单
     */
    const removeList = async (id: string) => {
      try {
        await ElMessageBox.confirm("确定删除该菜单吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await MenuApi.removeMenu({ id });
        EventHolder().requestReloadLeftMenu();
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      }
    };

    /**
     * 展开/收起全部
     */
    const listExpandToggle = () => {
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
    const loadFullMenuTree = async () => {
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
    const listExpandToggleInner = (data: GetMenuTreeVo[], expand: boolean) => {
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
      await loadFullMenuTree();
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
   * @param fullMenuTree 完整菜单树
   */
  useMenuModal(modalFormRef: Ref<FormInstance>, loadList: () => void, fullMenuTree: Ref<GetMenuTreeVo[]>) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
    const modalCurrentRow = ref<GetMenuTreeVo | null>(null);
    const modalForm = reactive<GetMenuDetailsVo>({
      id: "",
      parentId: "",
      name: "",
      description: "",
      kind: 0,
      menuKind: 0,
      menuPath: "",
      menuQueryParam: "",
      menuIcon: "",
      menuHidden: 0,
      menuBtnId: "",
      permission: "",
      seq: 0,
    });
    const modalFormLabel = computed(() => {
      if (modalForm.menuKind == 0) {
        return "目录";
      }
      if (modalForm.menuKind == 1) {
        return "菜单";
      }
      if (modalForm.menuKind == 2) {
        return "按钮";
      }
      return "";
    });

    const modalRules = {
      name: [
        { required: true, message: "请输入菜单名称", trigger: "blur" },
        { min: 2, max: 32, message: "菜单名称长度必须在2-32个字符之间", trigger: "blur" },
      ],
      menuKind: [{ required: true, message: "请选择菜单类型", trigger: "blur" }],
      menuPath: [
        { required: true, message: "请输入菜单路径", trigger: "blur" },
        { max: 256, message: "菜单路径长度不能超过256个字符", trigger: "blur" },
      ],
      permission: [{ max: 320, message: "所需权限长度不能超过320个字符", trigger: "blur" }],
      description: [{ max: 200, message: "菜单描述长度不能超过200个字符", trigger: "blur" }],
      seq: [
        { required: true, message: "请输入排序", trigger: "blur" },
        { type: "number", min: 0, max: 655350, message: "排序只能在0-655350之间", trigger: "blur" },
      ],
      menuQueryParam: [{ max: 512, message: "菜单查询参数长度不能超过512个字符", trigger: "blur" }],
      menuIcon: [{ max: 64, message: "菜单图标长度不能超过64个字符", trigger: "blur" }],
      menuHidden: [{ required: true, message: "请选择是否隐藏", trigger: "blur" }],
      menuBtnId: [
        { required: () => modalForm.menuKind == 2, message: "类型为按钮时，按钮ID不能为空", trigger: "blur" },
        { max: 64, message: "按钮ID长度不能超过64个字符", trigger: "blur" },
      ],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param currentRow 当前行
     */
    const openModal = async (mode: "add" | "edit" | "add-item", currentRow: GetMenuTreeVo | null) => {
      modalMode.value = mode;
      modalCurrentRow.value = currentRow;
      resetModal();

      if (mode === "add") {
        modalForm.parentId = "";
      }

      if (mode === "add-item" && currentRow) {
        modalForm.parentId = currentRow.id;

        //当前选项是目录 则首选菜单
        if (modalCurrentRow.value?.menuKind == 0) {
          modalForm.menuKind = 1;
        }

        //当前选项是菜单 则首选按钮
        if (modalCurrentRow.value?.menuKind == 1) {
          modalForm.menuKind = 2;
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
          modalForm.description = ret.data.description;
          modalForm.kind = ret.data.kind;
          modalForm.menuKind = ret.data.menuKind;
          modalForm.menuPath = ret.data.menuPath;
          modalForm.menuQueryParam = ret.data.menuQueryParam;
          modalForm.menuIcon = ret.data.menuIcon;
          modalForm.menuHidden = ret.data.menuHidden;
          modalForm.menuBtnId = ret.data.menuBtnId;
          modalForm.permission = ret.data.permission;
          modalForm.seq = ret.data.seq;
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
    const resetModal = (force: boolean = false) => {
      modalForm.id = "";
      modalForm.name = "";
      modalForm.description = "";
      modalForm.kind = 0;
      modalForm.menuPath = "";
      modalForm.menuQueryParam = "";
      modalForm.menuIcon = "";
      modalForm.menuHidden = 0;
      modalForm.menuBtnId = "";
      modalForm.permission = "";
      modalForm.seq = 0;

      if (force) {
        modalForm.parentId = "";
        modalForm.menuKind = 0;
      }
    };

    /**
     * 提交模态框表单
     */
    const submitModal = async () => {
      //先校验表单
      try {
        await modalFormRef?.value?.validate();
      } catch (error) {
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
        }

        if (modalMode.value === "edit") {
          //如果是目录则将权限设置为null
          if (modalForm.menuKind == 0) {
            modalForm.permission = "";
          }
          await MenuApi.editMenu(modalForm);
          ElMessage.success("操作成功");
        }
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      } finally {
        modalLoading.value = false;
        await loadList();
      }

      //通知左侧菜单重新加载
      EventHolder().requestReloadLeftMenu();
    };

    /**
     * 计算菜单树用于父级选择
     */
    const menuTreeForSelect = computed(() => {
      const currentMenu = modalForm;
      const isEditMode = modalMode.value === "edit";

      const filter = (menuTree: GetMenuTreeVo[]): GetMenuTreeVo[] => {
        return menuTree
          .filter((item) => item.menuKind !== 2) // 按钮不能作为父级
          .map((item) => {
            let disabled = false;

            // 编辑时，节点自身不能作为父级
            if (isEditMode && item.id === currentMenu.id) {
              disabled = true;
            }

            // 根据当前操作的菜单类型，判断父级是否可选
            // 0-目录 1-菜单 2-按钮

            // 当前是目录，父级只能是目录或根节点
            if (currentMenu.menuKind === 0) {
              if (item.menuKind === 1) disabled = true;
            }

            // 当前是菜单，父级只能是目录
            if (currentMenu.menuKind === 1) {
              if (item.menuKind !== 0) disabled = true;
            }

            // 当前是按钮，父级只能是菜单
            if (currentMenu.menuKind === 2) {
              if (item.menuKind !== 1) disabled = true;
            }

            return {
              ...item,
              disabled,
              children: item.children ? filter(item.children) : [],
            };
          });
      };

      let rootDisabled = false;
      // 菜单和按钮不能直接挂在根节点下
      if (currentMenu.menuKind === 1 || currentMenu.menuKind === 2) {
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
     * 监听菜单类型变化 用于模态框改变类型时清空菜单路径、查询参数、权限
     */
    watch(
      () => modalForm.menuKind,
      (newVal: number | null | undefined) => {
        if (newVal == 0) {
          modalForm.menuPath = "";
          modalForm.menuQueryParam = "";
          modalForm.permission = "";
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
