<template>
  <div class="list-container">
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="用户名">
              <el-input v-model="listForm.username" placeholder="输入用户名查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="状态">
              <el-select v-model="listForm.status" placeholder="选择状态" clearable>
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
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetList" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建用户</el-button>
    </div>

    <div class="list-table">
      <el-table :data="listData" stripe v-loading="listLoading" border>
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
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="listForm.pageNum"
          v-model:page-size="listForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="listTotal"
          @size-change="
            (val: number) => {
              listForm.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              listForm.pageNum = val;
              loadList();
            }
          "
          background
        />
      </div>
    </div>

    <!-- 用户编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑用户' : '添加用户'"
      width="500px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="modalForm.username" :disabled="modalMode === 'edit'" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="modalFormPassword"
            type="password"
            show-password
            :placeholder="modalMode === 'add' ? '请输入密码' : '不修改密码请留空'"
          />
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
import { ref, reactive, onMounted, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import Http from "@/commons/Http.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";
import AdminUserApi, {
  type GetUserDetailsVo,
  type GetUserListDto,
  type GetUserListVo,
  type AddUserDto,
  type EditUserDto,
  type UserGroupVo,
} from "@/views/core/api/UserApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import GroupApi from "@/views/core/api/GroupApi.ts";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

//列表内容
const listForm = reactive<GetUserListDto>({
  pageNum: 1,
  pageSize: 10,
  username: "",
  status: null,
});

const listData = ref<GetUserListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

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
    { max: 50, message: "邮箱长度不能超过50个字符", trigger: "blur" },
  ],
};

const loadList = async () => {
  listLoading.value = true;
  const result = await AdminUserApi.getUserList(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    listTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.username = "";
  listForm.status = null;
  loadList();
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
    try {
      const ret = await AdminUserApi.getUserDetails({ id: row.id });

      modalForm.id = ret.id;
      modalForm.username = ret.username;
      modalForm.nickname = ret.nickname || "";
      modalForm.email = ret.email || "";
      modalForm.status = ret.status;
      modalForm.groups = ret.groups || [];

      groupOptions.value = ret.groups || [];
      selectedGroupIds.value = ret.groups ? ret.groups.filter((group) => group.hasGroup).map((group) => group.id) : [];
    } catch (error: any) {
      ElMessage.error(error.message);
      return;
    }
  }
  if (mode !== "edit" || !row) {
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

  await loadList();
};

const removeList = async (id: string) => {
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
    await AdminUserApi.removeUser({ id });
    ElMessage.success("删除成功");
    await loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

loadList();
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}
</style>
