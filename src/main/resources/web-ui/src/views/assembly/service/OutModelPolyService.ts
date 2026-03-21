import { ref, type Ref } from "vue";
import type { GetOutModelPolyListDto, GetOutModelPolyListVo, EditOutModelPolyDto } from "@/views/assembly/api/OutModelPolyApi";
import OutModelPolyApi from "@/views/assembly/api/OutModelPolyApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

export default {
  /**
   * 按输出方案ID过滤的列表管理
   */
  useOutModelPolyList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetOutModelPolyListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetOutModelPolyListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async () => {
      listLoading.value = false;
      listForm.value.outputSchemaId = outputSchemaId.value;
      const result = await OutModelPolyApi.getOutModelPolyList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    const syncFromOrigin = async () => {
      try {
        await ElMessageBox.confirm("确定从原始模型同步聚合模型吗？已有字段将被覆盖。", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await OutModelPolyApi.syncFromOriginBySchema({ id: outputSchemaId.value });
        ElMessage.success("同步成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    const removeList = async (row: GetOutModelPolyListVo) => {
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
        await OutModelPolyApi.removeOutModelPoly({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    return {
      listData,
      listLoading,
      loadList,
      removeList,
      syncFromOrigin,
    };
  },

  /**
   * 表格行内编辑提交
   */
  useCellEdit() {
    const buildEditDto = (row: GetOutModelPolyListVo): EditOutModelPolyDto => ({
      id: row.id,
      outputSchemaId: row.outputSchemaId,
      outputModelOriginId: row.outputModelOriginId,
      name: row.name,
      kind: row.kind,
      length: row.length,
      require: row.require,
      policyCrudJson: row.policyCrudJson,
      policyQuery: row.policyQuery,
      policyView: row.policyView,
      remark: row.remark,
      seq: row.seq,
    });

    /**
     * 提交整行当前值到后端（文本类 input blur 时调用）
     */
    const submitRow = async (row: GetOutModelPolyListVo) => {
      const editDto = buildEditDto(row);

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        return true;
      } catch (error: any) {
        ElMessage.error(error.message);
        return false;
      }
    };

    /**
     * 提交指定字段新值（checkbox / select change 时调用）
     */
    const commitField = async (row: GetOutModelPolyListVo, field: string, newValue: any) => {
      const oldValue = (row as any)[field];
      if (JSON.stringify(newValue) === JSON.stringify(oldValue)) return;

      (row as any)[field] = newValue;
      const editDto = buildEditDto(row);

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        return true;
      } catch (error: any) {
        (row as any)[field] = oldValue;
        ElMessage.error(error.message);
        return false;
      }
    };

    return {
      submitRow,
      commitField,
    };
  },
};
