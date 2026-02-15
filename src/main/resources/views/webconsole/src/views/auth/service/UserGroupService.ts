import { ref, reactive, computed, type Ref, onMounted } from "vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import AdminGroupApi, {
  type GetGroupListDto,
  type GetGroupListVo,
  type GroupPermissionDefinitionVo,
  type   GetGroupDetailsVo,
  type AddGroupDto,
  type EditGroupDto,
} from "@/views/auth/api/GroupApi.ts";
import AdminPermissionApi from "@/views/auth/api/PermissionApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi";

export default {
  /**
   * 用户组列表打包
   */
  useUserGroupList() {
    const listForm = reactive<GetGroupListDto>({
      pageNum: 1,
      pageSize: 20,
      keyword: "",
      status: undefined,
    });

    const listData = ref<GetGroupListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      if (listLoading.value) {
        return;
      }

      listLoading.value = true;
      const result = await AdminGroupApi.getGroupList(listForm);

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
      listForm.pageNum = 1;
      listForm.pageSize = 20;
      listForm.keyword = "";
      listForm.status = undefined;
      loadList();
    };

    /**
     * 删除项
     */
    const removeList = async (id: string) => {
      try {
        await ElMessageBox.confirm("确定删除该用户组吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await AdminGroupApi.removeGroup({ id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 批量删除
     */
    const removeListBatch = async (selectedItems: GetGroupListVo[]) => {
      if (selectedItems.length === 0) {
        ElMessage.warning("请选择要删除的用户组");
        return;
      }

      try {
        await ElMessageBox.confirm(`确定删除选中的 ${selectedItems.length} 个用户组吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        const ids = selectedItems.map((item) => item.id);
        await AdminGroupApi.removeGroup({ ids });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(() => {
      loadList();
    });

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      loadList,
      resetList,
      removeList,
      removeListBatch,
    };
  },

  /**
   * 用户组模态框打包
   */
  useUserGroupModal(modalFormRef: Ref<FormInstance | undefined>, loadList: () => void) {
    const modalVisible = ref(false);
    const modalMode = ref<"add" | "edit">("add");
    const modalLoading = ref(false);
    const isSystemGroup = ref(false);

    // 表单数据
    const modalForm = reactive<GetGroupDetailsVo>({
      id: "",
      code: "",
      name: "",
      remark: "",
      isSystem: 0,
      status: 1,
      seq: 0,
      permissions: [],
      rowScope: 0,
      deptIds: [],
    });

    // 表单校验规则
    const modalRules = {
      code: [
        { required: true, message: "请输入组标识", trigger: "blur" },
        { min: 2, max: 50, message: "组标识长度必须在2-50个字符之间", trigger: "blur" },
        {
          pattern: /^[a-zA-Z][a-zA-Z_]*$/,
          message: "组标识只能包含英文字符和下划线，且必须以字母开头",
          trigger: "blur",
        },
      ],
      name: [
        { required: true, message: "请输入组名称", trigger: "blur" },
        { min: 2, max: 50, message: "组名称长度必须在2-50个字符之间", trigger: "blur" },
      ],
      remark: [{ max: 200, message: "描述不能超过200个字符", trigger: "blur" }],
      seq: [
        { required: true, message: "请输入排序号", trigger: "blur" },
        { min: 0, message: "排序号必须大于等于0", trigger: "blur" },
      ],
      rowScope: [{ required: true, message: "请选择数据权限范围", trigger: "change" }],
    };

    // 权限相关
    const permissionList = ref<GroupPermissionDefinitionVo[]>([]);
    const permissionSearch = ref("");
    const selectedPermissionIds = ref<string[]>([]);

    const filteredPermissions = computed(() => {
      const search = permissionSearch.value.toLowerCase().trim();
      if (!search) {
        return permissionList.value;
      }
      return permissionList.value.filter(
        (permission) => permission.name.toLowerCase().includes(search) || permission.code.toLowerCase().includes(search)
      );
    });

    // 部门选择相关
    const deptSelectModalVisible = ref(false);
    const selectedDepts = ref<GetOrgTreeVo[]>([]);

    const openDeptSelect = () => {
      deptSelectModalVisible.value = true;
    };

    const onDeptSelectConfirm = (depts: GetOrgTreeVo | GetOrgTreeVo[]) => {
      if (Array.isArray(depts)) {
        selectedDepts.value = depts;
        modalForm.deptIds = depts.map((d) => d.id);
      }
    };

    const removeDept = (deptId: string) => {
      selectedDepts.value = selectedDepts.value.filter((d) => d.id !== deptId);
      modalForm.deptIds = selectedDepts.value.map((d) => d.id);
    };

    /**
     * 重置模态框
     */
    const resetModal = async () => {
      modalForm.id = "";
      modalForm.code = "";
      modalForm.name = "";
      modalForm.remark = "";
      modalForm.status = 1;
      modalForm.seq = 0;
      modalForm.permissions = [];
      modalForm.rowScope = 0;
      modalForm.deptIds = [];

      permissionSearch.value = "";
      selectedPermissionIds.value = [];
      selectedDepts.value = [];

      if (modalMode.value === "add") {
        try {
          const permissions = await AdminPermissionApi.getPermissionDefinition();
          permissionList.value = permissions.map((p) => ({
            id: p.id,
            code: p.code,
            name: p.name,
            has: 0,
          }));
        } catch (error) {
          // 静默处理
        }
      }

      if (modalMode.value !== "add") {
        permissionList.value = [];
      }

      if (modalFormRef.value) {
        modalFormRef.value.resetFields();
      }
    };

    /**
     * 打开模态框
     */
    const openModal = async (mode: "add" | "edit", row: GetGroupListVo | null) => {
      modalMode.value = mode;
      await resetModal();

      if (mode === "edit" && row) {
        isSystemGroup.value = row.isSystem;
        try {
          const ret = await AdminGroupApi.getGroupDetails({ id: row.id });
          modalForm.id = ret.id;
          modalForm.code = ret.code;
          modalForm.name = ret.name;
          modalForm.remark = ret.remark;
          modalForm.status = ret.status;
          modalForm.seq = ret.seq;
          modalForm.permissions = ret.permissions || [];
          modalForm.rowScope = ret.rowScope ?? 0;
          modalForm.deptIds = ret.deptIds || [];

          permissionList.value = ret.permissions || [];
          selectedPermissionIds.value = ret.permissions ? ret.permissions.filter((p) => p.has === 0).map((p) => p.id) : [];

          // 如果是指定部门，需要初始化已选部门列表 (由于详情接口只返回了ID，这里可能需要额外查询部门名称，或者在详情接口中增加部门详情)
          // 暂时简化处理，实际开发中可能需要调用部门详情接口
          if (modalForm.deptIds.length > 0) {
            selectedDepts.value = modalForm.deptIds.map((id) => ({ id, name: `部门(${id})`, kind: 0 } as GetOrgTreeVo));
          }
        } catch (error: any) {
          ElMessage.error(error.message || "获取用户组详情失败");
          return;
        }
      }

      if (mode !== "edit" || !row) {
        isSystemGroup.value = false;
      }

      modalVisible.value = true;
    };

    /**
     * 提交模态框
     */
    const submitModal = async () => {
      try {
        await modalFormRef?.value?.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      try {
        if (modalMode.value === "add") {
          const addDto: AddGroupDto = {
            code: modalForm.code,
            name: modalForm.name,
            remark: modalForm.remark,
            status: modalForm.status,
            seq: modalForm.seq,
            rowScope: modalForm.rowScope,
            deptIds: modalForm.deptIds,
            permissionIds: selectedPermissionIds.value,
          };
          const result = await AdminGroupApi.addGroup(addDto);
          if (Result.isSuccess(result)) {
            ElMessage.success("操作成功");
            await resetModal();
          }
          if (Result.isError(result)) {
            ElMessage.error(result.message);
            return;
          }
        }

        if (modalMode.value === "edit") {
          const editDto: EditGroupDto = {
            id: modalForm.id,
            code: modalForm.code,
            name: modalForm.name,
            remark: modalForm.remark,
            status: modalForm.status,
            seq: modalForm.seq,
            rowScope: modalForm.rowScope,
            deptIds: modalForm.deptIds,
            permissionIds: selectedPermissionIds.value,
          };
          const result = await AdminGroupApi.editGroup(editDto);
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

    const selectAllPermissions = () => {
      selectedPermissionIds.value = filteredPermissions.value.map((p) => p.id);
    };

    const deselectAllPermissions = () => {
      selectedPermissionIds.value = [];
    };

    return {
      modalVisible,
      modalMode,
      modalLoading,
      isSystemGroup,
      modalForm,
      modalRules,
      permissionList,
      permissionSearch,
      selectedPermissionIds,
      filteredPermissions,
      deptSelectModalVisible,
      selectedDepts,
      openDeptSelect,
      onDeptSelectConfirm,
      removeDept,
      openModal,
      resetModal,
      submitModal,
      selectAllPermissions,
      deselectAllPermissions,
    };
  },

  /**
   * 权限管理模态框打包
   */
  useUserGroupPermissionModal() {
    const modalPermissionEditVisible = ref(false);
    const modalPermissionEditRow = ref<GetGroupListVo | null>(null);

    const openPermissionEditModal = (row: GetGroupListVo) => {
      modalPermissionEditRow.value = row;
      modalPermissionEditVisible.value = true;
    };

    return {
      modalPermissionEditVisible,
      modalPermissionEditRow,
      openPermissionEditModal,
    };
  },
};
