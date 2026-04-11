import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetPromptListDto,
  GetPromptListVo,
  GetPromptDetailsVo,
  AddPromptDto,
  EditPromptDto,
} from "@/views/prompt/api/PromptApi.ts";
import PromptApi from "@/views/prompt/api/PromptApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 提示词列表管理
   */
  usePromptList() {
    const listForm = ref<GetPromptListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      tags: "",
    });

    const listData = ref<GetPromptListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await PromptApi.getPromptList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询
     */
    const resetList = () => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 20;
      listForm.value.name = "";
      listForm.value.tags = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetPromptListVo) => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await PromptApi.removePrompt({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
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
    };
  },

  /**
   * 模态框管理（统一处理新增和编辑）
   */
  usePromptModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetPromptDetailsVo>({
      id: "",
      name: "",
      tags: "",
      content: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "请输入名称", trigger: "blur" },
        { max: 80, message: "名称长度不能超过80个字符", trigger: "blur" },
      ],
      tags: [{ required: true, message: "请输入标签(CTJ)", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetPromptListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.tags = "";
        modalForm.content = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await PromptApi.getPromptDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.tags = details.tags;
          modalForm.content = details.content;
          modalVisible.value = true;
        } catch (error: any) {
          ElMessage.error(error.message);
        }
      }
    };

    /**
     * 重置模态框
     */
    const resetModal = () => {
      if (!modalFormRef.value) {
        return;
      }
      modalFormRef.value.resetFields();
      modalForm.id = "";
      modalForm.name = "";
      modalForm.tags = "";
      modalForm.content = "";
    };

    /**
     * 提交模态框
     */
    const submitModal = async () => {
      if (!modalFormRef.value) {
        return;
      }

      try {
        await modalFormRef.value.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      if (modalMode.value === "add") {
        try {
          const addDto: AddPromptDto = {
            name: modalForm.name,
            tags: modalForm.tags,
            content: modalForm.content,
          };
          await PromptApi.addPrompt(addDto);
          ElMessage.success("新增成功");
          modalVisible.value = false;
          resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
        return;
      }

      if (modalMode.value === "edit") {
        if (!modalForm.id) {
          ElMessage.error("缺少ID参数");
          modalLoading.value = false;
          return;
        }

        try {
          const editDto: EditPromptDto = {
            id: modalForm.id,
            name: modalForm.name,
            tags: modalForm.tags,
            content: modalForm.content,
          };
          await PromptApi.editPrompt(editDto);
          ElMessage.success("编辑成功");
          modalVisible.value = false;
          resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
      }
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
