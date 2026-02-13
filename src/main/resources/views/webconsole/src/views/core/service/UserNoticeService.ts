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
   * 用户通知记录下拉列表打包（支持懒加载）
   */
  useUserNoticeDropList(onCountChange?: () => void) {
    const listData = ref<GetUserNoticeRcdListVo[]>([]);
    const listLoading = ref(false);
    const pageNum = ref(1);
    const pageSize = ref(5);
    const listTotal = ref(0);

    // 是否没有更多数据
    const noMore = computed(() => {
      return listData.value.length >= listTotal.value && listTotal.value > 0;
    });

    // 是否禁用加载
    const disabled = computed(() => {
      return listLoading.value || noMore.value;
    });

    /**
     * 加载更多数据
     */
    const loadMore = async () => {
      if (disabled.value) {
        return;
      }

      listLoading.value = true;
      try {
        const params: GetUserNoticeRcdListDto = {
          pageNum: pageNum.value,
          pageSize: pageSize.value,
        };

        const result = await NoticeRcdApi.getUserNoticeRcdList(params);

        if (Result.isSuccess(result)) {
          if (pageNum.value === 1) {
            listData.value = result.data;
          } else {
            listData.value.push(...result.data);
          }
          listTotal.value = result.total;

          // 如果有数据返回，页码加1
          if (result.data.length > 0) {
            pageNum.value++;
            // 因为查询到的记录变为了已读，所以刷新未读数量
            if (onCountChange) {
              setTimeout(() => {
                onCountChange();
              }, 500);
            }
          }
        }
      } catch (error) {
        // ElMessage.error(error.message);
      } finally {
        listLoading.value = false;
      }
    };

    /**
     * 重置列表数据
     */
    const resetList = () => {
      listData.value = [];
      pageNum.value = 1;
      listTotal.value = 0;
    };

    return {
      listData,
      listLoading,
      listTotal,
      noMore,
      disabled,
      loadMore,
      resetList,
    };
  },

  /**
   * 用户通知记录模态框打包
   */
  useUserNoticeModal() {},
};
