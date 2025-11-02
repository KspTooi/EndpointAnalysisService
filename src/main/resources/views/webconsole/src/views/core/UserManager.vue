<template>
  <div class="user-manager-container">
    <div class="query-form">
      <el-form :model="queryForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="用户名">
              <el-input v-model="queryForm.username" placeholder="输入用户名查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="状态">
              <el-select v-model="queryForm.status" placeholder="选择状态" clearable>
                <el-option label="正常" :value="0" />
                <el-option label="封禁" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="loadUserList" :disabled="loading">查询</el-button>
              <el-button @click="resetQuery" :disabled="loading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建用户</el-button>
    </div>

    <div class="user-table">
      <el-table :data="userList" stripe v-loading="loading" border>
        <el-table-column prop="username" label="用户名" min-width="150" />
        <el-table-column prop="nickname" label="昵称" min-width="150" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column prop="lastLoginTime" label="最后登录时间" min-width="180" />
        <el-table-column label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? "正常" : "封禁" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <!-- 桌面端分页 -->
        <el-pagination
          v-if="!isMobile"
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
        <!-- 移动端分页 -->
        <el-pagination
          v-else
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          layout="prev, pager, next"
          :total="total"
          @current-change="handleCurrentChange"
          :pager-count="5"
          small
          background
        />
      </div>
    </div>

    <!-- 用户编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑用户' : '添加用户'"
      width="500px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadUserList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="userFormRules" label-width="100px" :validate-on-rule-change="false">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="modalForm.username" :disabled="modalMode === 'edit'" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="modalFormPassword" type="password" show-password :placeholder="modalMode === 'add' ? '请输入密码' : '不修改密码请留空'" />
          <div v-if="modalMode === 'edit'" class="form-tip">不修改密码请留空</div>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="modalForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="modalForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="modalForm.status" placeholder="请选择状态">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">封禁</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="所属用户组" prop="groupIds">
          <el-select v-model="selectedGroupIds" multiple placeholder="请选择用户组" style="width: 100%">
            <el-option v-for="group in groupOptions" :key="group.id" :label="group.name" :value="group.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, markRaw, computed } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import Http from "@/commons/Http.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import AdminUserApi, { type GetUserDetailsVo, type GetUserListDto, type GetUserListVo, type AddUserDto, type EditUserDto, type UserGroupVo } from "@/api/core/UserApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import GroupApi from "@/api/core/GroupApi.ts";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 检测是否为移动设备
const isMobile = ref(false);
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768;
};

// 查询表单
const queryForm = reactive<GetUserListDto>({
  pageNum: 1,
  pageSize: 10,
  username: "",
  status: null,
});

// 用户列表
const userList = ref<GetUserListVo[]>([]);
const total = ref(0);
const loading = ref(false);

// 模态框相关
const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const modalLoading = ref(false);
const modalFormRef = ref<FormInstance>();

// 用户组选项，将从 API 获取
const groupOptions = ref<UserGroupVo[]>([]);

// 表单数据
const modalForm = reactive<GetUserDetailsVo>({
  id: "",
  username: "",
  nickname: "",
  email: "",
  status: 0,
  createTime: "",
  lastLoginTime: "",
  groups: [],
  permissions: [],
});
const modalFormPassword = ref("");
const selectedGroupIds = ref<string[]>([]);

// 表单校验规则 用户密码长度不能超过128个字符, 用户昵称长度不能超过50个字符
const userFormRules = {
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
    { max: 50, message: "邮箱长度不能超过50个字符", trigger: "blur" },
  ],
};

// 加载用户列表数据
const loadUserList = async () => {
  try {
    loading.value = true;
    let vos = await AdminUserApi.getUserList(queryForm);
    userList.value = vos.data;
    total.value = Number(vos.total);
  } catch (error) {
    ElMessage.error("加载用户列表失败");
    console.error("加载用户列表失败", error);
  } finally {
    loading.value = false;
  }
};

// 重置查询条件
const resetQuery = () => {
  queryForm.username = "";
  queryForm.status = null;
  queryForm.pageNum = 1;
  loadUserList();
};

// 处理每页大小变化
const handleSizeChange = (val: number) => {
  queryForm.pageSize = val;
  loadUserList();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  queryForm.pageNum = val;
  loadUserList();
};

// 重置表单
const resetModal = () => {
  modalForm.id = "";
  modalForm.username = "";
  modalForm.nickname = "";
  modalForm.email = "";
  modalForm.status = 0;
  modalForm.groups = [];
  modalFormPassword.value = "";
  selectedGroupIds.value = [];

  if (modalFormRef.value) {
    modalFormRef.value.resetFields();
  }
};

// 打开模态框
const openModal = async (mode: "add" | "edit", row: GetUserListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    loading.value = true;
    try {
      const userDetails: GetUserDetailsVo = await AdminUserApi.getUserDetails({ id: row.id });

      modalForm.id = userDetails.id;
      modalForm.username = userDetails.username;
      modalForm.nickname = userDetails.nickname || "";
      modalForm.email = userDetails.email || "";
      modalForm.status = userDetails.status;
      modalForm.groups = userDetails.groups || [];

      groupOptions.value = userDetails.groups || [];
      selectedGroupIds.value = userDetails.groups ? userDetails.groups.filter((group) => group.hasGroup).map((group) => group.id) : [];
    } catch (error) {
      ElMessage.error("获取用户详情失败");
      console.error("获取用户详情失败", error);
      return;
    } finally {
      loading.value = false;
    }
  } else {
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

// 提交表单
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
        email: modalForm.email,
        status: modalForm.status,
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
        email: modalForm.email,
        status: modalForm.status,
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

  loadUserList();
};

// 处理删除用户
const handleDelete = (row: GetUserListVo) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.username} 吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const params: CommonIdDto = { id: row.id };
        await Http.postEntity<string>("/user/removeUser", params);
        ElMessage.success("删除用户成功");
        loadUserList(); // 重新加载列表
      } catch (error) {
        const errorMsg = error instanceof Error ? error.message : "删除失败";
        ElMessage.error(errorMsg);
      }
    })
    .catch(() => {
      // 用户取消删除操作
    });
};

// 页面加载和窗口大小变化时检测设备类型
onMounted(() => {
  checkMobile();
  window.addEventListener("resize", checkMobile);
  loadUserList();

  // 不再需要模拟数据，用户组选项将在编辑时从详情接口获取
  // TODO: 如果需要在新增时也能选择用户组，需要独立获取用户组列表的逻辑
});
</script>

<style scoped>
.user-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.user-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  width: 100%;
}

@media (min-width: 768px) {
  .pagination-container {
    justify-content: flex-end;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style>
