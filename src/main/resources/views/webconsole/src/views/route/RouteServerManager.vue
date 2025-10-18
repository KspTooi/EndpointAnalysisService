<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item label="服务器名称">
          <el-input v-model="query.name" placeholder="请输入服务器名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="服务器主机">
          <el-input v-model="query.host" placeholder="请输入服务器主机" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="服务器端口">
          <el-input v-model="query.port" placeholder="请输入服务器端口" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="query.remark" placeholder="请输入备注" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="服务器状态">
          <el-select v-model="query.status" placeholder="请选择服务器状态" clearable style="width: 200px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetList">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openModal('add', null)">创建服务器</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
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
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="
            (val: number) => {
              query.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              query.pageNum = val;
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
import type { GetMenuDetailsVo, GetMenuTreeDto, GetMenuTreeVo } from "@/api/core/MenuApi";
import MenuApi from "@/api/core/MenuApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, watch, computed } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, Plus as PlusIcon } from "@element-plus/icons-vue";
import IconPicker from "@/components/common/IconPicker.vue";
import { Icon } from "@iconify/vue";
import { EventHolder } from "@/store/EventHolder";
import type { GetRouteServerDetailsVo, GetRouteServerListDto, GetRouteServerListVo } from "@/api/route/RouteServerApi";
import RouteServerApi from "@/api/route/RouteServerApi";

//列表内容
const query = reactive<GetRouteServerListDto>({
  name: "",
  host: "",
  port: null,
  remark: "",
  status: null,
  pageNum: 1,
  pageSize: 10,
});

const list = ref<GetRouteServerListVo[]>([]);
const total = ref(0);
const loading = ref(false);

const loadList = async () => {
  loading.value = true;
  const result = await RouteServerApi.getRouteServerList(query);

  if (Result.isSuccess(result)) {
    list.value = result.data;
    total.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  loading.value = false;
};

const resetList = () => {
  query.pageNum = 1;
  query.pageSize = 10;
  query.name = null;
  query.host = null;
  query.port = null;
  query.remark = null;
  query.status = null;
  loadList();
};

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
  loadList();
};

loadList();

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add"); //add:添加,edit:编辑
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

const openModal = async (mode: "add" | "edit", row: GetRouteServerListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
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

const resetModal = () => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.host = "";
  modalForm.port = 0;
  modalForm.remark = "";
  modalForm.status = 1;
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

  //modalVisible.value = false;
  loadList();
};
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.query-form {
  margin-bottom: 20px;
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
</style>
