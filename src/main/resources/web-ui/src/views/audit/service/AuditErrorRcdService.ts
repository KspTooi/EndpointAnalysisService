import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetAuditErrorRcdListDto,
  GetAuditErrorRcdListVo,
  GetAuditErrorRcdDetailsVo,
} from "@/views/audit/api/AuditErrorRcdApi.ts";
import AuditErrorRcdApi from "@/views/audit/api/AuditErrorRcdApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit" | "view";

export default {
  /**
   * 系统错误记录列表管理
   */
  useAuditErrorRcdList() {
    const listForm = ref<GetAuditErrorRcdListDto>({
      pageNum: 1,
      pageSize: 20,
      requestUri: "",
      userId: "",
      userName: "",
      errorType: "",
      errorMessage: "",
    });

    const listData = ref<GetAuditErrorRcdListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await AuditErrorRcdApi.getAuditErrorRcdList(listForm.value);

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
      listForm.value.errorCode = "";
      listForm.value.requestUri = "";
      listForm.value.userId = "";
      listForm.value.userName = "";
      listForm.value.errorType = "";
      listForm.value.errorMessage = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetAuditErrorRcdListVo) => {
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
        await AuditErrorRcdApi.removeAuditErrorRcd({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 批量删除记录
     */
    const removeListBatch = async (selectedItems: GetAuditErrorRcdListVo[]) => {
      if (selectedItems.length === 0) {
        ElMessage.warning("请选择要删除的记录");
        return;
      }

      try {
        await ElMessageBox.confirm(`确定删除选中的${selectedItems.length}条错误记录吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        const ids = selectedItems.map((item) => ({ id: item.id }));
        for (const item of ids) {
          await AuditErrorRcdApi.removeAuditErrorRcd(item);
        }
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
      removeListBatch,
    };
  },

  /**
   * 模态框管理（统一处理新增和编辑）
   */
  useAuditErrorRcdModal(reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetAuditErrorRcdDetailsVo>({
      id: "",
      errorCode: "",
      requestUri: "",
      userId: "",
      userName: "",
      errorType: "",
      errorMessage: "",
      errorStackTrace: "",
      createTime: "",
    });

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetAuditErrorRcdListVo | null) => {
      modalMode.value = mode;

      if (mode === "view") {
        if (!row) {
          ElMessage.error("未选择要查看的数据");
          return;
        }

        try {
          const details = await AuditErrorRcdApi.getAuditErrorRcdDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.requestUri = details.requestUri;
          modalForm.errorCode = details.errorCode;
          modalForm.userName = details.userName;
          modalForm.errorType = details.errorType;
          modalForm.errorMessage = details.errorMessage;
          modalForm.errorStackTrace = details.errorStackTrace;
          modalForm.createTime = details.createTime;
          modalVisible.value = true;
        } catch (error: any) {
          ElMessage.error(error.message);
        }
      }
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      openModal,
    };
  },
};
