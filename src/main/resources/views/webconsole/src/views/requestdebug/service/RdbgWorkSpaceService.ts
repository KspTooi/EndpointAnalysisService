import { computed, onMounted, ref, watch, type Ref } from "vue";
import type { GetCollectionDetailsVo, GetCollectionTreeVo } from "../api/CollectionApi";
import CollectionApi from "../api/CollectionApi";
import { Result } from "@/commons/entity/Result";
import { useRdbgStore } from "./RdbgStore";
import { ElMessage } from "element-plus";

const rdbgStore = useRdbgStore();

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

  /**
   * 请求集合详情功能打包
   */
  useCollectionDetails: () => {
    const details = ref<GetCollectionDetailsVo>(null);
    const detailsLoading = ref<boolean>(false);

    const loadDetails = async (collectionId: string) => {
      detailsLoading.value = true;
      try {
        const res = await CollectionApi.getCollectionDetails({ id: collectionId });
        if (Result.isSuccess(res)) {
          details.value = res.data;
        }
      } catch (error) {
        console.error(error);
      } finally {
        detailsLoading.value = false;
      }
    };

    const saveDetails = async (details: GetCollectionDetailsVo) => {
      detailsLoading.value = true;
      try {
        const res = await CollectionApi.editCollection(details);
        if (Result.isSuccess(res)) {
          ElMessage.success("已成功保存: " + details.name);
        }
      } catch (error) {
        console.error(error);
        ElMessage.error("保存失败: " + error.message);
      } finally {
        detailsLoading.value = false;
      }
    };

    /**
     * 监听当前激活的集合
     * 当当前激活的集合发生变化时 加载集合详情
     */
    watch(
      () => rdbgStore.getActiveCollection,
      (newVal) => {
        if (newVal) {
          loadDetails(newVal.id);
        }
      }
    );

    return {
      details,
      detailsLoading,
      loadDetails,
      saveDetails,
    };
  },
};
