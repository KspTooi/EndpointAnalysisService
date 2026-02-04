import { onMounted, reactive, ref, type Ref } from "vue";
import type {
  GetEpStdWordDetailsVo,
  GetEpStdWordListDto,
  GetEpStdWordListVo,
  AddEpStdWordDto,
  EditEpStdWordDto,
} from "@/views/document/api/EpStdWordApi.ts";
import EpStdWordApi from "@/views/document/api/EpStdWordApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService.ts";

export default {
  /**
   * 标准词列表打包
   */
  useEpStdWordList() {
    const listForm = ref<GetEpStdWordListDto>({
      pageNum: 1,
      pageSize: 10,
      sourceName: null,
      sourceNameFull: null,
      targetName: null,
      targetNameFull: null,
      remark: null,
    });

    const listData = ref<GetEpStdWordListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载标准词列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await EpStdWordApi.getEpStdWordList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
        QueryPersistService.persistQuery("ep-std-word-manager", listForm.value);
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询条件
     */
    const resetList = () => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 10;
      listForm.value.sourceName = null;
      listForm.value.sourceNameFull = null;
      listForm.value.targetName = null;
      listForm.value.targetNameFull = null;
      listForm.value.remark = null;
      QueryPersistService.clearQuery("ep-std-word-manager");
      loadList();
    };

    /**
     * 删除标准词
     */
    const removeList = async (id: string) => {
      try {
        await ElMessageBox.confirm("确定删除该标准词吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await EpStdWordApi.removeEpStdWord({ id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 批量删除标准词
     */
    const removeListBatch = async (selectedItems: GetEpStdWordListVo[]) => {
      if (selectedItems.length === 0) {
        ElMessage.warning("请选择要删除的标准词");
        return;
      }

      try {
        await ElMessageBox.confirm(`确定删除选中的${selectedItems.length}个标准词吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        const ids = selectedItems.map((item) => item.id);
        await EpStdWordApi.removeEpStdWord({ ids });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    //初始化
    onMounted(async () => {
      QueryPersistService.loadQuery("ep-std-word-manager", listForm.value);
      await loadList();
    });

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      loadList,
      resetList,
      removeList,
      removeListBatch,
    };
  },

  /**
   * 标准词模态框打包
   * @param modalFormRef 模态框表单引用
   * @param loadList 列表加载函数
   */
  useEpStdWordModal(modalFormRef: Ref<FormInstance>, loadList: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit">("add");
    const modalForm = reactive<GetEpStdWordDetailsVo>({
      id: "",
      sourceName: "",
      sourceNameFull: null,
      targetName: "",
      targetNameFull: null,
      remark: null,
    });

    const modalRules = {
      sourceName: [
        { required: true, message: "请输入简称", trigger: "blur" },
        { max: 128, message: "简称长度不能超过128个字符", trigger: "blur" },
      ],
      sourceNameFull: [{ max: 255, message: "全称长度不能超过255个字符", trigger: "blur" }],
      targetName: [
        { required: true, message: "请输入英文简称", trigger: "blur" },
        { max: 128, message: "英文简称长度不能超过128个字符", trigger: "blur" },
      ],
      targetNameFull: [{ max: 128, message: "英文全称长度不能超过128个字符", trigger: "blur" }],
      remark: [{ max: 1000, message: "备注长度不能超过1000个字符", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param row 当前行
     */
    const openModal = async (mode: "add" | "edit", row: GetEpStdWordListVo | null) => {
      modalMode.value = mode;
      resetModal();

      //如果是编辑模式则需要加载详情数据
      if (mode === "edit" && row) {
        try {
          const ret = await EpStdWordApi.getEpStdWordDetails({ id: row.id });
          modalForm.id = ret.id;
          modalForm.sourceName = ret.sourceName;
          modalForm.sourceNameFull = ret.sourceNameFull;
          modalForm.targetName = ret.targetName;
          modalForm.targetNameFull = ret.targetNameFull;
          modalForm.remark = ret.remark;
        } catch (error: any) {
          ElMessage.error(error.message || "获取标准词详情失败");
          return;
        }
      }

      modalVisible.value = true;
    };

    /**
     * 重置模态框表单
     */
    const resetModal = () => {
      modalForm.id = "";
      modalForm.sourceName = "";
      modalForm.sourceNameFull = null;
      modalForm.targetName = "";
      modalForm.targetNameFull = null;
      modalForm.remark = null;

      if (modalFormRef.value) {
        modalFormRef.value.resetFields();
      }
    };

    /**
     * 提交模态框表单
     */
    const submitModal = async () => {
      //先校验表单
      try {
        await modalFormRef?.value?.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      //提交表单
      try {
        if (modalMode.value === "add") {
          const addDto: AddEpStdWordDto = {
            sourceName: modalForm.sourceName,
            sourceNameFull: modalForm.sourceNameFull,
            targetName: modalForm.targetName,
            targetNameFull: modalForm.targetNameFull,
            remark: modalForm.remark,
          };
          await EpStdWordApi.addEpStdWord(addDto);
          ElMessage.success("操作成功");
          resetModal();
        }

        if (modalMode.value === "edit") {
          const editDto: EditEpStdWordDto = {
            id: modalForm.id,
            sourceName: modalForm.sourceName,
            sourceNameFull: modalForm.sourceNameFull,
            targetName: modalForm.targetName,
            targetNameFull: modalForm.targetNameFull,
            remark: modalForm.remark,
          };
          await EpStdWordApi.editEpStdWord(editDto);
          ElMessage.success("操作成功");
        }
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      } finally {
        modalLoading.value = false;
      }

      await loadList();
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      modalRules,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
