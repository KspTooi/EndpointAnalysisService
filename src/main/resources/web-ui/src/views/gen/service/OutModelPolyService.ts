import { reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutModelPolyListDto,
  GetOutModelPolyListVo,
  GetOutModelPolyDetailsVo,
  AddOutModelPolyDto,
  EditOutModelPolyDto,
} from "@/views/gen/api/OutModelPolyApi";
import OutModelPolyApi from "@/views/gen/api/OutModelPolyApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 按输出方案ID过滤的列表管理
   */
  useOutModelPolyList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetOutModelPolyListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetOutModelPolyListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async () => {
      listLoading.value = true;
      listForm.value.outputSchemaId = outputSchemaId.value;
      const result = await OutModelPolyApi.getOutModelPolyList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    const removeList = async (row: GetOutModelPolyListVo) => {
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
        await OutModelPolyApi.removeOutModelPoly({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    return {
      listData,
      listLoading,
      loadList,
      removeList,
    };
  },

  /**
   * 按输出方案ID固定的模态框管理
   */
  useOutModelPolyModal(modalFormRef: Ref<FormInstance | undefined>, outputSchemaId: Ref<string>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetOutModelPolyDetailsVo>({
      id: "",
      outputSchemaId: "",
      outputModelOriginId: "",
      name: "",
      kind: "",
      length: "",
      require: 0,
      policyCrudJson: "",
      policyQuery: 0,
      policyView: 0,
      placeholder: "",
      seq: 0,
    });

    const modalRules: FormRules = {
      outputModelOriginId: [{ required: true, message: "请输入原始字段ID", trigger: "blur" }],
      name: [{ required: true, message: "请输入聚合字段名", trigger: "blur" }],
      kind: [{ required: true, message: "请输入聚合数据类型", trigger: "blur" }],
      require: [{ required: true, message: "请输入聚合必填 0:否 1:是", trigger: "blur" }],
      policyCrudJson: [{ required: true, message: "请输入聚合可见性策略", trigger: "blur" }],
      policyQuery: [{ required: true, message: "请输入聚合查询策略", trigger: "blur" }],
      policyView: [{ required: true, message: "请输入聚合显示策略", trigger: "blur" }],
      placeholder: [{ required: true, message: "请输入placeholder", trigger: "blur" }],
      seq: [{ required: true, message: "请输入聚合排序", trigger: "blur" }],
    };

    const openModal = async (mode: ModalMode, row: GetOutModelPolyListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.outputSchemaId = outputSchemaId.value;
        modalForm.outputModelOriginId = "";
        modalForm.name = "";
        modalForm.kind = "";
        modalForm.length = "";
        modalForm.require = 0;
        modalForm.policyCrudJson = "";
        modalForm.policyQuery = 0;
        modalForm.policyView = 0;
        modalForm.placeholder = "";
        modalForm.seq = 0;
        modalVisible.value = true;
        return;
      }

      if (!row) {
        ElMessage.error("未选择要编辑的数据");
        return;
      }

      try {
        const details = await OutModelPolyApi.getOutModelPolyDetails({ id: row.id });
        modalForm.id = details.id;
        modalForm.outputSchemaId = details.outputSchemaId;
        modalForm.outputModelOriginId = details.outputModelOriginId;
        modalForm.name = details.name;
        modalForm.kind = details.kind;
        modalForm.length = details.length;
        modalForm.require = details.require;
        modalForm.policyCrudJson = details.policyCrudJson;
        modalForm.policyQuery = details.policyQuery;
        modalForm.policyView = details.policyView;
        modalForm.placeholder = details.placeholder;
        modalForm.seq = details.seq;
        modalVisible.value = true;
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    const resetModal = () => {
      if (!modalFormRef.value) {
        return;
      }
      modalFormRef.value.resetFields();
      modalForm.id = "";
      modalForm.outputSchemaId = "";
      modalForm.outputModelOriginId = "";
      modalForm.name = "";
      modalForm.kind = "";
      modalForm.length = "";
      modalForm.require = 0;
      modalForm.policyCrudJson = "";
      modalForm.policyQuery = 0;
      modalForm.policyView = 0;
      modalForm.placeholder = "";
      modalForm.seq = 0;
    };

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
          const addDto: AddOutModelPolyDto = {
            outputSchemaId: outputSchemaId.value,
            outputModelOriginId: modalForm.outputModelOriginId,
            name: modalForm.name,
            kind: modalForm.kind,
            length: modalForm.length,
            require: modalForm.require,
            policyCrudJson: modalForm.policyCrudJson,
            policyQuery: modalForm.policyQuery,
            policyView: modalForm.policyView,
            placeholder: modalForm.placeholder,
            seq: modalForm.seq,
          };
          await OutModelPolyApi.addOutModelPoly(addDto);
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

      if (!modalForm.id) {
        ElMessage.error("缺少ID参数");
        modalLoading.value = false;
        return;
      }

      try {
        const editDto: EditOutModelPolyDto = {
          id: modalForm.id,
          outputSchemaId: modalForm.outputSchemaId,
          outputModelOriginId: modalForm.outputModelOriginId,
          name: modalForm.name,
          kind: modalForm.kind,
          length: modalForm.length,
          require: modalForm.require,
          policyCrudJson: modalForm.policyCrudJson,
          policyQuery: modalForm.policyQuery,
          policyView: modalForm.policyView,
          placeholder: modalForm.placeholder,
          seq: modalForm.seq,
        };
        await OutModelPolyApi.editOutModelPoly(editDto);
        ElMessage.success("编辑成功");
        modalVisible.value = false;
        resetModal();
        reloadCallback();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
      modalLoading.value = false;
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
