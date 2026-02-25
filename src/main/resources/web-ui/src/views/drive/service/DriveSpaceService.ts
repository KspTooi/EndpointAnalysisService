import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetDriveSpaceListDto,
  GetDriveSpaceListVo,
  GetDriveSpaceDetailsVo,
  GetDriveSpaceMemberDetailsVo,
  AddDriveSpaceDto,
  EditDriveSpaceDto,
  EditDriveSpaceMembersDto,
} from "@/views/drive/api/DriveSpaceApi.ts";
import DriveSpaceApi from "@/views/drive/api/DriveSpaceApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi.ts";
import type { GetUserListVo } from "@/views/core/api/UserApi.ts";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 云盘空间选择器（用于下拉选择，一次加载全量）
   */
  useDriveSpaceSelector() {
    const spaceList = ref<GetDriveSpaceListVo[]>([]);
    const spaceLoading = ref(false);

    /**
     * 加载全量云盘空间列表
     */
    const loadSpaceList = async () => {
      spaceLoading.value = true;
      const result = await DriveSpaceApi.getDriveSpaceList({
        pageNum: 1,
        pageSize: 10000,
        name: "",
        remark: "",
        status: null,
      });

      if (Result.isSuccess(result)) {
        spaceList.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      spaceLoading.value = false;
    };

    /* onMounted(async () => {
      await loadSpaceList();
    }); */

    return {
      spaceList,
      spaceLoading,
      loadSpaceList,
    };
  },

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
    // quotaLimit 表单以 MB 为单位，回填时 bytes→MB，发送时 MB→bytes
    const modalForm = reactive<GetDriveSpaceDetailsVo>({
      id: "",
      name: "",
      remark: "",
      quotaLimit: "",
      status: 0,
      members: [],
    });

    // 新增/编辑时维护的成员列表（独立于 modalForm，便于单独操作）
    const modalMembers = ref<GetDriveSpaceMemberDetailsVo[]>([]);

    // edit 模式下成员操作的全局 loading
    const memberOpLoading = ref(false);

    /**
     * 重新从接口加载当前空间的成员列表
     */
    const reloadMembers = async () => {
      const details = await DriveSpaceApi.getDriveSpaceDetails({ id: modalForm.id });
      modalMembers.value = [];
      for (const m of details.members) {
        modalMembers.value.push({ ...m });
      }
    };

    /**
     * 将选中的用户批量添加到成员列表（去重，add 模式纯本地；edit 模式调接口后刷新）
     */
    const addUserMembers = async (users: GetUserListVo[]) => {
      if (modalMode.value === "add") {
        for (const user of users) {
          let exists = false;
          for (const m of modalMembers.value) {
            if (m.memberKind === 0 && m.memberId === user.id) {
              exists = true;
              break;
            }
          }
          if (exists) {
            continue;
          }
          modalMembers.value.push({
            id: "",
            driveSpaceId: "",
            memberName: user.nickname || user.username,
            memberKind: 0,
            memberId: user.id,
            role: 3,
          });
        }
        return;
      }

      memberOpLoading.value = true;
      try {
        for (const user of users) {
          let exists = false;
          for (const m of modalMembers.value) {
            if (m.memberKind === 0 && m.memberId === user.id) {
              exists = true;
              break;
            }
          }
          if (exists) {
            continue;
          }
          await DriveSpaceApi.editDriveSpaceMembers({
            driveSpaceId: modalForm.id,
            memberId: user.id,
            memberKind: 0,
            role: 3,
            action: 0,
          });
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        await reloadMembers();
        memberOpLoading.value = false;
      }
    };

    /**
     * 将选中的部门批量添加到成员列表（去重，add 模式纯本地；edit 模式调接口后刷新）
     */
    const addDeptMembers = async (depts: GetOrgTreeVo[]) => {
      if (modalMode.value === "add") {
        for (const dept of depts) {
          let exists = false;
          for (const m of modalMembers.value) {
            if (m.memberKind === 1 && m.memberId === dept.id) {
              exists = true;
              break;
            }
          }
          if (exists) {
            continue;
          }
          modalMembers.value.push({
            id: "",
            driveSpaceId: "",
            memberName: dept.name,
            memberKind: 1,
            memberId: dept.id,
            role: 3,
          });
        }
        return;
      }

      memberOpLoading.value = true;
      try {
        for (const dept of depts) {
          let exists = false;
          for (const m of modalMembers.value) {
            if (m.memberKind === 1 && m.memberId === dept.id) {
              exists = true;
              break;
            }
          }
          if (exists) {
            continue;
          }
          await DriveSpaceApi.editDriveSpaceMembers({
            driveSpaceId: modalForm.id,
            memberId: dept.id,
            memberKind: 1,
            role: 3,
            action: 0,
          });
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        await reloadMembers();
        memberOpLoading.value = false;
      }
    };

    /**
     * edit 模式：修改成员角色，调接口后刷新
     */
    const editUpdateMemberRole = async (member: GetDriveSpaceMemberDetailsVo) => {
      memberOpLoading.value = true;
      try {
        await DriveSpaceApi.editDriveSpaceMembers({
          driveSpaceId: modalForm.id,
          memberId: member.memberId,
          memberKind: member.memberKind,
          role: member.role,
          action: 0,
        });
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        memberOpLoading.value = false;
        await reloadMembers();
      }
    };

    /**
     * edit 模式：删除成员，二次确认后调接口刷新
     */
    const editRemoveMember = async (member: GetDriveSpaceMemberDetailsVo) => {
      try {
        await ElMessageBox.confirm(`确定移除成员「${member.memberName}」吗？`, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }
      memberOpLoading.value = true;
      try {
        await DriveSpaceApi.editDriveSpaceMembers({
          driveSpaceId: modalForm.id,
          memberId: member.memberId,
          memberKind: member.memberKind,
          role: member.role,
          action: 1,
        });
      } catch (error: any) {
        ElMessage.error(error.message);
      } finally {
        memberOpLoading.value = false;
        await reloadMembers();
      }
    };

    /**
     * add 模式：从成员列表移除一条记录（纯本地）
     */
    const removeMember = (index: number) => {
      modalMembers.value.splice(index, 1);
    };

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      name: [
        { required: true, message: "请输入空间名称", trigger: "blur" },
        { max: 80, message: "空间名称不超过80个字符", trigger: "blur" },
      ],
      remark: [{ max: 65535, message: "空间描述过长", trigger: "blur" }],
      quotaLimit: [{ required: true, message: "请输入配额限制(MB)", trigger: "blur" }],
      status: [{ required: true, message: "请选择状态", trigger: "change" }],
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
        modalForm.quotaLimit = "1024";
        modalForm.status = 0;
        modalMembers.value = [];
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
          // 后端返回 bytes，转换为 MB 回填
          modalForm.quotaLimit = details.quotaLimit ? String(Math.round(Number(details.quotaLimit) / 1048576)) : "";
          modalForm.status = details.status;
          // 回填成员列表
          modalMembers.value = [];
          for (const m of details.members) {
            modalMembers.value.push({ ...m });
          }
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
      modalMembers.value = [];
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
          const addDtoMembers = [];
          for (const m of modalMembers.value) {
            addDtoMembers.push({
              memberKind: m.memberKind,
              memberId: m.memberId,
              role: m.role,
            });
          }
          const addDto: AddDriveSpaceDto = {
            name: modalForm.name,
            remark: modalForm.remark,
            // MB 转换为 bytes
            quotaLimit: String(Number(modalForm.quotaLimit) * 1048576),
            status: modalForm.status,
            members: addDtoMembers,
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
            // MB 转换为 bytes
            quotaLimit: String(Number(modalForm.quotaLimit) * 1048576),
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
      modalMembers,
      memberOpLoading,
      openModal,
      resetModal,
      submitModal,
      addUserMembers,
      addDeptMembers,
      removeMember,
      editUpdateMemberRole,
      editRemoveMember,
    };
  },
};
