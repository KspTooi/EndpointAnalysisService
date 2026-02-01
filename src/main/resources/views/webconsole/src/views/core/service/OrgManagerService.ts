import { computed, onMounted, reactive, ref, type Ref } from "vue";
import type { GetOrgTreeVo, GetOrgDetailsVo, AddOrgDto, EditOrgDto } from "@/views/core/api/OrgApi.ts";
import OrgApi from "@/views/core/api/OrgApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance, type TableInstance } from "element-plus";

export default {
  /**
   * 组织机构树打包
   * @param listTableRef 列表表格引用
   */
  useOrgTree(listTableRef: Ref<TableInstance>) {
    const queryForm = reactive({
      name: "",
    });

    const listData = ref<GetOrgTreeVo[]>([]);
    const listLoading = ref(false);

    /**
     * 前端过滤数据
     */
    const filteredData = computed(() => {
      return filterTree(listData.value);
    });

    /**
     * 递归过滤树形数据
     */
    const filterTree = (tree: GetOrgTreeVo[]): GetOrgTreeVo[] => {
      return tree
        .map((node) => {
          const matchesName = !queryForm.name || node.name.includes(queryForm.name);

          const filteredChildren = node.children ? filterTree(node.children) : [];

          if (matchesName) {
            return { ...node, children: filteredChildren };
          }

          if (filteredChildren.length > 0) {
            return { ...node, children: filteredChildren };
          }

          return null;
        })
        .filter((node) => node !== null) as GetOrgTreeVo[];
    };

    /**
     * 用于树形选择器的数据
     */
    const treeSelectData = computed(() => {
      return convertToTreeSelect(listData.value);
    });

    /**
     * 转换为树形选择器格式
     */
    const convertToTreeSelect = (tree: GetOrgTreeVo[]): any[] => {
      return tree.map((node) => ({
        value: node.id,
        label: node.name,
        children: node.children && node.children.length > 0 ? convertToTreeSelect(node.children) : undefined,
      }));
    };

    /**
     * 加载组织机构树
     */
    const loadList = async () => {
      listLoading.value = true;
      try {
        const result = await OrgApi.getOrgTree({ name: queryForm.name });
        listData.value = result;
      } catch (error: any) {
        ElMessage.error(error.message || "获取组织机构树失败");
      }
      listLoading.value = false;
    };

    /**
     * 前端筛选
     */
    const filterData = () => {
      // 前端筛选，不需要重新加载数据
    };

    /**
     * 重置查询条件
     */
    const resetQuery = () => {
      queryForm.name = "";
    };

    /**
     * 删除组织机构
     */
    const removeList = async (id: string) => {
      try {
        await ElMessageBox.confirm("确定删除该组织机构吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await OrgApi.removeOrg({ id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    //初始化
    onMounted(async () => {
      await loadList();
    });

    return {
      queryForm,
      listData,
      listLoading,
      filteredData,
      treeSelectData,
      loadList,
      filterData,
      resetQuery,
      removeList,
    };
  },

  /**
   * 组织机构模态框打包
   * @param modalFormRef 模态框表单引用
   * @param loadList 列表加载函数
   * @param treeSelectData 树形选择器数据
   */
  useOrgModal(modalFormRef: Ref<FormInstance>, loadList: () => void, treeSelectData: Ref<any[]>) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit" | "add-item">("add");
    const modalForm = reactive({
      id: "",
      parentId: null as string | null,
      kind: 0,
      name: "",
      principalId: null as string | null,
      seq: 0,
    });

    const modalRules = {
      kind: [{ required: true, message: "请选择组织机构类型", trigger: "change" }],
      name: [
        { required: true, message: "请输入组织机构名称", trigger: "blur" },
        { min: 1, max: 128, message: "组织机构名称长度必须在1-128个字符之间", trigger: "blur" },
      ],
      seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param row 当前行
     */
    const openModal = async (mode: "add" | "edit" | "add-item", row: GetOrgTreeVo | null) => {
      modalMode.value = mode;
      resetModal();

      if (mode === "add") {
        modalForm.parentId = null;
        modalForm.kind = 1; // 顶级默认为企业
      }

      if (mode === "add-item" && row) {
        modalForm.parentId = row.id;
        modalForm.kind = 0; // 子级默认为部门
      }

      //如果是编辑模式则需要加载详情数据
      if (mode === "edit" && row) {
        try {
          const ret = await OrgApi.getOrgDetails({ id: row.id });
          modalForm.id = ret.id;
          modalForm.parentId = ret.parentId;
          modalForm.kind = ret.kind;
          modalForm.name = ret.name;
          modalForm.principalId = ret.principalId;
          modalForm.seq = ret.seq;
        } catch (error: any) {
          ElMessage.error(error.message || "获取组织机构详情失败");
          return;
        }
      }

      modalVisible.value = true;
    };

    /**
     * 重置模态框表单
     */
    const resetModal = () => {
      modalForm.id = "";
      modalForm.parentId = null;
      modalForm.kind = 0;
      modalForm.name = "";
      modalForm.principalId = null;
      modalForm.seq = 0;

      if (modalFormRef.value) {
        modalFormRef.value.resetFields();
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
          const addDto: AddOrgDto = {
            parentId: modalForm.parentId,
            kind: modalForm.kind,
            name: modalForm.name,
            principalId: modalForm.principalId,
            seq: modalForm.seq,
          };
          const result = await OrgApi.addOrg(addDto);
          if (Result.isSuccess(result)) {
            ElMessage.success("操作成功");
            resetModal();
          }
          if (Result.isError(result)) {
            ElMessage.error(result.message);
            return;
          }
        }

        if (modalMode.value === "edit") {
          const editDto: EditOrgDto = {
            id: modalForm.id,
            parentId: modalForm.parentId,
            name: modalForm.name,
            principalId: modalForm.principalId,
            seq: modalForm.seq,
          };
          const result = await OrgApi.editOrg(editDto);
          if (Result.isSuccess(result)) {
            ElMessage.success("操作成功");
          }
          if (Result.isError(result)) {
            ElMessage.error(result.message);
            return;
          }
        }
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      } finally {
        modalLoading.value = false;
      }

      await loadList();
    };

    /**
     * 组织机构类型名称
     */
    const modalKindName = computed(() => {
      if (modalForm.kind == 1) {
        return "企业";
      }
      return "部门";
    });

    return {
      modalKindName,
      modalKind: computed(() => {
        return modalForm.kind;
      }),
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      modalRules,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
