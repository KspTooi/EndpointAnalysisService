import { computed, ref } from "vue";
import type { GetUserNoticeRcdListDto, GetUserNoticeRcdListVo } from "../api/NoticeRcdApi";
import NoticeRcdApi from "../api/NoticeRcdApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage } from "element-plus";

export default {
  /**
   * 用户通知记录数量打包
   */
  useUserNoticeCount() {
    const count = ref(0);
    const countLoading = ref(false);

    //处理后的count (超过99显示99+)
    const processedCount = computed(() => {
      return count.value > 99 ? 99 + "+" : count.value;
    });

    const loadCount = async () => {
      countLoading.value = true;
      try {
        const result = await NoticeRcdApi.getUserNoticeCount();
        count.value = result;
      } catch (error) {
        //ElMessage.error(error.message);
      } finally {
        countLoading.value = false;
      }
    };

    return {
      count,
      countLoading,
      processedCount,
      loadCount,
    };
  },

  /**
   * 用户通知记录列表打包
   */
  useUserNoticeList() {
    const listForm = ref<GetUserNoticeRcdListDto>({
      pageNum: 1,
      pageSize: 20,
    });

    const listData = ref<GetUserNoticeRcdListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    const loadList = async (pn?: number, ps?: number) => {
      listForm.value.pageNum = pn ?? 1;
      listForm.value.pageSize = ps ?? 20;
      listLoading.value = true;

      try {
        const result = await NoticeRcdApi.getUserNoticeRcdList(listForm.value);

        if (Result.isSuccess(result)) {
          listData.value = result.data;
          listTotal.value = result.total;
        }
      } catch (error) {
        //ElMessage.error(error.message);
      } finally {
        listLoading.value = false;
      }
    };

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      loadList,
    };
  },

  /**
   * 用户通知记录模态框打包
   */
  useUserNoticeModal() {},
};
