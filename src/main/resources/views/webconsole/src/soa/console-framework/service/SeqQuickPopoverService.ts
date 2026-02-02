import { reactive, ref } from "vue";

export default {
  /**
   * 排序快捷修改弹窗服务
   * @param getDetailApi 获取详情接口
   * @param editApi 编辑接口
   * @param seqField 排序字段名称
   * @returns 排序快捷修改弹窗相关状态和方法
   */
  useSeqQuickPopover: (getDetailApi: () => Promise<any>, editApi: (dto: any) => Promise<any>, seqField: string) => {
    const popVisible = ref(false);
    const popSeq = ref(0);
    const loading = ref(false);

    const openPopover = async () => {
      loading.value = true;
      try {
        const res = await getDetailApi();
        if (!res) {
          return;
        }
        popSeq.value = res[seqField] || 0;
        popVisible.value = true;
      } catch (error) {
        console.error('获取详情失败:', error);
      } finally {
        loading.value = false;
      }
    };

    const confirmEdit = async () => {
      loading.value = true;
      try {
        const dto: any = {};
        dto[seqField] = popSeq.value;
        await editApi(dto);
        popVisible.value = false;
      } catch (error) {
        console.error('修改失败:', error);
      } finally {
        loading.value = false;
      }
    };

    return {
      popVisible,
      popSeq,
      loading,
      openPopover,
      confirmEdit,
    };
  },
};
