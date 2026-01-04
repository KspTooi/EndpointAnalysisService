import { onMounted, ref } from "vue";
import type { GetCollectionTreeVo } from "../api/CollectionApi";
import CollectionApi from "../api/CollectionApi";
import { Result } from "@/commons/entity/Result";

export default {
  /**
   * 请求集合列表功能打包
   */
  useCollectionList: () => {
    const listData = ref<GetCollectionTreeVo[]>([]);
    const listTotal = ref<number>(0);
    const listFilter = ref<string>(null);
    const listLoading = ref<boolean>(false);

    /**
     * 加载请求集合列表
     */
    const loadList = async () => {
      listLoading.value = true;
      try {
        const res = await CollectionApi.getCollectionTree();
        if (Result.isSuccess(res)) {
          listData.value = res.data;
          listTotal.value = res.data.length;
        }
      } catch (error) {
        console.error(error);
      } finally {
        listLoading.value = false;
      }
    };

    onMounted(() => {
      loadList();
    });

    return {
      listData,
      listTotal,
      listFilter,
      listLoading,
      loadList,
    };
  },
};
