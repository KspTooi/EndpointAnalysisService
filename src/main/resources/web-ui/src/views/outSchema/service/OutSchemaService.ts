import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutSchemaListDto,
  GetOutSchemaListVo,
  GetOutSchemaDetailsVo,
  AddOutSchemaDto,
  EditOutSchemaDto,
} from "@/views/outSchema/api/OutSchemaApi.ts";
import OutSchemaApi from "@/views/outSchema/api/OutSchemaApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 输出方案表列表管理
   */
  useOutSchemaList() {
    const listForm = ref<GetOutSchemaListDto>({
      pageNum: 1,
      pageSize: 20,
      dataSourceId: "",
      typeSchemaId: "",
      inputScmId: "",
      outputScmId: "",
      name: "",
      modelName: "",
      tableName: "",
      removeTablePrefix: "",
      permCodePrefix: "",
      policyOverride: null,
      baseInput: "",
      baseOutput: "",
      remark: "",
      fieldCountOrigin: null,
      fieldCountPoly: null,
    });

    const listData = ref<GetOutSchemaListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await OutSchemaApi.getOutSchemaList(listForm.value);

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
      listForm.value.dataSourceId = "";
      listForm.value.typeSchemaId = "";
      listForm.value.inputScmId = "";
      listForm.value.outputScmId = "";
      listForm.value.name = "";
      listForm.value.modelName = "";
      listForm.value.tableName = "";
      listForm.value.removeTablePrefix = "";
      listForm.value.permCodePrefix = "";
      listForm.value.policyOverride = null;
      listForm.value.baseInput = "";
      listForm.value.baseOutput = "";
      listForm.value.remark = "";
      listForm.value.fieldCountOrigin = null;
      listForm.value.fieldCountPoly = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetOutSchemaListVo) => {
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
        await OutSchemaApi.removeOutSchema({ id: row.id });
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
  useOutSchemaModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetOutSchemaDetailsVo>({
      id: "",
      dataSourceId: "",
      typeSchemaId: "",
      inputScmId: "",
      outputScmId: "",
      name: "",
      modelName: "",
      tableName: "",
      removeTablePrefix: "",
      permCodePrefix: "",
      policyOverride: 0,
      baseInput: "",
      baseOutput: "",
      remark: "",
      fieldCountOrigin: 0,
      fieldCountPoly: 0,
      createTime: "",
      creatorId: "",
      updateTime: "",
      updaterId: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [{ required: true, message: "请输入输出方案名称", trigger: "blur" }],
      modelName: [{ required: true, message: "请输入模型名称", trigger: "blur" }],
      removeTablePrefix: [{ required: true, message: "请输入移除表前缀", trigger: "blur" }],
      permCodePrefix: [{ required: true, message: "请输入权限码前缀", trigger: "blur" }],
      policyOverride: [{ required: true, message: "请输入写出策略 0:不覆盖 1:覆盖", trigger: "blur" }],
      baseInput: [{ required: true, message: "请输入输入基准路径", trigger: "blur" }],
      baseOutput: [{ required: true, message: "请输入输出基准路径", trigger: "blur" }],
      fieldCountOrigin: [{ required: true, message: "请输入字段数(原始)", trigger: "blur" }],
      fieldCountPoly: [{ required: true, message: "请输入字段数(聚合)", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetOutSchemaListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.dataSourceId = "";
        modalForm.typeSchemaId = "";
        modalForm.inputScmId = "";
        modalForm.outputScmId = "";
        modalForm.name = "";
        modalForm.modelName = "";
        modalForm.tableName = "";
        modalForm.removeTablePrefix = "";
        modalForm.permCodePrefix = "";
        modalForm.policyOverride = 0;
        modalForm.baseInput = "";
        modalForm.baseOutput = "";
        modalForm.remark = "";
        modalForm.fieldCountOrigin = 0;
        modalForm.fieldCountPoly = 0;
        modalForm.createTime = "";
        modalForm.creatorId = "";
        modalForm.updateTime = "";
        modalForm.updaterId = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await OutSchemaApi.getOutSchemaDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.dataSourceId = details.dataSourceId;
          modalForm.typeSchemaId = details.typeSchemaId;
          modalForm.inputScmId = details.inputScmId;
          modalForm.outputScmId = details.outputScmId;
          modalForm.name = details.name;
          modalForm.modelName = details.modelName;
          modalForm.tableName = details.tableName;
          modalForm.removeTablePrefix = details.removeTablePrefix;
          modalForm.permCodePrefix = details.permCodePrefix;
          modalForm.policyOverride = details.policyOverride;
          modalForm.baseInput = details.baseInput;
          modalForm.baseOutput = details.baseOutput;
          modalForm.remark = details.remark;
          modalForm.fieldCountOrigin = details.fieldCountOrigin;
          modalForm.fieldCountPoly = details.fieldCountPoly;
          modalForm.createTime = details.createTime;
          modalForm.creatorId = details.creatorId;
          modalForm.updateTime = details.updateTime;
          modalForm.updaterId = details.updaterId;
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
      modalForm.dataSourceId = "";
      modalForm.typeSchemaId = "";
      modalForm.inputScmId = "";
      modalForm.outputScmId = "";
      modalForm.name = "";
      modalForm.modelName = "";
      modalForm.tableName = "";
      modalForm.removeTablePrefix = "";
      modalForm.permCodePrefix = "";
      modalForm.policyOverride = 0;
      modalForm.baseInput = "";
      modalForm.baseOutput = "";
      modalForm.remark = "";
      modalForm.fieldCountOrigin = 0;
      modalForm.fieldCountPoly = 0;
      modalForm.createTime = "";
      modalForm.creatorId = "";
      modalForm.updateTime = "";
      modalForm.updaterId = "";
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
          const addDto: AddOutSchemaDto = {
            dataSourceId: modalForm.dataSourceId,
            typeSchemaId: modalForm.typeSchemaId,
            inputScmId: modalForm.inputScmId,
            outputScmId: modalForm.outputScmId,
            name: modalForm.name,
            modelName: modalForm.modelName,
            tableName: modalForm.tableName,
            removeTablePrefix: modalForm.removeTablePrefix,
            permCodePrefix: modalForm.permCodePrefix,
            policyOverride: modalForm.policyOverride,
            baseInput: modalForm.baseInput,
            baseOutput: modalForm.baseOutput,
            remark: modalForm.remark,
            fieldCountOrigin: modalForm.fieldCountOrigin,
            fieldCountPoly: modalForm.fieldCountPoly,
          };
          await OutSchemaApi.addOutSchema(addDto);
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
          const editDto: EditOutSchemaDto = {
            id: modalForm.id,
            dataSourceId: modalForm.dataSourceId,
            typeSchemaId: modalForm.typeSchemaId,
            inputScmId: modalForm.inputScmId,
            outputScmId: modalForm.outputScmId,
            name: modalForm.name,
            modelName: modalForm.modelName,
            tableName: modalForm.tableName,
            removeTablePrefix: modalForm.removeTablePrefix,
            permCodePrefix: modalForm.permCodePrefix,
            policyOverride: modalForm.policyOverride,
            baseInput: modalForm.baseInput,
            baseOutput: modalForm.baseOutput,
            remark: modalForm.remark,
            fieldCountOrigin: modalForm.fieldCountOrigin,
            fieldCountPoly: modalForm.fieldCountPoly,
          };
          await OutSchemaApi.editOutSchema(editDto);
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
