import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetDriveSpaceListDto,
  GetDriveSpaceListVo,
  GetDriveSpaceDetailsVo,
  AddDriveSpaceDto,
  EditDriveSpaceDto,
} from "@/views/drive/api/DriveSpaceApi.ts";
import DriveSpaceApi from "@/views/drive/api/DriveSpaceApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 云盘空间列表管理
   */
  useDriveSpaceList() {
    const listForm = ref<GetDriveSpaceListDto>({
      pageNum: 1,
      pageSize: 20,
      name: "",
      remark: "",
      status: null,
    });

    const listData = ref<GetDriveSpaceListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await DriveSpaceApi.getDriveSpaceList(listForm.value);

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
      listForm.value.remark = "";
      listForm.value.status = null;
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetDriveSpaceListVo) => {
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
        await DriveSpaceApi.removeDriveSpace({ id: row.id });
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
  useDriveSpaceModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetDriveSpaceDetailsVo>({
      id: "",
      name: "",
      remark: "",
      quotaLimit: "",
      status: 0,
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "请输入空间名称", trigger: "blur" },
        { max: 80, message: "空间名称不超过80个字符", trigger: "blur" },
      ],
      remark: [{ max: 65535, message: "空间描述过长", trigger: "blur" }],
      quotaLimit: [{ required: true, message: "请输入配额限制(bytes)", trigger: "blur" }],
      status: [{ required: true, message: "请选择状态", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetDriveSpaceListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.name = "";
        modalForm.remark = "";
        modalForm.quotaLimit = "";
        modalForm.status = 0;
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await DriveSpaceApi.getDriveSpaceDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.name = details.name;
          modalForm.remark = details.remark;
          modalForm.quotaLimit = details.quotaLimit;
          modalForm.status = details.status;
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
      modalForm.remark = "";
      modalForm.quotaLimit = "";
      modalForm.status = 0;
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
          const addDto: AddDriveSpaceDto = {
            name: modalForm.name,
            remark: modalForm.remark,
            quotaLimit: modalForm.quotaLimit,
            status: modalForm.status,
          };
          await DriveSpaceApi.addDriveSpace(addDto);
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
          const editDto: EditDriveSpaceDto = {
            id: modalForm.id,
            name: modalForm.name,
            remark: modalForm.remark,
            quotaLimit: modalForm.quotaLimit,
            status: modalForm.status,
          };
          await DriveSpaceApi.editDriveSpace(editDto);
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
