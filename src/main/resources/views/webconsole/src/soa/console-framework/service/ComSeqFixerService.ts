import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";

export default {
  /**
   * 排序快捷修改弹窗服务
   * @param id 数据ID
   * @param getDetailApi 获取详情接口
   * @param editApi 编辑接口
   * @param seqField 排序字段名称
   * @param onSuccess 成功回调
   * @returns 排序快捷修改弹窗相关状态和方法
   */
  useSeqQuickPopover: (
    id: string,
    getDetailApi: (id: string) => Promise<any>,
    editApi: (id: string, dto: any) => Promise<any>,
    seqField: string,
    onSuccess?: () => void
  ) => {
    const queryForm = reactive({
      seq: 0,
    });
    const loading = ref(false);
    const popoverVisible = ref(false);
    const detailData = ref<any>(null);

    const onBeforeShow = async () => {
      loading.value = true;
      try {
        const res = await getDetailApi(id);
        if (!res) {
          return;
        }
        detailData.value = res;
        queryForm.seq = res[seqField] || 0;
      } catch (error) {
        ElMessage.error("获取数据失败");
        console.error("获取详情失败:", error);
      } finally {
        loading.value = false;
      }
    };

    const onConfirm = async () => {
      loading.value = true;
      try {
        const dto: any = { ...detailData.value };
        dto[seqField] = queryForm.seq;
        await editApi(id, dto);
        ElMessage.success("修改成功");
        popoverVisible.value = false;
        if (onSuccess) {
          onSuccess();
        }
      } catch (error) {
        ElMessage.error("修改失败");
        console.error("修改失败:", error);
      } finally {
        loading.value = false;
      }
    };

    const onCancel = () => {
      popoverVisible.value = false;
    };

    return {
      queryForm,
      loading,
      popoverVisible,
      onBeforeShow,
      onConfirm,
      onCancel,
    };
  },
};
