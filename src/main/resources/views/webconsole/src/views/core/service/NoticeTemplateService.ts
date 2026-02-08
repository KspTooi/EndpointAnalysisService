import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetNoticeTemplateListDto,
  GetNoticeTemplateListVo,
  GetNoticeTemplateDetailsVo,
  AddNoticeTemplateDto,
  EditNoticeTemplateDto,
} from "@/views/core/api/NoticeTemplateApi.ts";
import NoticeTemplateApi from "@/views/core/api/NoticeTemplateApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 通知模板表列表管理
   */
  useNoticeTemplateList() {
    const listForm = ref<GetNoticeTemplateListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      code: "",
      content: "",
      status: null,
      remark: "",
    });

    const listData = ref<GetNoticeTemplateListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await NoticeTemplateApi.getNoticeTemplateList(listForm.value);

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
      listForm.value.code = "";
      listForm.value.content = "";
      listForm.value.status = null;
      listForm.value.remark = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetNoticeTemplateListVo) => {
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
        await NoticeTemplateApi.removeNoticeTemplate({ id: row.id });
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
  useNoticeTemplateModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetNoticeTemplateDetailsVo>({
      id: "",
      name: "",
      code: "",
      content: "",
      status: 0,
      remark: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "模板名称不能为空", trigger: "blur" },
        { max: 32, message: "模板名称长度不能超过32个字符", trigger: "blur" },
      ],
      code: [
        { required: true, message: "模板唯一编码不能为空", trigger: "blur" },
        { max: 32, message: "模板唯一编码长度不能超过32个字符", trigger: "blur" },
      ],
      content: [{ required: true, message: "模板内容不能为空", trigger: "blur" }],
      status: [
        { required: true, message: "状态不能为空", trigger: "blur" },
        { type: "number", min: 0, max: 1, message: "状态只能在0-1之间", trigger: "blur" },
      ],
      remark: [{ max: 1000, message: "备注长度不能超过1000个字符", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetNoticeTemplateListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.code = "";
        modalForm.content = "";
        modalForm.status = 0;
        modalForm.remark = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await NoticeTemplateApi.getNoticeTemplateDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.code = details.code;
          modalForm.content = details.content;
          modalForm.status = details.status;
          modalForm.remark = details.remark;
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
      modalForm.code = "";
      modalForm.content = "";
      modalForm.status = 0;
      modalForm.remark = "";
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
          const addDto: AddNoticeTemplateDto = {
            name: modalForm.name,
            code: modalForm.code,
            content: modalForm.content,
            status: modalForm.status,
            remark: modalForm.remark,
          };
          await NoticeTemplateApi.addNoticeTemplate(addDto);
          ElMessage.success("新增成功");
          //modalVisible.value = false;
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
          const editDto: EditNoticeTemplateDto = {
            id: modalForm.id,
            name: modalForm.name,
            code: modalForm.code,
            content: modalForm.content,
            status: modalForm.status,
            remark: modalForm.remark,
          };
          await NoticeTemplateApi.editNoticeTemplate(editDto);
          ElMessage.success("编辑成功");
          //modalVisible.value = false;
          //resetModal();
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
