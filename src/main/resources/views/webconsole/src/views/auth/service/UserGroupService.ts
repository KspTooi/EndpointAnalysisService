import { ref, reactive, computed, type Ref, onMounted } from "vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import AdminGroupApi, {
    type GetGroupListDto,
    type GetGroupListVo,
    type GroupPermissionDefinitionVo,
    type GetGroupDetailsVo,
    type AddGroupDto,
    type EditGroupDto,
} from "@/views/auth/api/GroupApi.ts";
import AdminPermissionApi from "@/views/auth/api/PermissionApi.ts";
import { Result } from "@/commons/entity/Result.ts";

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
                await ElMessageBox.confirm("确定删除该访问组吗？", "提示", {
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
                ElMessage.warning("请选择要删除的访问组");
                return;
            }

            // 过滤掉系统内置组
            const deletableItems = selectedItems.filter((item) => !item.isSystem);

            if (deletableItems.length === 0) {
                ElMessage.warning("选中的项均为系统组，不可删除");
                return;
            }

            try {
                await ElMessageBox.confirm(`确定删除选中的${deletableItems.length}个访问组吗？（系统组将被自动跳过）`, "提示", {
                    confirmButtonText: "确定",
                    cancelButtonText: "取消",
                    type: "warning",
                });
            } catch (error) {
                return;
            }

            try {
                const ids = deletableItems.map((item) => item.id);
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
            description: "",
            isSystem: false,
            status: 1,
            sortOrder: 0,
            permissions: [],
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
            description: [{ max: 200, message: "描述不能超过200个字符", trigger: "blur" }],
            sortOrder: [
                { required: true, message: "请输入排序号", trigger: "blur" },
                { min: 0, message: "排序号必须大于等于0", trigger: "blur" },
            ],
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

        /**
         * 重置模态框
         */
        const resetModal = async () => {
            modalForm.id = "";
            modalForm.code = "";
            modalForm.name = "";
            modalForm.description = "";
            modalForm.status = 1;
            modalForm.sortOrder = 0;
            modalForm.permissions = [];

            permissionSearch.value = "";
            selectedPermissionIds.value = [];

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
                    modalForm.description = ret.description;
                    modalForm.status = ret.status;
                    modalForm.sortOrder = ret.sortOrder;
                    modalForm.permissions = ret.permissions || [];

                    permissionList.value = ret.permissions || [];
                    selectedPermissionIds.value = ret.permissions ? ret.permissions.filter((p) => p.has === 0).map((p) => p.id) : [];
                } catch (error: any) {
                    ElMessage.error(error.message || "获取访问组详情失败");
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
                        description: modalForm.description,
                        status: modalForm.status,
                        sortOrder: modalForm.sortOrder,
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
                        description: modalForm.description,
                        status: modalForm.status,
                        sortOrder: modalForm.sortOrder,
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
