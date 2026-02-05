import { onMounted, ref } from "vue";
import type { GetAuditLoginListDto, GetAuditLoginListVo } from "@/views/audit/api/AuditLoginApi.ts";
import AuditLoginApi from "@/views/audit/api/AuditLoginApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService";

export default {
  /**
   * 登录审计列表打包
   */
  useAuditLoginList() {
    const listForm = ref<GetAuditLoginListDto>({
      pageNum: 1,
      pageSize: 10,
      username: "",
      status: "",
    });

    const listData = ref<GetAuditLoginListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await AuditLoginApi.getAuditLoginList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
        QueryPersistService.persistQuery("audit-login-rcd", listForm.value);
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
      listForm.value.pageSize = 10;
      listForm.value.username = "";
      listForm.value.status = "";
      QueryPersistService.clearQuery("audit-login-rcd");
      loadList();
    };

    /**
     * 删除日志
     */
    const removeList = async (row: GetAuditLoginListVo) => {
      try {
        await ElMessageBox.confirm("确定删除该条审计日志吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await AuditLoginApi.removeAuditLogin({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
      QueryPersistService.loadQuery("audit-login-rcd", listForm.value);
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
};
