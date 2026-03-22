import { onMounted, ref, type Ref } from "vue";
import type { GetOutModelOriginListDto, GetOutModelOriginListVo } from "@/views/assembly/api/OutModelOriginApi";
import OutModelOriginApi from "@/views/assembly/api/OutModelOriginApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage } from "element-plus";
import type { GetOutSchemaListVo } from "../api/OutSchemaApi";

export default {
  /**
   * 输出方案原始模型表列表管理
   */
  useOutModelOriginList(outputSchema: GetOutSchemaListVo, cdrcReturn: () => void) {
    const listForm = ref<GetOutModelOriginListDto>({
      outputSchemaId: null,
    });

    const listData = ref<GetOutModelOriginListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async (): Promise<void> => {
      listLoading.value = true;
      const result = await OutModelOriginApi.getOutModelOriginList(listForm.value);

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
      //如果输出方案无效 则回退
      if (!outputSchema) {
        cdrcReturn();
        return;
      }

      //设置输出方案ID
      listForm.value.outputSchemaId = outputSchema.id;
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
