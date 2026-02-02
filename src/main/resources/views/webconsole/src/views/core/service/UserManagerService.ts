import { onMounted, reactive, ref, watch, type Ref } from "vue";
import type {
  GetUserDetailsVo,
  GetUserListDto,
  GetUserListVo,
  AddUserDto,
  EditUserDto,
  UserGroupVo,
} from "@/views/core/api/UserApi.ts";
import AdminUserApi from "@/views/core/api/UserApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService";
import GroupApi from "@/views/core/api/GroupApi.ts";
import OrgApi, { type GetOrgTreeVo } from "@/views/core/api/OrgApi.ts";

export default {
  /**
   * 用户列表打包
   */
  useUserList(orgId: Ref<string | null>) {
    const listForm = ref<GetUserListDto>({
      pageNum: 1,
      pageSize: 10,
      username: "",
      status: null,
      orgId: null,
    });

    const listData = ref<GetUserListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载用户列表
     */
    const loadList = async (orgId?: string | null) => {
      listForm.value.orgId = orgId ?? null;
      listLoading.value = true;
      const result = await AdminUserApi.getUserList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
        QueryPersistService.persistQuery("user-manager", listForm.value);
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询条件
     */
    const resetList = (orgId?: string | null) => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 10;
      listForm.value.username = "";
      listForm.value.status = null;
      QueryPersistService.clearQuery("user-manager");
      loadList(orgId ?? null);
    };

    /**
     * 删除用户
     */
    const removeList = async (user: GetUserListVo) => {
      try {
        await ElMessageBox.confirm("确定删除该用户吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await AdminUserApi.removeUser({ id: user.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    //初始化
    onMounted(async () => {
      QueryPersistService.loadQuery("user-manager", listForm.value);
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
   * 用户模态框打包
   * @param modalFormRef 模态框表单引用
   * @param loadList 列表加载函数
   */
  useUserModal(modalFormRef: Ref<FormInstance>, loadList: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit">("add");
    const modalCurrentRow = ref<GetUserListVo | null>(null);
    const modalForm = reactive<GetUserDetailsVo>({
      id: "",
      deptId: "",
      username: "",
      nickname: "",
      gender: 0,
      phone: "",
      email: "",
      status: 0,
      createTime: "",
      lastLoginTime: "",
      isSystem: 0,
      groups: [],
      permissions: [],
    });
    const modalFormPassword = ref("");
    const selectedGroupIds = ref<string[]>([]);
    const groupOptions = ref<UserGroupVo[]>([]);
    const orgTreeOptions = ref<any[]>([]);

    /**
     * 处理组织架构树数据，禁用企业节点
     */
    const processOrgTreeData = (treeData: GetOrgTreeVo[]): any[] => {
      return treeData.map((node) => {
        const processedNode: any = {
          ...node,
          disabled: node.kind === 1, // kind === 1 表示企业，禁用选择
        };
        if (node.children && node.children.length > 0) {
          processedNode.children = processOrgTreeData(node.children);
        }
        return processedNode;
      });
    };

    const modalRules = {
      username: [
        { required: true, message: "请输入用户名", trigger: "blur" },
        { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: "用户名只能包含4-20位字母、数字和下划线", trigger: "blur" },
      ],
      nickname: [{ max: 50, message: "昵称长度不能超过50个字符", trigger: "blur" }],
      password: [
        {
          trigger: "blur",
          validator: (rule: any, value: string, callback: Function) => {
            const password = modalFormPassword.value;
            if (modalMode.value === "add" && !password) {
              callback(new Error("请输入密码"));
              return;
            }
            if (password && password.length > 128) {
              callback(new Error("密码长度不能超过128个字符"));
              return;
            }
            if (password && password.length < 6) {
              callback(new Error("密码长度不能少于6位"));
              return;
            }
            callback();
          },
        },
      ],
      email: [
        { type: "email", message: "请输入正确的邮箱格式", trigger: "blur" },
        { max: 64, message: "邮箱长度不能超过64个字符", trigger: "blur" },
      ],
      gender: [{ required: true, message: "请选择性别", trigger: "change" }],
      phone: [{ max: 64, message: "手机号长度不能超过64个字符", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式
     * @param currentRow 当前行
     */
    const openModal = async (mode: "add" | "edit", currentRow: GetUserListVo | null) => {
      modalMode.value = mode;
      modalCurrentRow.value = currentRow;
      resetModal();

      // 获取组织架构树并处理，禁用企业节点
      const treeData = await OrgApi.getOrgTree({});
      orgTreeOptions.value = processOrgTreeData(treeData);

      //如果是编辑模式则需要加载详情数据
      if (mode === "edit" && currentRow) {
        try {
          const ret = await AdminUserApi.getUserDetails({ id: currentRow.id });

          modalForm.id = ret.id;
          modalForm.username = ret.username;
          modalForm.nickname = ret.nickname || "";
          modalForm.gender = ret.gender ?? 0;
          modalForm.phone = ret.phone || "";
          modalForm.email = ret.email || "";
          modalForm.status = ret.status;
          modalForm.isSystem = ret.isSystem ?? 0;
          modalForm.groups = ret.groups || [];
          modalForm.deptId = ret.deptId;

          groupOptions.value = ret.groups || [];
          selectedGroupIds.value = ret.groups ? ret.groups.filter((group) => group.hasGroup).map((group) => group.id) : [];
        } catch (error: any) {
          ElMessage.error(error.message);
          return;
        }
      }

      if (mode !== "edit" || !currentRow) {
        // 新增模式，获取用户组列表
        const groups = await GroupApi.getGroupList({ pageNum: 1, pageSize: 100000, status: 1 });
        groupOptions.value = [];
        groups.data.forEach((group) => {
          groupOptions.value.push({
            id: group.id,
            name: group.name,
            description: "",
            sortOrder: 0,
            isSystem: group.isSystem,
            hasGroup: false,
          });
        });
      }

      modalVisible.value = true;
    };

    /**
     * 重置模态框表单
     */
    const resetModal = () => {
      modalForm.id = "";
      modalForm.username = "";
      modalForm.nickname = "";
      modalForm.gender = 0;
      modalForm.phone = "";
      modalForm.email = "";
      modalForm.status = 0;
      modalForm.isSystem = 0;
      modalForm.groups = [];
      modalForm.deptId = "";
      modalFormPassword.value = "";
      selectedGroupIds.value = [];

      if (modalFormRef.value) {
        modalFormRef.value.resetFields();
      }
    };

    /**
     * 提交模态框表单
     */
    const submitModal = async () => {
      //先校验表单
      try {
        await modalFormRef?.value?.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      //提交表单
      try {
        if (modalMode.value === "add") {
          const addDto: AddUserDto = {
            username: modalForm.username,
            password: modalFormPassword.value,
            nickname: modalForm.nickname,
            gender: modalForm.gender,
            phone: modalForm.phone,
            email: modalForm.email,
            status: modalForm.status,
            deptId: modalForm.deptId || undefined,
            groupIds: selectedGroupIds.value,
          };
          const result = await AdminUserApi.addUser(addDto);
          if (Result.isSuccess(result)) {
            ElMessage.success("操作成功");
            resetModal();
          }
          if (Result.isError(result)) {
            ElMessage.error(result.message);
            return;
          }
        }

        if (modalMode.value === "edit") {
          const editDto: EditUserDto = {
            id: modalForm.id,
            username: modalForm.username,
            nickname: modalForm.nickname,
            gender: modalForm.gender,
            phone: modalForm.phone,
            email: modalForm.email,
            status: modalForm.status,
            deptId: modalForm.deptId || undefined,
            groupIds: selectedGroupIds.value,
          };
          if (modalFormPassword.value) {
            editDto.password = modalFormPassword.value;
          }
          const result = await AdminUserApi.editUser(editDto);
          if (Result.isSuccess(result)) {
            ElMessage.success("操作成功");
          }
          if (Result.isError(result)) {
            ElMessage.error(result.message);
            return;
          }
        }
      } catch (error: any) {
        ElMessage.error(error.message);
        return;
      } finally {
        modalLoading.value = false;
      }

      await loadList();
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalCurrentRow,
      modalForm,
      modalFormPassword,
      selectedGroupIds,
      groupOptions,
      modalRules,
      openModal,
      resetModal,
      submitModal,
      orgTreeOptions,
    };
  },
};
