import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type { GetQtTaskRcdListDto, GetQtTaskRcdListVo, GetQtTaskRcdDetailsVo } from "@/views/qt/api/QtTaskRcdApi.ts";
import QtTaskRcdApi from "@/views/qt/api/QtTaskRcdApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService.ts";

/**
 * 模态框模式类型
 */
type ModalMode = "view";

export default {
  /**
   * 任务调度日志表列表管理
   */
  useQtTaskRcdList() {
    const listForm = ref<GetQtTaskRcdListDto>({
      pageNum: 1,
      pageSize: 20,
      id: "",
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
      createTime: "",
    });

    const listData = ref<GetQtTaskRcdListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);
    const listSelected = ref<GetQtTaskRcdListVo[]>([]);

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
      
      // 持久化查询条件
      QueryPersistService.persistQuery("qt-task-rcd", listForm.value);
    };

    /**
     * 重置查询
     */
    const resetList = () => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 20;
      listForm.value.id = "";
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
      listForm.value.createTime = "";
      
      // 清除持久化的查询条件
      QueryPersistService.clearQuery("qt-task-rcd");
      
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
        await QtTaskRcdApi.removeQtTaskRcd({ ids });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message || "删除失败");
      }
    };

    /**
     * 表格选中项变化事件
     */
    const onSelectionChange = (rows: GetQtTaskRcdListVo[]) => {
      listSelected.value = rows;
    };

    onMounted(async () => {
      // 加载持久化的查询条件
      QueryPersistService.loadQuery("qt-task-rcd", listForm.value);
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
   * 模态框管理（仅支持查看）
   */
  useQtTaskRcdModal() {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("view");
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
    });

    /**
     * 打开模态框（查看模式）
     * @param row 要查看的行数据
     */
    const openModal = async (row: GetQtTaskRcdListVo) => {
      modalMode.value = "view";

      if (!row) {
        ElMessage.error("未选择要查看的数据");
        return;
      }

      try {
        modalLoading.value = true;
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
        modalVisible.value = true;
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        modalLoading.value = false;
      }
    };

    /**
     * 重置模态框
     */
    const resetModal = () => {
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
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      openModal,
      resetModal,
    };
  },
};
