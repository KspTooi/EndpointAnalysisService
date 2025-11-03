<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="服务器名称">
              <el-input v-model="listForm.name" placeholder="请输入服务器名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="服务器主机">
              <el-input v-model="listForm.host" placeholder="请输入服务器主机" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="服务器端口">
              <el-input v-model="listForm.port" placeholder="请输入服务器端口" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetList" :disabled="listLoading">重置</el-button>
              <ExpandButton v-model="uiState.isAdvancedSearch" :disabled="listLoading" />
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="uiState.isAdvancedSearch">
          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="备注">
                <el-input v-model="listForm.remark" placeholder="请输入备注" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="服务器状态">
                <el-select v-model="listForm.status" placeholder="请选择服务器状态" clearable style="width: 100%">
                  <el-option label="启用" :value="1" />
                  <el-option label="禁用" :value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1"> </el-col>
          </el-row>
        </template>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建服务器</el-button>
      <el-button type="danger" @click="removeListBatch" :disabled="listSelected.length === 0" :loading="listLoading">删除选中项</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id" default-expand-all @selection-change="(val: GetRouteServerListVo[]) => (listSelected = val)">
        <el-table-column type="selection" width="40" />
        <el-table-column label="服务器名称" prop="name" show-overflow-tooltip />
        <el-table-column label="服务器主机" prop="host" show-overflow-tooltip />
        <el-table-column label="服务器端口" prop="port" show-overflow-tooltip />
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
        <el-table-column label="服务器状态" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? "启用" : "禁用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updateTime" width="180" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon" :disabled="scope.row.children && scope.row.children.length > 0">
              删除
            </el-button>
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

    <!-- 菜单编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑服务器' : '添加服务器'"
      width="550px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="95px" :validate-on-rule-change="false">
        <el-form-item label="服务器名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入服务器名称" />
        </el-form-item>
        <el-form-item label="服务器主机" prop="host">
          <el-input v-model="modalForm.host" placeholder="请输入服务器主机" />
        </el-form-item>
        <el-form-item label="服务器端口" prop="port">
          <el-input v-model.number="modalForm.port" placeholder="请输入服务器端口" type="number" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="请输入备注" />
        </el-form-item>
        <el-form-item label="服务器状态" prop="status">
          <el-select v-model="modalForm.status" placeholder="请选择服务器状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
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
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, onMounted, markRaw } from "vue";
import { Delete, View } from "@element-plus/icons-vue";
import type { GetRouteServerDetailsVo, GetRouteServerListDto, GetRouteServerListVo } from "@/api/relay/RouteServerApi.ts";
import RouteServerApi from "@/api/relay/RouteServerApi.ts";
import ExpandButton from "@/components/common/ExpandButton.vue";

// 图标常量
const ViewIcon = markRaw(View);
const DeleteIcon = markRaw(Delete);

// 列表相关变量
const listForm = reactive<GetRouteServerListDto>({
  name: "",
  host: "",
  port: null,
  remark: "",
  status: null,
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetRouteServerListVo[]>([]);
const listSelected = ref<GetRouteServerListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

// UI状态变量
const uiState = reactive({
  isAdvancedSearch: false,
});

// 模态框相关变量
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add");
const modalForm = reactive<GetRouteServerDetailsVo>({
  id: "",
  name: "",
  host: "",
  port: 0,
  remark: "",
  status: 0,
  createTime: "",
  updateTime: "",
});

// 表单校验规则
const modalRules = {
  name: [
    { required: true, message: "请输入服务器名称", trigger: "blur" },
    { max: 32, message: "服务器名称长度不能超过32个字符", trigger: "blur" },
  ],
  host: [
    { required: true, message: "请输入服务器主机", trigger: "blur" },
    { max: 32, message: "服务器主机长度不能超过32个字符", trigger: "blur" },
  ],
  remark: [{ max: 5000, message: "备注长度不能超过5000个字符", trigger: "blur" }],
  port: [
    { required: true, message: "请输入服务器端口", trigger: "blur" },
    { type: "number", min: 1, max: 65535, message: "端口号必须在1-65535之间", trigger: "blur" },
  ],
  status: [
    { required: true, message: "请选择服务器状态", trigger: "blur" },
    { type: "number", min: 0, max: 1, message: "服务器状态只能在0或1之间", trigger: "blur" },
  ],
};

// 加载列表
const loadList = async () => {
  listLoading.value = true;
  const result = await RouteServerApi.getRouteServerList(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    listTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

// 重置查询条件
const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.name = null;
  listForm.host = null;
  listForm.port = null;
  listForm.remark = null;
  listForm.status = null;
  loadList();
};

// 删除单条记录
const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该服务器吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await RouteServerApi.removeRouteServer({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
      return;
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  }
  await loadList();
};

// 批量删除
const removeListBatch = async () => {
  try {
    await ElMessageBox.confirm("确定删除该服务器吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await RouteServerApi.removeRouteServer({ ids: listSelected.value.map((item) => item.id) });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
      return;
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  }
  await loadList();
};

// 打开模态框
const openModal = async (mode: "add" | "edit", row: GetRouteServerListVo | null) => {
  modalMode.value = mode;
  resetModal();

  if (mode === "edit" && row) {
    const ret = await RouteServerApi.getRouteServerDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.name = ret.data.name;
      modalForm.host = ret.data.host;
      modalForm.port = ret.data.port;
      modalForm.remark = ret.data.remark;
      modalForm.status = ret.data.status;
      modalForm.createTime = ret.data.createTime;
      modalForm.updateTime = ret.data.updateTime;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  }

  modalVisible.value = true;
};

// 重置模态框表单
const resetModal = () => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.host = "";
  modalForm.port = 0;
  modalForm.remark = "";
  modalForm.status = 1;
};

// 提交模态框表单
const submitModal = async () => {
  try {
    await modalFormRef?.value?.validate();
  } catch (error) {
    return;
  }

  modalLoading.value = true;

  try {
    if (modalMode.value === "add") {
      await RouteServerApi.addRouteServer(modalForm);
      ElMessage.success("操作成功");
      resetModal();
    }

    if (modalMode.value === "edit") {
      await RouteServerApi.editRouteServer(modalForm);
      ElMessage.success("操作成功");
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  } finally {
    modalLoading.value = false;
  }

  await loadList();
};

// 生命周期
onMounted(() => {
  loadList();
});
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
