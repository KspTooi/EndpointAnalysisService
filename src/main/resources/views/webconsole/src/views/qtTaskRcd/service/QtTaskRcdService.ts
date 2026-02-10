import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetQtTaskRcdListDto,
  GetQtTaskRcdListVo,
  GetQtTaskRcdDetailsVo,
  AddQtTaskRcdDto,
  EditQtTaskRcdDto,
} from "@/views/qtTaskRcd/api/QtTaskRcdApi.ts";
import QtTaskRcdApi from "@/views/qtTaskRcd/api/QtTaskRcdApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 任务调度日志表列表管理
   */
  useQtTaskRcdList() {
    const listForm = ref<GetQtTaskRcdListDto>({
      pageNum: 1,
      pageSize: 20,
      taskId: "",
      taskName: "",
      groupName: "",
      target: "",
      targetParam: "",
      targetResult: "",
      status: null,
      startTime: "",
      endTime: "",
      costTime: null,
    });

    const listData = ref<GetQtTaskRcdListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await QtTaskRcdApi.getQtTaskRcdList(listForm.value);

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
      listForm.value.taskId = "";
      listForm.value.taskName = "";
      listForm.value.groupName = "";
      listForm.value.target = "";
      listForm.value.targetParam = "";
      listForm.value.targetResult = "";
      listForm.value.status = null;
      listForm.value.startTime = "";
      listForm.value.endTime = "";
      listForm.value.costTime = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetQtTaskRcdListVo) => {
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
        await QtTaskRcdApi.removeQtTaskRcd({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
      await loadList();
    });

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      loadList,
      resetList,
      removeList,
    };
  },

  /**
   * 模态框管理（统一处理新增和编辑）
   */
  useQtTaskRcdModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetQtTaskRcdDetailsVo>({
      id: "",
      taskId: "",
      taskName: "",
      groupName: "",
      target: "",
      targetParam: "",
      targetResult: "",
      status: 0,
      startTime: "",
      endTime: "",
      costTime: 0,
      createTime: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      taskId: [{ required: true, message: "请输入任务ID", trigger: "blur" }],
      taskName: [{ required: true, message: "请输入任务名", trigger: "blur" }],
      target: [{ required: true, message: "请输入调用目标", trigger: "blur" }],
      status: [{ required: true, message: "请输入运行状态 0:正常 1:失败 2:超时 3:已调度", trigger: "blur" }],
      startTime: [{ required: true, message: "请输入运行开始时间", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetQtTaskRcdListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.taskId = "";
        modalForm.taskName = "";
        modalForm.groupName = "";
        modalForm.target = "";
        modalForm.targetParam = "";
        modalForm.targetResult = "";
        modalForm.status = 0;
        modalForm.startTime = "";
        modalForm.endTime = "";
        modalForm.costTime = 0;
        modalForm.createTime = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await QtTaskRcdApi.getQtTaskRcdDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.taskId = details.taskId;
          modalForm.taskName = details.taskName;
          modalForm.groupName = details.groupName;
          modalForm.target = details.target;
          modalForm.targetParam = details.targetParam;
          modalForm.targetResult = details.targetResult;
          modalForm.status = details.status;
          modalForm.startTime = details.startTime;
          modalForm.endTime = details.endTime;
          modalForm.costTime = details.costTime;
          modalForm.createTime = details.createTime;
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
      modalForm.taskId = "";
      modalForm.taskName = "";
      modalForm.groupName = "";
      modalForm.target = "";
      modalForm.targetParam = "";
      modalForm.targetResult = "";
      modalForm.status = 0;
      modalForm.startTime = "";
      modalForm.endTime = "";
      modalForm.costTime = 0;
      modalForm.createTime = "";
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
          const addDto: AddQtTaskRcdDto = {
            taskId: modalForm.taskId,
            taskName: modalForm.taskName,
            groupName: modalForm.groupName,
            target: modalForm.target,
            targetParam: modalForm.targetParam,
            targetResult: modalForm.targetResult,
            status: modalForm.status,
            startTime: modalForm.startTime,
            endTime: modalForm.endTime,
            costTime: modalForm.costTime,
          };
          await QtTaskRcdApi.addQtTaskRcd(addDto);
          ElMessage.success("新增成功");
          modalVisible.value = false;
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
          const editDto: EditQtTaskRcdDto = {
            id: modalForm.id,
            taskId: modalForm.taskId,
            taskName: modalForm.taskName,
            groupName: modalForm.groupName,
            target: modalForm.target,
            targetParam: modalForm.targetParam,
            targetResult: modalForm.targetResult,
            status: modalForm.status,
            startTime: modalForm.startTime,
            endTime: modalForm.endTime,
            costTime: modalForm.costTime,
          };
          await QtTaskRcdApi.editQtTaskRcd(editDto);
          ElMessage.success("编辑成功");
          modalVisible.value = false;
          resetModal();
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
