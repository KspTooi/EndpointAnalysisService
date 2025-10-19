<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item>
          <el-form-item label="路由规则名" prop="name">
            <el-input v-model="query.name" placeholder="请输入路由规则名" />
          </el-form-item>
          <el-form-item label="匹配类型" prop="matchType">
            <el-select v-model="query.matchType" placeholder="请选择匹配类型" style="width: 200px">
              <el-option label="全部" :value="0" />
              <el-option label="IP地址" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="匹配值" prop="matchValue">
            <el-input v-model="query.matchValue" placeholder="请输入匹配值" />
          </el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetList">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openModal('add', null)">创建路由规则</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column label="路由规则名" prop="name" width="300" show-overflow-tooltip />
        <el-table-column label="目标服务器" prop="routeServerId" width="100" show-overflow-tooltip />
        <el-table-column label="匹配类型" prop="matchType" width="90" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.matchType === 0 ? 'success' : 'danger'">{{ scope.row.matchType === 0 ? "全部" : "IP地址" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="匹配键" prop="matchKey" width="100" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.matchKey === null">--</span>
            <span v-else>{{ scope.row.matchKey }}</span>
          </template>
        </el-table-column>
        <el-table-column label="匹配操作" prop="matchOperator" width="100" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.matchOperator === null">--</span>
            <span v-else>{{ scope.row.matchOperator === 0 ? "等于" : "不等于" }}</span>
          </template>
        </el-table-column>
        <el-table-column label="匹配值" prop="matchValue" width="100" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.matchValue === null">--</span>
            <span v-else>{{ scope.row.matchValue }}</span>
          </template>
        </el-table-column>
        <el-table-column label="权重" prop="seq" width="100" show-overflow-tooltip />
        <el-table-column label="策略描述" prop="remark" show-overflow-tooltip />
        <el-table-column label="更新时间" prop="updateTime" width="100" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="200">
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

    <!-- 路由规则编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑路由规则' : '添加路由规则'"
      width="550px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="95px" :validate-on-rule-change="false">
        <el-form-item label="路由规则名" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入路由规则名" />
        </el-form-item>
        <el-form-item label="匹配类型" prop="matchType">
          <el-select v-model="modalForm.matchType" placeholder="请选择匹配类型">
            <el-option label="全部" :value="0" />
            <el-option label="IP地址" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配键" prop="matchKey" v-if="modalForm.matchType == 2">
          <el-input v-model="modalForm.matchKey" placeholder="请输入匹配键" />
        </el-form-item>
        <el-form-item label="匹配操作" prop="matchOperator" v-if="modalForm.matchType != 0">
          <el-select v-model="modalForm.matchOperator" placeholder="请选择匹配操作">
            <el-option label="等于" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="匹配值" prop="matchValue" v-if="modalForm.matchType != 0">
          <el-input v-model="modalForm.matchValue" placeholder="请输入匹配值" />
        </el-form-item>
        <el-form-item label="目标服务器" prop="routeServerId">
          <el-select v-model="modalForm.routeServerId" placeholder="请选择目标服务器" clearable filterable>
            <el-option v-for="item in routeServerList" :key="item.id" :label="item.name" :value="item.id">
              <span :style="{ color: item.status === 0 ? 'var(--el-color-danger)' : '' }">{{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权重" prop="seq">
          <el-input v-model.number="modalForm.seq" placeholder="请输入权重" type="number" :min="0" :max="100000" />
        </el-form-item>
        <el-form-item label="策略描述" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="请输入策略描述" />
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
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, watch, computed } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, Plus as PlusIcon } from "@element-plus/icons-vue";
import RouteRuleApi, { type GetRouteRuleDetailsVo, type GetRouteRuleListDto, type GetRouteRuleListVo } from "@/api/route/RouteRuleApi.ts";
import type { GetRouteServerListVo } from "@/api/route/RouteServerApi";
import RouteServerApi from "@/api/route/RouteServerApi";

//列表内容
const query = reactive<GetRouteRuleListDto>({
  name: null,
  matchType: null,
  matchValue: null,
  pageNum: 1,
  pageSize: 10,
});

const list = ref<GetRouteRuleListVo[]>([]);
const total = ref(0);
const loading = ref(false);

const loadList = async () => {
  loading.value = true;
  const result = await RouteRuleApi.getRouteRuleList(query);

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
  query.name = null;
  query.matchType = null;
  query.matchValue = null;
  query.pageNum = 1;
  query.pageSize = 10;
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该路由规则吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await RouteRuleApi.removeRouteRule({ id });
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
const modalForm = reactive<GetRouteRuleDetailsVo>({
  id: "",
  name: "",
  matchType: 0,
  matchKey: null,
  matchOperator: null,
  matchValue: null,
  routeServerId: "",
  seq: 1,
  remark: "",
  updateTime: "",
});

const modalRules = {
  name: [
    { required: true, message: "请输入路由规则名", trigger: "blur" },
    { max: 32, message: "路由规则名长度不能超过32个字符", trigger: "blur" },
  ],
  matchType: [{ required: true, message: "请选择匹配类型", trigger: "change" }],
  matchKey: [
    { required: true, message: "请输入匹配键", trigger: "blur" },
    { max: 255, message: "匹配键长度不能超过255个字符", trigger: "blur" },
  ],
  matchOperator: [{ required: true, message: "请选择匹配操作", trigger: "change" }],
  matchValue: [
    { required: true, message: "请输入匹配值", trigger: "blur" },
    { max: 255, message: "匹配值长度不能超过255个字符", trigger: "blur" },
  ],
  routeServerId: [{ required: true, message: "请选择目标服务器", trigger: "change" }],
  seq: [
    { required: true, message: "请输入权重", trigger: "blur" },
    { type: "number", min: 0, max: 100000, message: "权重必须在0-100000之间", trigger: "blur" },
  ],
  remark: [
    { required: false, message: "请输入策略描述", trigger: "blur" },
    { max: 5000, message: "策略描述长度不能超过5000个字符", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit", row: GetRouteRuleListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //加载目标服务器列表
  await loadRouteServerList();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    const ret = await RouteRuleApi.getRouteRuleDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.name = ret.data.name;
      modalForm.matchType = ret.data.matchType;
      modalForm.matchKey = ret.data.matchKey;
      modalForm.matchOperator = ret.data.matchOperator;
      modalForm.matchValue = ret.data.matchValue;
      modalForm.routeServerId = ret.data.routeServerId;
      modalForm.seq = ret.data.seq;
      modalForm.remark = ret.data.remark;
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
  modalForm.matchType = 0;
  modalForm.matchKey = null;
  modalForm.matchOperator = null;
  modalForm.matchValue = null;
  modalForm.routeServerId = "";
  modalForm.seq = 1;
  modalForm.remark = "";
  modalForm.updateTime = "";
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
      await RouteRuleApi.addRouteRule(modalForm);
      ElMessage.success("新增成功");
      resetModal();
    }

    if (modalMode.value === "edit") {
      await RouteRuleApi.editRouteRule(modalForm);
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

//目标服务器列表
const routeServerList = ref<GetRouteServerListVo[]>([]);

const loadRouteServerList = async () => {
  const result = await RouteServerApi.getRouteServerList({
    pageNum: 1,
    pageSize: 100000,
  });
  if (Result.isSuccess(result)) {
    routeServerList.value = result.data;
  }
};

watch(
  () => modalForm.matchType,
  (newVal) => {
    //如果匹配类型为全部 则清空
    if (newVal === 0) {
      modalForm.matchKey = null;
      modalForm.matchOperator = null;
      modalForm.matchValue = null;
    }
  }
);
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

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
