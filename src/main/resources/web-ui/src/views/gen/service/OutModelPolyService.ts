import { ref, type Ref } from "vue";
import type {
  GetOutModelPolyListDto,
  GetOutModelPolyListVo,
  EditOutModelPolyDto,
} from "@/views/gen/api/OutModelPolyApi";
import OutModelPolyApi from "@/views/gen/api/OutModelPolyApi";
import { Result } from "@/commons/entity/Result";
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
      listLoading.value = true;
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
   * 表格行内单元格编辑
   */
  useCellEdit(reloadCallback: () => Promise<void>) {
    const editingCell = ref<{ rowId: string; field: string } | null>(null);
    const editingValue = ref<any>(null);

    const startEdit = (row: GetOutModelPolyListVo, field: string) => {
      editingCell.value = { rowId: row.id, field };
      editingValue.value = (row as any)[field];
    };

    const cancelEdit = () => {
      editingCell.value = null;
      editingValue.value = null;
    };

    const isEditing = (rowId: string, field: string) => {
      return editingCell.value?.rowId === rowId && editingCell.value?.field === field;
    };

    const submitEdit = async (row: GetOutModelPolyListVo) => {
      if (!editingCell.value) return;
      const field = editingCell.value.field;
      const oldValue = (row as any)[field];
      const newValue = editingValue.value;

      cancelEdit();

      if (JSON.stringify(newValue) === JSON.stringify(oldValue)) return;

      const editDto: EditOutModelPolyDto = {
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
        [field]: newValue,
      };

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        await reloadCallback();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 直接提交指定字段新值（用于 checkbox / select 等 change 事件）
     */
    const commitField = async (row: GetOutModelPolyListVo, field: string, newValue: any) => {
      if (JSON.stringify(newValue) === JSON.stringify((row as any)[field])) return;

      const editDto: EditOutModelPolyDto = {
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
        [field]: newValue,
      };

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        await reloadCallback();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    return {
      editingCell,
      editingValue,
      startEdit,
      cancelEdit,
      isEditing,
      submitEdit,
      commitField,
    };
  },
};
