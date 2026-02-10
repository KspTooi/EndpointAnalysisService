import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetQtTaskListDto,
  GetQtTaskListVo,
  GetQtTaskDetailsVo,
  AddQtTaskDto,
  EditQtTaskDto,
  GetLocalBeanListVo,
} from "@/views/qt/api/QtTaskApi.ts";
import QtTaskApi from "@/views/qt/api/QtTaskApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 任务调度表列表管理
   */
  useQtTaskList() {
    const listForm = ref<GetQtTaskListDto>({
      pageNum: 1,
      pageSize: 20,
      groupId: "",
      name: "",
      status: null,
    });

    const listData = ref<GetQtTaskListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await QtTaskApi.getQtTaskList(listForm.value);

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
      listForm.value.groupId = "";
      listForm.value.name = "";
      listForm.value.status = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetQtTaskListVo) => {
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
        await QtTaskApi.removeQtTask({ id: row.id });
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
  useQtTaskModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetQtTaskDetailsVo>({
      id: "",
      groupId: "",
      groupName: "",
      name: "",
      kind: 0,
      cron: "",
      target: "",
      targetParam: "",
      reqMethod: "",
      concurrent: 0,
      policyMisfire: 0,
      policyError: "",
      policyRcd: "",
      expireTime: "",
      lastExecStatus: 0,
      lastStartTime: "",
      lastEndTime: "",
      status: 0,
      createTime: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [{ required: true, message: "请输入任务名", trigger: "blur" }],
      kind: [{ required: true, message: "请选择任务类型", trigger: "blur" }],
      target: [{ required: true, message: "请输入调用目标", trigger: "blur" }],
      cron: [{ required: true, message: "请输入CRON表达式", trigger: "blur" }],
      concurrent: [{ required: true, message: "请选择并发执行", trigger: "blur" }],
      policyMisfire: [{ required: true, message: "请选择过期策略", trigger: "blur" }],
      status: [{ required: true, message: "请选择状态", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetQtTaskListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.groupId = "";
        modalForm.groupName = "";
        modalForm.name = "";
        modalForm.kind = 0;
        modalForm.cron = "";
        modalForm.target = "";
        modalForm.targetParam = "";
        modalForm.reqMethod = "";
        modalForm.concurrent = 0;
        modalForm.policyMisfire = 0;
        modalForm.policyError = "";
        modalForm.policyRcd = "";
        modalForm.expireTime = "";
        modalForm.lastExecStatus = 0;
        modalForm.lastStartTime = "";
        modalForm.lastEndTime = "";
        modalForm.status = 0;
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
          const details = await QtTaskApi.getQtTaskDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.groupId = details.groupId;
          modalForm.groupName = details.groupName;
          modalForm.name = details.name;
          modalForm.kind = details.kind;
          modalForm.cron = details.cron;
          modalForm.target = details.target;
          modalForm.targetParam = details.targetParam;
          modalForm.reqMethod = details.reqMethod;
          modalForm.concurrent = details.concurrent;
          modalForm.policyMisfire = details.policyMisfire;
          modalForm.policyError = details.policyError;
          modalForm.policyRcd = details.policyRcd;
          modalForm.expireTime = details.expireTime;
          modalForm.lastExecStatus = details.lastExecStatus;
          modalForm.lastStartTime = details.lastStartTime;
          modalForm.lastEndTime = details.lastEndTime;
          modalForm.status = details.status;
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
      modalForm.groupId = "";
      modalForm.groupName = "";
      modalForm.name = "";
      modalForm.kind = 0;
      modalForm.cron = "";
      modalForm.target = "";
      modalForm.targetParam = "";
      modalForm.reqMethod = "";
      modalForm.concurrent = 0;
      modalForm.policyMisfire = 0;
      modalForm.policyError = "";
      modalForm.policyRcd = "";
      modalForm.expireTime = "";
      modalForm.lastExecStatus = 0;
      modalForm.lastStartTime = "";
      modalForm.lastEndTime = "";
      modalForm.status = 0;
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
          const addDto: AddQtTaskDto = {
            groupId: modalForm.groupId,
            name: modalForm.name,
            kind: modalForm.kind,
            cron: modalForm.cron,
            target: modalForm.target,
            targetParam: modalForm.targetParam,
            reqMethod: modalForm.reqMethod,
            concurrent: modalForm.concurrent,
            policyMisfire: modalForm.policyMisfire,
            policyError: modalForm.policyError,
            policyRcd: modalForm.policyRcd,
            expireTime: modalForm.expireTime,
            status: modalForm.status,
          };
          await QtTaskApi.addQtTask(addDto);
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
          const editDto: EditQtTaskDto = {
            id: modalForm.id,
            groupId: modalForm.groupId,
            groupName: modalForm.groupName,
            name: modalForm.name,
            kind: modalForm.kind,
            cron: modalForm.cron,
            target: modalForm.target,
            targetParam: modalForm.targetParam,
            reqMethod: modalForm.reqMethod,
            concurrent: modalForm.concurrent,
            policyMisfire: modalForm.policyMisfire,
            policyError: modalForm.policyError,
            policyRcd: modalForm.policyRcd,
            expireTime: modalForm.expireTime,
            status: modalForm.status,
          };
          await QtTaskApi.editQtTask(editDto);
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

  /**
   * 本地任务Bean列表管理
   */
  useLocalBeanList() {
    const listData = ref<GetLocalBeanListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async () => {
      try {
        listLoading.value = true;
        const result = await QtTaskApi.getLocalBeanList();
        if (Result.isSuccess(result)) {
          listData.value = result.data;
        }
        if (Result.isError(result)) {
          ElMessage.error(result.message);
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        listLoading.value = false;
      }
    };

    /*  onMounted(async () => {
      await loadList();
    }); */

    return {
      listData,
      listLoading,
      loadList,
    };
  },
};
