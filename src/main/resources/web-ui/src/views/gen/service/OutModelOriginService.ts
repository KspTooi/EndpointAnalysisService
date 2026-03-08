import { ref, type Ref } from "vue";
import type { GetOutModelOriginListDto, GetOutModelOriginListVo } from "@/views/gen/api/OutModelOriginApi";
import OutModelOriginApi from "@/views/gen/api/OutModelOriginApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage } from "element-plus";

export default {
  /**
   * 按输出方案ID过滤的列表管理
   */
  useOutModelOriginList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetOutModelOriginListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetOutModelOriginListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async () => {
      listLoading.value = true;
      listForm.value.outputSchemaId = outputSchemaId.value;
      const result = await OutModelOriginApi.getOutModelOriginList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    return {
      listData,
      listLoading,
      loadList,
    };
  },
};
