import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetTymSchemaFieldListDto,
  GetTymSchemaFieldListVo,
  GetTymSchemaFieldDetailsVo,
  AddTymSchemaFieldDto,
  EditTymSchemaFieldDto,
} from "@/views/assembly/api/TymSchemaFieldApi";
import TymSchemaFieldApi from "@/views/assembly/api/TymSchemaFieldApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 类型映射方案字段表列表管理
   */
  useTymSchemaFieldList(typeSchemaId: Ref<string>) {
    const listForm = ref<GetTymSchemaFieldListDto>({
      pageNum: 1,
      pageSize: 20,
      typeSchemaId: typeSchemaId.value,
    });

    const listData = ref<GetTymSchemaFieldListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      listForm.value.typeSchemaId = typeSchemaId.value;
      const result = await TymSchemaFieldApi.getTymSchemaFieldList(listForm.value);

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
      listForm.value.typeSchemaId = typeSchemaId.value;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetTymSchemaFieldListVo) => {
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
        await TymSchemaFieldApi.removeTymSchemaField({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
      //await loadList();
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
  useTymSchemaFieldModal(modalFormRef: Ref<FormInstance | undefined>, typeSchemaId: Ref<string>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetTymSchemaFieldDetailsVo>({
      id: "",
      typeSchemaId: typeSchemaId.value,
      source: "",
      target: "",
      seq: 0,
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      source: [{ required: true, message: "请输入匹配源类型", trigger: "blur" }],
      target: [{ required: true, message: "请输入匹配目标类型", trigger: "blur" }],
      seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetTymSchemaFieldListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.typeSchemaId = typeSchemaId.value;
        modalForm.source = "";
        modalForm.target = "";
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
          const details = await TymSchemaFieldApi.getTymSchemaFieldDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.typeSchemaId = typeSchemaId.value;
          modalForm.source = details.source;
          modalForm.target = details.target;
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
      modalForm.typeSchemaId = typeSchemaId.value;
      modalForm.source = "";
      modalForm.target = "";
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
          const addDto: AddTymSchemaFieldDto = {
            typeSchemaId: typeSchemaId.value,
            source: modalForm.source,
            target: modalForm.target,
            seq: modalForm.seq,
          };
          await TymSchemaFieldApi.addTymSchemaField(addDto);
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
          const editDto: EditTymSchemaFieldDto = {
            id: modalForm.id,
            source: modalForm.source,
            target: modalForm.target,
            seq: modalForm.seq,
          };
          await TymSchemaFieldApi.editTymSchemaField(editDto);
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
