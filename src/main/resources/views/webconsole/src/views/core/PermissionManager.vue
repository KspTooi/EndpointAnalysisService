<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="权限代码">
              <el-input v-model="listForm.code" placeholder="输入权限代码查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="权限名称">
              <el-input v-model="listForm.name" placeholder="输入权限名称查询" clearable />
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
      <el-button type="success" @click="openModal('add', null)">创建权限节点</el-button>
    </div>

    <!-- 权限列表 -->
    <div class="list-table">
      <el-table :data="listData" stripe v-loading="listLoading" border>
        <el-table-column
          prop="code"
          label="权限代码"
          min-width="150"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column
          prop="name"
          label="权限名称"
          min-width="150"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column
          prop="description"
          label="权限描述"
          min-width="200"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column label="系统权限" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isSystem === 1 ? 'warning' : 'info'">
              {{ scope.row.isSystem === 1 ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon" :disabled="scope.row.isSystem === 1"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 权限编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑权限节点' : '添加权限节点'"
      width="500px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="rules" label-width="100px" :validate-on-rule-change="false">
        <!-- 编辑时显示的只读信息 -->
        <template v-if="modalMode === 'edit'">
          <el-form-item label="创建时间">
            <el-input v-model="modalForm.createTime" disabled />
          </el-form-item>
          <el-form-item label="修改时间">
            <el-input v-model="modalForm.updateTime" disabled />
          </el-form-item>
          <el-form-item label="系统权限">
            <el-tag :type="modalForm.isSystem === 1 ? 'warning' : 'info'">
              {{ modalForm.isSystem === 1 ? "是" : "否" }}
            </el-tag>
          </el-form-item>
        </template>

        <!-- 可编辑字段 -->
        <el-form-item label="权限代码" prop="code">
          <el-input
            v-model="modalForm.code"
            :disabled="modalMode === 'edit' && modalForm.isSystem === 1"
            :placeholder="modalMode === 'edit' && modalForm.isSystem === 1 ? '系统权限不可修改代码' : '请输入权限代码'"
          />
        </el-form-item>
        <el-form-item label="权限名称" prop="name">
          <el-input
            v-model="modalForm.name"
            :disabled="modalMode === 'edit' && modalForm.isSystem === 1"
            :placeholder="modalMode === 'edit' && modalForm.isSystem === 1 ? '系统权限不可修改名称' : '请输入权限名称'"
          />
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input v-model="modalForm.description" type="textarea" :rows="3" placeholder="请输入权限描述" />
        </el-form-item>
        <el-form-item label="排序顺序" prop="sortOrder">
          <el-input-number v-model="modalForm.sortOrder" :min="0" :max="9999" />
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
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Edit, Delete } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance } from "element-plus";
import AdminPermissionApi, {
  type GetPermissionDetailsVo,
  type GetPermissionListDto,
  type GetPermissionListVo,
  type AddPermissionDto,
  type EditPermissionDto,
} from "@/api/core/PermissionApi.ts";
import { Result } from "@/commons/entity/Result.ts";

const modalMode = ref<"add" | "edit">("add");

const listForm = reactive<GetPermissionListDto>({
  code: null,
  name: null,
  pageNum: 1,
  pageSize: 10,
});
const listData = ref<GetPermissionListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const modalForm = reactive<GetPermissionDetailsVo>({
  code: "",
  createTime: "",
  description: "",
  id: "",
  isSystem: 0,
  name: "",
  sortOrder: 0,
  updateTime: "",
});

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 模态框相关
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);

// 表单校验规则
const rules = {
  code: [
    { required: true, message: "请输入权限代码", trigger: "blur" },
    {
      pattern: /^[a-z]([a-z0-9\-]*[a-z0-9])*(:[a-z]([a-z0-9\-]*[a-z0-9])*)*$/,
      message: "权限标识格式错误，只允许小写字母、数字、连字符，以及冒号作为分隔符",
      trigger: "blur",
    },
  ],
  name: [
    { required: true, message: "请输入权限名称", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  description: [{ max: 200, message: "描述不能超过200个字符", trigger: "blur" }],
};

const loadList = async () => {
  listLoading.value = true;
  const result = await AdminPermissionApi.getPermissionList(listForm);

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
  listForm.code = null;
  listForm.name = null;
  loadList();
};

// 重置表单
const resetModal = () => {
  modalForm.id = "";
  modalForm.code = "";
  modalForm.name = "";
  modalForm.description = "";
  modalForm.sortOrder = 0;
  modalForm.isSystem = 0;
  modalForm.createTime = "";
  modalForm.updateTime = "";

  if (modalFormRef.value) {
    modalFormRef.value.resetFields();
  }
};

loadList();

//打开模态框
const openModal = async (mode: "add" | "edit", row: GetPermissionListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    try {
      const ret = await AdminPermissionApi.getPermissionDetails({ id: row.id });
      modalForm.id = ret.id;
      modalForm.code = ret.code;
      modalForm.name = ret.name;
      modalForm.description = ret.description;
      modalForm.sortOrder = ret.sortOrder;
      modalForm.isSystem = ret.isSystem;
      modalForm.createTime = ret.createTime;
      modalForm.updateTime = ret.updateTime;
    } catch (error: any) {
      ElMessage.error(error.message || "获取权限详情失败");
      return;
    }
  }

  modalVisible.value = true;
};

//提交表单
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
      const addDto: AddPermissionDto = {
        code: modalForm.code,
        name: modalForm.name,
        description: modalForm.description,
        sortOrder: modalForm.sortOrder,
      };
      const result = await AdminPermissionApi.addPermission(addDto);
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
      const editDto: EditPermissionDto = {
        id: modalForm.id,
        code: modalForm.code,
        name: modalForm.name,
        description: modalForm.description,
        sortOrder: modalForm.sortOrder,
      };
      const result = await AdminPermissionApi.editPermission(editDto);
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

  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该权限吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await AdminPermissionApi.removePermission({ id });
    ElMessage.success("删除成功");
    loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};
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

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
