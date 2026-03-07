import { onMounted, reactive, ref, watch, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutSchemaListDto,
  GetOutSchemaListVo,
  GetOutSchemaDetailsVo,
  AddOutSchemaDto,
  EditOutSchemaDto,
} from "@/views/gen/api/OutSchemaApi";
import OutSchemaApi from "@/views/gen/api/OutSchemaApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import type { GetDataSourceListVo } from "@/views/gen/api/DataSourceApi";
import type { GetTymSchemaListVo } from "@/views/gen/api/TymSchemaApi";
import type { GetScmListVo } from "@/views/gen/api/ScmApi";
import DataSourceApi from "@/views/gen/api/DataSourceApi";
import TymSchemaApi from "@/views/gen/api/TymSchemaApi";
import ScmApi from "@/views/gen/api/ScmApi";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 输出方案列表管理
   */
  useOutSchemaList() {
    const listForm = ref<GetOutSchemaListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      modelName: "",
      tableName: "",
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
      listForm.value.name = "";
      listForm.value.modelName = "";
      listForm.value.tableName = "";
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
    });

    const modalDataSource = ref<GetDataSourceListVo[]>([]);
    const modalTypeSchema = ref<GetTymSchemaListVo[]>([]);
    const modalScm = ref<GetScmListVo[]>([]);

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "请输入输出方案名称", trigger: "blur" },
        { max: 32, message: "输出方案名称不能超过32个字符", trigger: "blur" },
      ],
      modelName: [
        { required: true, message: "请输入模型名称", trigger: "change" },
        { max: 255, message: "模型名称不能超过255个字符", trigger: "change" },
      ],
      tableName: [{ max: 80, message: "数据源表名不能超过80个字符", trigger: "blur" }],
      removeTablePrefix: [
        { required: true, message: "请输入移除表前缀", trigger: "blur" },
        { max: 80, message: "移除表前缀不能超过80个字符", trigger: "blur" },
      ],
      permCodePrefix: [
        { required: true, message: "请输入权限码前缀", trigger: "blur" },
        { max: 32, message: "权限码前缀不能超过32个字符", trigger: "blur" },
      ],
      policyOverride: [{ required: true, message: "请选择写出策略", trigger: "blur" }],
      baseInput: [
        { required: true, message: "请输入输入基准路径", trigger: "blur" },
        { max: 320, message: "输入基准路径不能超过320个字符", trigger: "blur" },
      ],
      baseOutput: [
        { required: true, message: "请输入输出基准路径", trigger: "blur" },
        { max: 320, message: "输出基准路径不能超过320个字符", trigger: "blur" },
      ],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetOutSchemaListVo | null) => {
      modalMode.value = mode;

      //重新拉取数据源、类型映射方案、SCM列表
      try {
        const dataSourceResult = await DataSourceApi.getDataSourceList({ pageNum: 1, pageSize: 10000 });
        const typeSchemaResult = await TymSchemaApi.getTymSchemaList({ pageNum: 1, pageSize: 10000 });
        const scmResult = await ScmApi.getScmList({ pageNum: 1, pageSize: 10000 });

        modalDataSource.value = dataSourceResult.data;
        modalTypeSchema.value = typeSchemaResult.data;
        modalScm.value = scmResult.data;
      } catch (error: any) {
        ElMessage.error("拉取数据源、类型映射方案、SCM列表失败，请稍后重试");
        return;
      }

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
        modalForm.baseInput = "/";
        modalForm.baseOutput = "/";
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
      modalForm.baseInput = "/";
      modalForm.baseOutput = "/";
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
      modalDataSource,
      modalTypeSchema,
      modalScm,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
