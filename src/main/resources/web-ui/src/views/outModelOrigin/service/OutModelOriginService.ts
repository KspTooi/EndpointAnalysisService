import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutModelOriginListDto,
  GetOutModelOriginListVo,
  GetOutModelOriginDetailsVo,
  AddOutModelOriginDto,
  EditOutModelOriginDto,
} from "@/views/outModelOrigin/api/OutModelOriginApi.ts";
import OutModelOriginApi from "@/views/outModelOrigin/api/OutModelOriginApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 输出方案原始模型表列表管理
   */
  useOutModelOriginList() {
    const listForm = ref<GetOutModelOriginListDto>({
      pageNum: 1,
      pageSize: 20,
      id: "",
      outputSchemaId: "",
      name: "",
      kind: "",
      length: "",
      require: null,
      remark: "",
      seq: null,
    });

    const listData = ref<GetOutModelOriginListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await OutModelOriginApi.getOutModelOriginList(listForm.value);

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
      listForm.value.id = "";
      listForm.value.outputSchemaId = "";
      listForm.value.name = "";
      listForm.value.kind = "";
      listForm.value.length = "";
      listForm.value.require = null;
      listForm.value.remark = "";
      listForm.value.seq = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetOutModelOriginListVo) => {
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
        await OutModelOriginApi.removeOutModelOrigin({ id: row.id });
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
  useOutModelOriginModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetOutModelOriginDetailsVo>({
      id: "",
      outputSchemaId: "",
      name: "",
      kind: "",
      length: "",
      require: 0,
      remark: "",
      seq: 0,
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      outputSchemaId: [{ required: true, message: "请输入输出方案ID", trigger: "blur" }],
      name: [
        { required: true, message: "请输入原始字段名", trigger: "blur" },
        { max: 255, message: "原始字段名长度不能超过255", trigger: "blur" },
      ],
      kind: [
        { required: true, message: "请输入原始数据类型", trigger: "blur" },
        { max: 255, message: "原始数据类型长度不能超过255", trigger: "blur" },
      ],
      length: [{ max: 255, message: "原始长度不能超过255", trigger: "blur" }],
      require: [{ required: true, message: "请输入原始必填 0:否 1:是", trigger: "blur" }],
      remark: [{ max: 255, message: "原始备注长度不能超过255", trigger: "blur" }],
      seq: [{ required: true, message: "请输入原始排序", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetOutModelOriginListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.outputSchemaId = "";
        modalForm.name = "";
        modalForm.kind = "";
        modalForm.length = "";
        modalForm.require = 0;
        modalForm.remark = "";
        modalForm.seq = 0;
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await OutModelOriginApi.getOutModelOriginDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.outputSchemaId = details.outputSchemaId;
          modalForm.name = details.name;
          modalForm.kind = details.kind;
          modalForm.length = details.length;
          modalForm.require = details.require;
          modalForm.remark = details.remark;
          modalForm.seq = details.seq;
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
      modalForm.outputSchemaId = "";
      modalForm.name = "";
      modalForm.kind = "";
      modalForm.length = "";
      modalForm.require = 0;
      modalForm.remark = "";
      modalForm.seq = 0;
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
          const addDto: AddOutModelOriginDto = {
            outputSchemaId: modalForm.outputSchemaId,
            name: modalForm.name,
            kind: modalForm.kind,
            length: modalForm.length,
            require: modalForm.require,
            remark: modalForm.remark,
            seq: modalForm.seq,
          };
          await OutModelOriginApi.addOutModelOrigin(addDto);
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
          const editDto: EditOutModelOriginDto = {
            id: modalForm.id,
            outputSchemaId: modalForm.outputSchemaId,
            name: modalForm.name,
            kind: modalForm.kind,
            length: modalForm.length,
            require: modalForm.require,
            remark: modalForm.remark,
            seq: modalForm.seq,
          };
          await OutModelOriginApi.editOutModelOrigin(editDto);
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
