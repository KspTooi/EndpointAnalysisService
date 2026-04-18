import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetQfBizFormListDto,
  GetQfBizFormListVo,
  GetQfBizFormDetailsVo,
  AddQfBizFormDto,
  EditQfBizFormDto,
} from "@/views/qf/api/QfBizFormApi.ts";
import QfBizFormApi from "@/views/qf/api/QfBizFormApi.ts";
import { Result } from "@/commons/model/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 业务表单列表管理
   */
  useQfBizFormList() {
    const listForm = ref<GetQfBizFormListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      code: "",
      tableName: "",
      status: null,
      seq: null,
    });

    const listData = ref<GetQfBizFormListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async (): Promise<void> => {
      listLoading.value = true;
      const result = await QfBizFormApi.getQfBizFormList(listForm.value);

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
    const resetList = (): void => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 20;
      listForm.value.name = "";
      listForm.value.code = "";
      listForm.value.tableName = "";
      listForm.value.status = null;
      listForm.value.seq = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetQfBizFormListVo): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await QfBizFormApi.removeQfBizForm({ id: row.id });
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
  useQfBizFormModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetQfBizFormDetailsVo>({
      id: "",
      name: "",
      code: "",
      formType: 0,
      icon: "",
      tableName: "",
      routePc: "",
      routeMobile: "",
      status: 0,
      seq: 0,
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "请输入业务名称", trigger: "blur" },
        { max: 32, message: "业务名称长度不能超过32个字符", trigger: "blur" },
      ],
      code: [
        { required: true, message: "请输入业务编码", trigger: "blur" },
        { max: 32, message: "业务编码长度不能超过32个字符", trigger: "blur" },
      ],
      formType: [{ required: true, message: "请输入表单类型 0:手搓表单 1:动态表单", trigger: "blur" }],
      icon: [
        { required: true, message: "请输入表单图标", trigger: "blur" },
        { max: 80, message: "表单图标长度不能超过80个字符", trigger: "blur" },
      ],
      tableName: [
        { required: true, message: "请输入物理表名", trigger: "blur" },
        { max: 200, message: "物理表名长度不能超过200个字符", trigger: "blur" },
      ],
      routePc: [{ max: 200, message: "PC端路由名长度不能超过200个字符", trigger: "blur" }],
      routeMobile: [{ max: 200, message: "移动端路由名长度不能超过200个字符", trigger: "blur" }],
      status: [{ required: true, message: "请输入状态 0:正常 1:停用", trigger: "blur" }],
      seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetQfBizFormListVo | null): Promise<void> => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.code = "";
        modalForm.formType = 0;
        modalForm.icon = "";
        modalForm.tableName = "";
        modalForm.routePc = "";
        modalForm.routeMobile = "";
        modalForm.status = 0;
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
          const details = await QfBizFormApi.getQfBizFormDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.code = details.code;
          modalForm.formType = details.formType;
          modalForm.icon = details.icon;
          modalForm.tableName = details.tableName;
          modalForm.routePc = details.routePc;
          modalForm.routeMobile = details.routeMobile;
          modalForm.status = details.status;
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
    const resetModal = (): void => {
      if (!modalFormRef.value) {
        return;
      }
      modalFormRef.value.resetFields();
      modalForm.id = "";
      modalForm.name = "";
      modalForm.code = "";
      modalForm.formType = 0;
      modalForm.icon = "";
      modalForm.tableName = "";
      modalForm.routePc = "";
      modalForm.routeMobile = "";
      modalForm.status = 0;
      modalForm.seq = 0;
    };

    /**
     * 提交模态框
     */
    const submitModal = async (): Promise<void> => {
      if (!modalFormRef.value) {
        return;
      }

      try {
        await modalFormRef.value.validate();
      } catch {
        return;
      }

      modalLoading.value = true;

      if (modalMode.value === "add") {
        try {
          const addDto: AddQfBizFormDto = {
            name: modalForm.name,
            code: modalForm.code,
            formType: modalForm.formType,
            icon: modalForm.icon,
            tableName: modalForm.tableName,
            routePc: modalForm.routePc,
            routeMobile: modalForm.routeMobile,
            status: modalForm.status,
            seq: modalForm.seq,
          };
          await QfBizFormApi.addQfBizForm(addDto);
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
          const editDto: EditQfBizFormDto = {
            id: modalForm.id,
            name: modalForm.name,
            formType: modalForm.formType,
            icon: modalForm.icon,
            tableName: modalForm.tableName,
            routePc: modalForm.routePc,
            routeMobile: modalForm.routeMobile,
            status: modalForm.status,
            seq: modalForm.seq,
          };
          await QfBizFormApi.editQfBizForm(editDto);
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
