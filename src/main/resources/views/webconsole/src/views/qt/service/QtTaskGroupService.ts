import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetQtTaskGroupListDto,
  GetQtTaskGroupListVo,
  GetQtTaskGroupDetailsVo,
  AddQtTaskGroupDto,
  EditQtTaskGroupDto,
} from "@/views/qt/api/QtTaskGroupApi.ts";
import QtTaskGroupApi from "@/views/qt/api/QtTaskGroupApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 任务分组列表管理
   */
  useQtTaskGroupList() {
    const listForm = ref<GetQtTaskGroupListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      remark: "",
    });

    const listData = ref<GetQtTaskGroupListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);
    const listSelected = ref<GetQtTaskGroupListVo[]>([]);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await QtTaskGroupApi.getQtTaskGroupList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询
     */
    const resetList = () => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 20;
      listForm.value.name = "";
      listForm.value.remark = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetQtTaskGroupListVo) => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await QtTaskGroupApi.removeQtTaskGroup({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 批量删除选中项
     */
    const removeListBatch = async () => {
      if (listSelected.value.length === 0) {
        ElMessage.warning("请先选择要删除的记录");
        return;
      }

      try {
        await ElMessageBox.confirm(`确定要删除选中的 ${listSelected.value.length} 项吗？`, "提示", {
          type: "warning",
          confirmButtonText: "确定",
          cancelButtonText: "取消",
        });
      } catch (error) {
        return;
      }

      try {
        const ids = listSelected.value.map((item) => item.id);
        await QtTaskGroupApi.removeQtTaskGroup({ ids });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message || "删除失败");
      }
    };

    /**
     * 表格选中项变化事件
     */
    const onSelectionChange = (rows: GetQtTaskGroupListVo[]) => {
      listSelected.value = rows;
    };

    onMounted(async () => {
      await loadList();
    });

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      listSelected,
      loadList,
      resetList,
      removeList,
      removeListBatch,
      onSelectionChange,
    };
  },

  /**
   * 模态框管理（统一处理新增和编辑）
   */
  useQtTaskGroupModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetQtTaskGroupDetailsVo>({
      id: "",
      name: "",
      remark: "",
      createTime: "",
      creatorId: "",
      updateTime: "",
      updaterId: "",
      deleteTime: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "分组名不能为空", trigger: "blur" },
        { max: 80, message: "分组名长度不能超过80个字符", trigger: "blur" },
      ],
      remark: [{ max: 1000, message: "分组备注长度不能超过1000个字符", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetQtTaskGroupListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.remark = "";
        modalForm.createTime = "";
        modalForm.creatorId = "";
        modalForm.updateTime = "";
        modalForm.updaterId = "";
        modalForm.deleteTime = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await QtTaskGroupApi.getQtTaskGroupDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.remark = details.remark;
          modalForm.createTime = details.createTime;
          modalForm.creatorId = details.creatorId;
          modalForm.updateTime = details.updateTime;
          modalForm.updaterId = details.updaterId;
          modalForm.deleteTime = details.deleteTime;
          modalVisible.value = true;
        } catch (error: any) {
          ElMessage.error(error.message);
        }
      }
    };

    /**
     * 重置模态框
     */
    const resetModal = () => {
      if (!modalFormRef.value) {
        return;
      }
      modalFormRef.value.resetFields();
      modalForm.id = "";
      modalForm.name = "";
      modalForm.remark = "";
      modalForm.createTime = "";
      modalForm.creatorId = "";
      modalForm.updateTime = "";
      modalForm.updaterId = "";
      modalForm.deleteTime = "";
    };

    /**
     * 提交模态框
     */
    const submitModal = async () => {
      if (!modalFormRef.value) {
        return;
      }

      try {
        await modalFormRef.value.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      if (modalMode.value === "add") {
        try {
          const addDto: AddQtTaskGroupDto = {
            name: modalForm.name,
            remark: modalForm.remark,
          };
          await QtTaskGroupApi.addQtTaskGroup(addDto);
          ElMessage.success("新增成功");
          //modalVisible.value = false;
          resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
        return;
      }

      if (modalMode.value === "edit") {
        if (!modalForm.id) {
          ElMessage.error("缺少ID参数");
          modalLoading.value = false;
          return;
        }

        try {
          const editDto: EditQtTaskGroupDto = {
            id: modalForm.id,
            name: modalForm.name,
            remark: modalForm.remark,
          };
          await QtTaskGroupApi.editQtTaskGroup(editDto);
          ElMessage.success("编辑成功");
          //modalVisible.value = false;
          //resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
      }
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
