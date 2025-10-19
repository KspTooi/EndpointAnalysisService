<template>
  <div v-show="visible" ref="menuRef" class="context-menu" :style="{ left: x + 'px', top: y + 'px' }" @click.stop>
    <div v-if="node?.type === 0" class="menu-item" @click="handleAddSubGroup">
      <el-icon><Plus /></el-icon>
      <span>新建子组</span>
    </div>

    <div class="menu-item" @click="handleEdit">
      <el-icon><Edit /></el-icon>
      <span>编辑{{ node?.type === 0 ? "分组" : "请求" }}</span>
    </div>

    <div class="menu-item" @click="handleCopy">
      <el-icon><CopyDocument /></el-icon>
      <span>复制{{ node?.type === 0 ? "分组" : "请求" }}</span>
    </div>

    <div class="menu-item" @click="handleDelete">
      <el-icon><Delete /></el-icon>
      <span>删除{{ node?.type === 0 ? "分组" : "请求" }}</span>
    </div>
  </div>

  <!-- 编辑对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    :title="`编辑${node?.type === 0 ? '分组' : '请求'}`"
    width="400px"
    class="modal-centered"
    @keyup.enter="handleConfirmEdit"
    @opened="focusModalEdit"
  >
    <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="80px">
      <el-form-item :label="`${node?.type === 0 ? '分组' : '请求'}名称`" prop="name">
        <el-input v-model="editForm.name" :placeholder="`请输入${node?.type === 0 ? '分组' : '请求'}名称`" ref="editInputRef" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer" style="gap: 10px; display: flex; justify-content: right">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmEdit" :loading="editLoading">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 新建子组对话框 -->
  <el-dialog
    v-model="addSubGroupDialogVisible"
    :title="`为 [${node?.name}] 新建子组`"
    width="400px"
    class="modal-centered"
    @keyup.enter="handleConfirmAddSubGroup"
    @opened="focusModalAddSubGroup"
  >
    <el-form ref="addSubGroupFormRef" :model="addSubGroupForm" :rules="addSubGroupRules" label-width="80px">
      <el-form-item label="子组名称" prop="name">
        <el-input v-model="addSubGroupForm.name" placeholder="请输入子组名称" ref="addSubGroupInputRef" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer" style="gap: 10px; display: flex; justify-content: right">
        <el-button @click="addSubGroupDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAddSubGroup" :loading="addSubGroupLoading">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick, onMounted, onUnmounted, watch } from "vue";
import { Edit, Delete, Plus, CopyDocument } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import UserRequestTreeApi from "@/api/userrequest/UserRequestTreeApi.ts";
import type { GetUserRequestTreeVo, EditUserRequestTreeDto, RemoveUserRequestTreeDto, AddUserRequestTreeDto } from "@/api/userrequest/UserRequestTreeApi.ts";
import UserRequestGroupApi from "@/api/userrequest/UserRequestGroupApi.ts";
import type { AddUserRequestGroupDto } from "@/api/userrequest/UserRequestGroupApi.ts";
import UserRequestApi from "@/api/userrequest/UserRequestApi.ts";
import { EventHolder } from "@/store/EventHolder";

interface Props {
  visible: boolean;
  x: number;
  y: number;
  node: GetUserRequestTreeVo | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  close: [];
  refresh: [];
}>();

const menuRef = ref<HTMLElement>();
const editDialogVisible = ref(false);
const editLoading = ref(false);
const editInputRef = ref<HTMLInputElement>();
const editFormRef = ref<FormInstance>();
const editForm = ref({
  name: "",
});

// 新建子组相关状态
const addSubGroupDialogVisible = ref(false);
const addSubGroupLoading = ref(false);
const addSubGroupInputRef = ref<HTMLInputElement>();
const addSubGroupFormRef = ref<FormInstance>();
const addSubGroupForm = ref({
  name: "",
});

const editRules = {
  name: [
    { required: true, message: "请输入名称", trigger: "blur" },
    { min: 1, max: 64, message: "名称长度应在 1 到 64 个字符之间", trigger: "blur" },
  ],
};

const addSubGroupRules = {
  name: [
    { required: true, message: "请输入子组名称", trigger: "blur" },
    { min: 1, max: 64, message: "子组名称长度应在 1 到 64 个字符之间", trigger: "blur" },
  ],
};

// 处理编辑
const handleEdit = () => {
  if (!props.node) return;

  editForm.value.name = props.node.name;
  editDialogVisible.value = true;
  emit("close");
};

// 确认编辑
const handleConfirmEdit = async () => {
  if (!editFormRef.value || !props.node) return;

  try {
    const valid = await editFormRef.value.validate();
    if (!valid) return;

    editLoading.value = true;

    const dto: EditUserRequestTreeDto = {
      id: props.node.id,
      name: editForm.value.name,
    };

    await UserRequestTreeApi.editUserRequestTree(dto);

    const nodeType = props.node.type === 0 ? "分组" : "请求";
    ElMessage.success(`编辑${nodeType}成功`);
    editDialogVisible.value = false;
    emit("refresh");

    //还需要刷新请求详情
    EventHolder().requestReloadRequestDetails();
  } catch (error: any) {
    const nodeType = props.node.type === 0 ? "分组" : "请求";
    ElMessage.error(error.message || `编辑${nodeType}失败`);
  } finally {
    editLoading.value = false;
  }
};

// 处理复制
const handleCopy = async () => {
  if (!props.node) return;

  try {
    await UserRequestTreeApi.copyUserRequestTree({ id: props.node.id });
    ElMessage.success("复制成功");
    emit("refresh");
    emit("close");
  } catch (error: any) {
    ElMessage.error(error.message || "复制失败");
  }
};

// 处理新建子组
const handleAddSubGroup = () => {
  if (!props.node || props.node.type !== 0) return;

  addSubGroupForm.value.name = "";
  addSubGroupDialogVisible.value = true;
  emit("close");
};

// 确认新建子组
const handleConfirmAddSubGroup = async () => {
  if (!addSubGroupFormRef.value || !props.node) return;

  try {
    const valid = await addSubGroupFormRef.value.validate();
    if (!valid) return;

    addSubGroupLoading.value = true;

    const dto: AddUserRequestTreeDto = {
      parentId: props.node.id,
      kind: 0, //0:组 1:请求
      name: addSubGroupForm.value.name,
    };

    await UserRequestTreeApi.addUserRequestTree(dto);

    ElMessage.success("新建子组成功");
    addSubGroupDialogVisible.value = false;
    emit("refresh");
  } catch (error: any) {
    ElMessage.error(error.message || "新建子组失败");
  } finally {
    addSubGroupLoading.value = false;
  }
};

// 处理删除
const handleDelete = async () => {
  if (!props.node) return;

  const nodeType = props.node.type === 0 ? "分组" : "请求";

  try {
    await ElMessageBox.confirm(`确认删除此${nodeType}吗？`, "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const dto: RemoveUserRequestTreeDto = {
      id: props.node.id,
    };

    await UserRequestTreeApi.removeUserRequestTree(dto);

    ElMessage.success(`删除${nodeType}成功`);
    emit("close");
    emit("refresh");
  } catch (error: any) {
    emit("close");
    if (error !== "cancel") {
      ElMessage.error(error.message || `删除${nodeType}失败`);
    }
  }
};

// 监听点击外部关闭菜单
const handleClickOutside = (event: MouseEvent) => {
  if (menuRef.value && !menuRef.value.contains(event.target as Node)) {
    emit("close");
  }
};

onMounted(() => {
  document.addEventListener("click", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("click", handleClickOutside);
});

// 聚焦编辑对话框
const focusModalEdit = () => {
  nextTick(() => {
    editInputRef.value?.focus();
  });
};

// 聚焦新建子组对话框
const focusModalAddSubGroup = () => {
  nextTick(() => {
    addSubGroupInputRef.value?.focus();
  });
};
</script>

<style scoped>
.context-menu {
  position: fixed;
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  z-index: 2000;
  min-width: 120px;
  padding: 4px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
  user-select: none;
}

.menu-item:hover {
  background: #f5f7fa;
  color: #409eff;
}

.menu-item .el-icon {
  font-size: 16px;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
