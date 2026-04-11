import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetUserListDto,
  GetUserListVo,
  GetUserDetailsVo,
  AddUserDto,
  EditUserDto,
} from "@/views/user/api/UserApi.ts";
import UserApi from "@/views/user/api/UserApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * ${model.comment}列表管理
   */
  useUserList() {
    const listForm = ref<GetUserListDto>({
      pageNum: 1,
      pageSize: 20,
      username: "",
      password: "",
      nickname: "",
      gender: "",
      phone: "",
      email: "",
      loginCount: "",
      status: "",
      lastLoginTime: "",
      rootId: "",
      rootName: "",
      deptId: "",
      deptName: "",
      activeCompanyId: "",
      activeEnvId: "",
      avatarAttachId: "",
      isSystem: "",
      dataVersion: "",
    });

    const listData = ref<GetUserListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await UserApi.getUserList(listForm.value);

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
      listForm.value.username = "";
      listForm.value.password = "";
      listForm.value.nickname = "";
      listForm.value.gender = "";
      listForm.value.phone = "";
      listForm.value.email = "";
      listForm.value.loginCount = "";
      listForm.value.status = "";
      listForm.value.lastLoginTime = "";
      listForm.value.rootId = "";
      listForm.value.rootName = "";
      listForm.value.deptId = "";
      listForm.value.deptName = "";
      listForm.value.activeCompanyId = "";
      listForm.value.activeEnvId = "";
      listForm.value.avatarAttachId = "";
      listForm.value.isSystem = "";
      listForm.value.dataVersion = "";
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetUserListVo) => {
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
        await UserApi.removeUser({ id: row.id });
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
  useUserModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetUserDetailsVo>({
      id: "",
      username: "",
      password: "",
      nickname: "",
      gender: "",
      phone: "",
      email: "",
      loginCount: "",
      status: "",
      lastLoginTime: "",
      rootId: "",
      rootName: "",
      deptId: "",
      deptName: "",
      activeCompanyId: "",
      activeEnvId: "",
      avatarAttachId: "",
      isSystem: "",
      dataVersion: "",
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
      loginCount: [{ required: true, message: "请输入登录次数", trigger: "blur" }],
      status: [{ required: true, message: "请输入用户状态 0:正常 1:封禁", trigger: "blur" }],
      isSystem: [{ required: true, message: "请输入内置用户 0:否 1:是", trigger: "blur" }],
      dataVersion: [{ required: true, message: "请输入数据版本号", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetUserListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.username = "";
        modalForm.password = "";
        modalForm.nickname = "";
        modalForm.gender = "";
        modalForm.phone = "";
        modalForm.email = "";
        modalForm.loginCount = "";
        modalForm.status = "";
        modalForm.lastLoginTime = "";
        modalForm.rootId = "";
        modalForm.rootName = "";
        modalForm.deptId = "";
        modalForm.deptName = "";
        modalForm.activeCompanyId = "";
        modalForm.activeEnvId = "";
        modalForm.avatarAttachId = "";
        modalForm.isSystem = "";
        modalForm.dataVersion = "";
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
          const details = await UserApi.getUserDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.username = details.username;
          modalForm.password = details.password;
          modalForm.nickname = details.nickname;
          modalForm.gender = details.gender;
          modalForm.phone = details.phone;
          modalForm.email = details.email;
          modalForm.loginCount = details.loginCount;
          modalForm.status = details.status;
          modalForm.lastLoginTime = details.lastLoginTime;
          modalForm.rootId = details.rootId;
          modalForm.rootName = details.rootName;
          modalForm.deptId = details.deptId;
          modalForm.deptName = details.deptName;
          modalForm.activeCompanyId = details.activeCompanyId;
          modalForm.activeEnvId = details.activeEnvId;
          modalForm.avatarAttachId = details.avatarAttachId;
          modalForm.isSystem = details.isSystem;
          modalForm.dataVersion = details.dataVersion;
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
      modalForm.username = "";
      modalForm.password = "";
      modalForm.nickname = "";
      modalForm.gender = "";
      modalForm.phone = "";
      modalForm.email = "";
      modalForm.loginCount = "";
      modalForm.status = "";
      modalForm.lastLoginTime = "";
      modalForm.rootId = "";
      modalForm.rootName = "";
      modalForm.deptId = "";
      modalForm.deptName = "";
      modalForm.activeCompanyId = "";
      modalForm.activeEnvId = "";
      modalForm.avatarAttachId = "";
      modalForm.isSystem = "";
      modalForm.dataVersion = "";
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
          const addDto: AddUserDto = {
            username: modalForm.username,
            password: modalForm.password,
            nickname: modalForm.nickname,
            gender: modalForm.gender,
            phone: modalForm.phone,
            email: modalForm.email,
            loginCount: modalForm.loginCount,
            status: modalForm.status,
            lastLoginTime: modalForm.lastLoginTime,
            rootId: modalForm.rootId,
            rootName: modalForm.rootName,
            deptId: modalForm.deptId,
            deptName: modalForm.deptName,
            activeCompanyId: modalForm.activeCompanyId,
            activeEnvId: modalForm.activeEnvId,
            avatarAttachId: modalForm.avatarAttachId,
            isSystem: modalForm.isSystem,
            dataVersion: modalForm.dataVersion,
          };
          await UserApi.addUser(addDto);
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
          const editDto: EditUserDto = {
            id: modalForm.id,
            username: modalForm.username,
            password: modalForm.password,
            nickname: modalForm.nickname,
            gender: modalForm.gender,
            phone: modalForm.phone,
            email: modalForm.email,
            loginCount: modalForm.loginCount,
            status: modalForm.status,
            lastLoginTime: modalForm.lastLoginTime,
            rootId: modalForm.rootId,
            rootName: modalForm.rootName,
            deptId: modalForm.deptId,
            deptName: modalForm.deptName,
            activeCompanyId: modalForm.activeCompanyId,
            activeEnvId: modalForm.activeEnvId,
            avatarAttachId: modalForm.avatarAttachId,
            isSystem: modalForm.isSystem,
            dataVersion: modalForm.dataVersion,
          };
          await UserApi.editUser(editDto);
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
