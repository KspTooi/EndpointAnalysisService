import { onMounted, ref, type Ref } from "vue";
import type { GetRawModelDto, GetRawModelListVo } from "@/views/assembly/api/RawModelApi";
import RawModelApi from "@/views/assembly/api/RawModelApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage } from "element-plus";
import type { GetOpSchemaListVo } from "@/views/assembly/api/OpSchemaApi";

export default {
  /**
   * 输出方案下原始模型列表
   */
  useRawModelList(opSchemaRow: GetOpSchemaListVo, cdrcReturn: () => void) {
    const listForm = ref<GetRawModelDto>({
      outputSchemaId: "",
    });

    const listData = ref<GetRawModelListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    const loadList = async (): Promise<void> => {
      listLoading.value = true;
      const result = await RawModelApi.getRawModelList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    onMounted(async () => {
      if (!opSchemaRow) {
        cdrcReturn();
        return;
      }

      listForm.value.outputSchemaId = opSchemaRow.id;
      await loadList();
    });

    return {
      listData,
      listTotal,
      listLoading,
      loadList,
    };
  },
};
