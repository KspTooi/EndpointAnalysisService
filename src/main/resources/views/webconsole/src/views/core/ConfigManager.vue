<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="关键字">
              <el-input v-model="listForm.keyword" placeholder="配置键/值/描述" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="所有者名称">
              <el-input v-model="listForm.userName" placeholder="输入所有者名称查询" clearable />
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
      <el-button type="success" @click="openModal('add', null)">创建配置</el-button>
    </div>

    <!-- 配置列表 -->
    <div class="list-table">
      <el-table :data="listData" stripe v-loading="listLoading" border>
        <el-table-column
          prop="configKey"
          label="配置键"
          min-width="210"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column
          prop="configValue"
          label="配置值"
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
          label="配置描述"
          min-width="180"
          show-overflow-tooltip
          :show-overflow-tooltip-props="{
            effect: 'dark',
            placement: 'top',
            enterable: false,
          }"
        />
        <el-table-column prop="userName" label="所有者" min-width="120" />
        <el-table-column label="操作" fixed="right" min-width="140">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
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

    <!-- 配置编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑配置' : '添加配置'"
      width="500px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <div v-if="modalMode === 'add'" style="color: #909399; font-size: 13px; margin-bottom: 10px">提示：新建配置项时，所有者默认为当前用户，无法指定为他人。</div>
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="100px" :validate-on-rule-change="false">
        <!-- 编辑时显示的只读信息 -->
        <template v-if="modalMode === 'edit'">
          <el-form-item label="创建时间">
            <el-input v-model="modalForm.createTime" disabled />
          </el-form-item>
          <el-form-item label="修改时间">
            <el-input v-model="modalForm.updateTime" disabled />
          </el-form-item>
          <el-form-item label="所有者名称">
            <el-input v-model="modalForm.userName" disabled />
          </el-form-item>
        </template>

        <!-- 可编辑字段 -->
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="modalForm.configKey" placeholder="请输入配置键" :disabled="modalMode === 'edit'" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="modalForm.configValue" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="modalForm.description" type="textarea" :rows="3" placeholder="请输入配置描述" />
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
import { reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Edit, Delete } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance } from "element-plus";
import ConfigApi, { type GetConfigDetailsVo, type GetConfigListDto, type GetConfigListVo, type AddConfigDto, type EditConfigDto } from "@/api/core/ConfigApi.ts";
import { Result } from "@/commons/entity/Result.ts";

const listForm = reactive<GetConfigListDto>({
  keyword: null,
  userName: null,
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetConfigListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 模态框相关
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add");
const modalForm = reactive<GetConfigDetailsVo>({
  id: "",
  userId: "",
  userName: "",
  configKey: "",
  configValue: "",
  description: "",
  createTime: "",
  updateTime: "",
});
const modalRules = {
  configKey: [
    { required: true, message: "请输入配置键", trigger: "blur" },
    { pattern: /^[a-zA-Z0-9_.]{2,50}$/, message: "配置键只能包含2-50位字母、数字、下划线和点", trigger: "blur" },
  ],
  configValue: [
    { required: true, message: "请输入配置值", trigger: "blur" },
    { max: 500, message: "配置值长度不能超过500个字符", trigger: "blur" },
  ],
  description: [{ max: 200, message: "描述不能超过200个字符", trigger: "blur" }],
};

const loadList = async () => {
  listLoading.value = true;
  const result = await ConfigApi.getConfigList(listForm);

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
  listForm.keyword = null;
  listForm.userName = null;
  loadList();
};

const resetModal = () => {
  modalForm.id = "";
  modalForm.configKey = "";
  modalForm.configValue = "";
  modalForm.description = "";
  modalForm.createTime = "";
  modalForm.updateTime = "";
  modalForm.userId = "";
  modalForm.userName = "";

  if (modalFormRef.value) {
    modalFormRef.value.resetFields();
  }
};

loadList();

const openModal = async (mode: "add" | "edit", row: GetConfigListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    try {
      const ret = await ConfigApi.getConfigDetails({ id: row.id });
      modalForm.id = ret.id;
      modalForm.userId = ret.userId;
      modalForm.userName = ret.userName;
      modalForm.configKey = ret.configKey;
      modalForm.configValue = ret.configValue;
      modalForm.description = ret.description;
      modalForm.createTime = ret.createTime;
      modalForm.updateTime = ret.updateTime;
    } catch (error: any) {
      ElMessage.error(error.message || "获取配置详情失败");
      return;
    }
  }

  modalVisible.value = true;
};

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
      const addDto: AddConfigDto = {
        configKey: modalForm.configKey,
        configValue: modalForm.configValue,
        description: modalForm.description,
      };
      const result = await ConfigApi.addConfig(addDto);
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
      const editDto: EditConfigDto = {
        id: modalForm.id,
        configValue: modalForm.configValue,
        description: modalForm.description,
      };
      const result = await ConfigApi.editConfig(editDto);
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
    await ElMessageBox.confirm("确定删除该配置吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await ConfigApi.removeConfig({ id });
    ElMessage.success("删除成功");
    await loadList();
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
