import { ref, type Ref } from "vue";
import type { GetPolyModelListDto, GetPolyModelListVo, EditPolyModelDto } from "@/views/assembly/api/PolyModelApi";
import PolyModelApi from "@/views/assembly/api/PolyModelApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

export default {
  /**
   * 按输出方案ID过滤的聚合模型列表
   */
  usePolyModelList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetPolyModelListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetPolyModelListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async (): Promise<void> => {
      listLoading.value = false;
      listForm.value.outputSchemaId = outputSchemaId.value;
      const result = await PolyModelApi.getPolyModelList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    const syncPolyModelFromOrigin = async (): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定从原始模型同步聚合模型吗？已有字段将被覆盖。", "提示", {
          closeOnClickModal: false,
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await PolyModelApi.syncPolyModelFromOriginBySchema({ id: outputSchemaId.value });
        ElMessage.success("同步成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    const removeList = async (row: GetPolyModelListVo): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await PolyModelApi.removePolyModel({ id: row.id });
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
      syncPolyModelFromOrigin,
    };
  },

  /**
   * 聚合模型表格行内编辑
   */
  usePolyModelCellEdit() {
    const buildEditDto = (row: GetPolyModelListVo): EditPolyModelDto => ({
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

    const submitRow = async (row: GetPolyModelListVo): Promise<boolean> => {
      const editDto = buildEditDto(row);

      try {
        await PolyModelApi.editPolyModel(editDto);
        return true;
      } catch (error: any) {
        ElMessage.error(error.message);
        return false;
      }
    };

    const commitField = async (row: GetPolyModelListVo, field: string, newValue: any): Promise<boolean> => {
      const oldValue = (row as any)[field];
      if (JSON.stringify(newValue) === JSON.stringify(oldValue)) {
        return false;
      }

      (row as any)[field] = newValue;
      const editDto = buildEditDto(row);

      try {
        await PolyModelApi.editPolyModel(editDto);
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
