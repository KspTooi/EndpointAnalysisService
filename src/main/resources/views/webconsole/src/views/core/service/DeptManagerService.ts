import { computed, onMounted, reactive, ref, type Ref } from "vue";
import type { GetDeptTreeVo, GetDeptDetailsVo, AddDeptDto, EditDeptDto } from "@/views/core/api/DeptApi.ts";
import DeptApi from "@/views/core/api/DeptApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance, type TableInstance } from "element-plus";

export default {
  /**
   * 部门树打包
   * @param listTableRef 列表表格引用
   */
  useDeptTree(listTableRef: Ref<TableInstance>) {
    const queryForm = reactive({
      name: "",
      status: null as number | null,
    });

    const listData = ref<GetDeptTreeVo[]>([]);
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
    const filterTree = (tree: GetDeptTreeVo[]): GetDeptTreeVo[] => {
      return tree
        .map((node) => {
          const matchesName = !queryForm.name || node.name.includes(queryForm.name);
          const matchesStatus = queryForm.status === null || queryForm.status === undefined || node.status === queryForm.status;

          const filteredChildren = node.children ? filterTree(node.children) : [];

          if (matchesName && matchesStatus) {
            return { ...node, children: filteredChildren };
          }

          if (filteredChildren.length > 0) {
            return { ...node, children: filteredChildren };
          }

          return null;
        })
        .filter((node) => node !== null) as GetDeptTreeVo[];
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
    const convertToTreeSelect = (tree: GetDeptTreeVo[]): any[] => {
      return tree.map((node) => ({
        value: node.id,
        label: node.name,
        children: node.children && node.children.length > 0 ? convertToTreeSelect(node.children) : undefined,
      }));
    };

    /**
     * 加载部门树
     */
    const loadList = async () => {
      listLoading.value = true;
      try {
        const result = await DeptApi.getDeptTree();
        listData.value = result;
      } catch (error: any) {
        ElMessage.error(error.message || "获取部门树失败");
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
      queryForm.status = null;
    };

    /**
     * 删除部门
     */
    const removeList = async (id: string) => {
      try {
        await ElMessageBox.confirm("确定删除该部门吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await DeptApi.removeDept({ id });
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
   * 部门模态框打包
   * @param modalFormRef 模态框表单引用
   * @param loadList 列表加载函数
   * @param treeSelectData 树形选择器数据
   */
  useDeptModal(modalFormRef: Ref<FormInstance>, loadList: () => void, treeSelectData: Ref<any[]>) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit" | "add-item">("add");
    const modalForm = reactive({
      id: "",
      parentId: null as string | null,
      name: "",
      principalId: null as string | null,
      status: 0,
      seq: 0,
    });

    const modalRules = {
      name: [
        { required: true, message: "请输入部门名称", trigger: "blur" },
        { min: 1, max: 32, message: "部门名称长度必须在1-32个字符之间", trigger: "blur" },
      ],
      status: [{ required: true, message: "请选择部门状态", trigger: "change" }],
      seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param row 当前行
     */
    const openModal = async (mode: "add" | "edit" | "add-item", row: GetDeptTreeVo | null) => {
      modalMode.value = mode;
      resetModal();

      if (mode === "add") {
        modalForm.parentId = null;
      }

      if (mode === "add-item" && row) {
        modalForm.parentId = row.id;
      }

      //如果是编辑模式则需要加载详情数据
      if (mode === "edit" && row) {
        try {
          const ret = await DeptApi.getDeptDetails({ id: row.id });
          modalForm.id = ret.id;
          modalForm.parentId = ret.parentId;
          modalForm.name = ret.name;
          modalForm.principalId = ret.principalId;
          modalForm.status = ret.status;
          modalForm.seq = ret.seq;
        } catch (error: any) {
          ElMessage.error(error.message || "获取部门详情失败");
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
      modalForm.name = "";
      modalForm.principalId = null;
      modalForm.status = 0;
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
          const addDto: AddDeptDto = {
            parentId: modalForm.parentId,
            name: modalForm.name,
            principalId: modalForm.principalId,
            status: modalForm.status,
            seq: modalForm.seq,
          };
          const result = await DeptApi.addDept(addDto);
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
          const editDto: EditDeptDto = {
            id: modalForm.id,
            parentId: modalForm.parentId,
            name: modalForm.name,
            principalId: modalForm.principalId,
            status: modalForm.status,
            seq: modalForm.seq,
          };
          const result = await DeptApi.editDept(editDto);
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

    return {
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
