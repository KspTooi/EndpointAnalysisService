import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutBlueprintListDto,
  GetOutBlueprintListVo,
  GetOutBlueprintDetailsVo,
  AddOutBlueprintDto,
  EditOutBlueprintDto,
} from "@/views/gen/api/OutBlueprintApi";
import OutBlueprintApi from "@/views/gen/api/OutBlueprintApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 输出蓝图表列表管理
   */
  useOutBlueprintList() {
    const listForm = ref<GetOutBlueprintListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      projectName: "",
      code: "",
    });

    const listData = ref<GetOutBlueprintListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await OutBlueprintApi.getOutBlueprintList(listForm.value);

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
      listForm.value.projectName = "";
      listForm.value.code = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetOutBlueprintListVo) => {
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
        await OutBlueprintApi.removeOutBlueprint({ id: row.id });
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
  useOutBlueprintModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetOutBlueprintDetailsVo>({
      id: "",
      name: "",
      projectName: "",
      code: "",
      scmUrl: "",
      scmAuthKind: 0,
      scmUsername: "",
      scmPassword: "",
      scmPk: "",
      scmBranch: "",
      scmBasePath: "",
      remark: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [{ required: true, message: "请输入蓝图名称", trigger: "blur", max: 32 }],
      projectName: [{ required: false, trigger: "blur", max: 80 }],
      code: [{ required: true, message: "请输入蓝图编码", trigger: "blur", max: 32 }],
      scmUrl: [{ required: true, message: "请输入SCM仓库地址", trigger: "blur", max: 1000 }],
      scmAuthKind: [{ required: true, message: "请选择SCM认证方式", trigger: "change", type: "number" }],
      scmBranch: [{ required: true, message: "请输入SCM分支", trigger: "blur", max: 80 }],
      scmBasePath: [{ required: true, message: "请输入基准路径", trigger: "blur", max: 1280 }],
      remark: [{ required: false, trigger: "blur", max: 500 }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetOutBlueprintListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.projectName = "";
        modalForm.code = "";
        modalForm.scmUrl = "";
        modalForm.scmAuthKind = 0;
        modalForm.scmUsername = "";
        modalForm.scmPassword = "";
        modalForm.scmPk = "";
        modalForm.scmBranch = "";
        modalForm.scmBasePath = "";
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
          const details = await OutBlueprintApi.getOutBlueprintDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.projectName = details.projectName;
          modalForm.code = details.code;
          modalForm.scmUrl = details.scmUrl;
          modalForm.scmAuthKind = details.scmAuthKind;
          modalForm.scmUsername = details.scmUsername;
          modalForm.scmPassword = details.scmPassword;
          modalForm.scmPk = details.scmPk;
          modalForm.scmBranch = details.scmBranch;
          modalForm.scmBasePath = details.scmBasePath;
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
      modalForm.projectName = "";
      modalForm.code = "";
      modalForm.scmUrl = "";
      modalForm.scmAuthKind = 0;
      modalForm.scmUsername = "";
      modalForm.scmPassword = "";
      modalForm.scmPk = "";
      modalForm.scmBranch = "";
      modalForm.scmBasePath = "";
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
          const addDto: AddOutBlueprintDto = {
            name: modalForm.name,
            projectName: modalForm.projectName,
            code: modalForm.code,
            scmUrl: modalForm.scmUrl,
            scmAuthKind: modalForm.scmAuthKind,
            scmUsername: modalForm.scmUsername,
            scmPassword: modalForm.scmPassword,
            scmPk: modalForm.scmPk,
            scmBranch: modalForm.scmBranch,
            scmBasePath: modalForm.scmBasePath,
            remark: modalForm.remark,
          };
          await OutBlueprintApi.addOutBlueprint(addDto);
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
          const editDto: EditOutBlueprintDto = {
            id: modalForm.id,
            name: modalForm.name,
            projectName: modalForm.projectName,
            code: modalForm.code,
            scmUrl: modalForm.scmUrl,
            scmAuthKind: modalForm.scmAuthKind,
            scmUsername: modalForm.scmUsername,
            scmPassword: modalForm.scmPassword,
            scmPk: modalForm.scmPk,
            scmBranch: modalForm.scmBranch,
            scmBasePath: modalForm.scmBasePath,
            remark: modalForm.remark,
          };
          await OutBlueprintApi.editOutBlueprint(editDto);
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
