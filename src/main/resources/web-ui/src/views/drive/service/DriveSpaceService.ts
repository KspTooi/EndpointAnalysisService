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
      rootId: "",
      deptId: "",
      name: "",
      remark: "",
      quotaLimit: "",
      quotaUsed: "",
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
      listForm.value.rootId = "";
      listForm.value.deptId = "";
      listForm.value.name = "";
      listForm.value.remark = "";
      listForm.value.quotaLimit = "";
      listForm.value.quotaUsed = "";
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
      rootId: "",
      deptId: "",
      name: "",
      remark: "",
      quotaLimit: "",
      quotaUsed: "",
      status: 0,
      createTime: "",
      creatorId: "",
      updateTime: "",
      updaterId: "",
      deleteTime: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      rootId: [{ required: true, message: "请输入租户ID", trigger: "blur" }],
      deptId: [{ required: true, message: "请输入部门ID", trigger: "blur" }],
      name: [{ required: true, message: "请输入空间名称", trigger: "blur" }],
      quotaLimit: [{ required: true, message: "请输入配额限制(bytes)", trigger: "blur" }],
      quotaUsed: [{ required: true, message: "请输入已用配额(bytes)", trigger: "blur" }],
      status: [{ required: true, message: "请输入状态 0:正常 1:归档", trigger: "blur" }],
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
        modalForm.rootId = "";
        modalForm.deptId = "";
        modalForm.name = "";
        modalForm.remark = "";
        modalForm.quotaLimit = "";
        modalForm.quotaUsed = "";
        modalForm.status = 0;
        modalForm.createTime = "";
        modalForm.creatorId = "";
        modalForm.updateTime = "";
        modalForm.updaterId = "";
        modalForm.deleteTime = "";
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
          modalForm.rootId = details.rootId;
          modalForm.deptId = details.deptId;
          modalForm.name = details.name;
          modalForm.remark = details.remark;
          modalForm.quotaLimit = details.quotaLimit;
          modalForm.quotaUsed = details.quotaUsed;
          modalForm.status = details.status;
          modalForm.createTime = details.createTime;
          modalForm.creatorId = details.creatorId;
          modalForm.updateTime = details.updateTime;
          modalForm.updaterId = details.updaterId;
          modalForm.deleteTime = details.deleteTime;
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
      modalForm.rootId = "";
      modalForm.deptId = "";
      modalForm.name = "";
      modalForm.remark = "";
      modalForm.quotaLimit = "";
      modalForm.quotaUsed = "";
      modalForm.status = 0;
      modalForm.createTime = "";
      modalForm.creatorId = "";
      modalForm.updateTime = "";
      modalForm.updaterId = "";
      modalForm.deleteTime = "";
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
            rootId: modalForm.rootId,
            deptId: modalForm.deptId,
            name: modalForm.name,
            remark: modalForm.remark,
            quotaLimit: modalForm.quotaLimit,
            quotaUsed: modalForm.quotaUsed,
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
            rootId: modalForm.rootId,
            deptId: modalForm.deptId,
            name: modalForm.name,
            remark: modalForm.remark,
            quotaLimit: modalForm.quotaLimit,
            quotaUsed: modalForm.quotaUsed,
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
