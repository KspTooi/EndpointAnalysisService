<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item label="组名称" label-for="query-name">
          <el-input v-model="query.name" placeholder="输入组名称查询" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="备注" label-for="query-remark">
          <el-input v-model="query.remark" placeholder="输入备注查询" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="负载均衡策略" label-for="query-loadBalance">
          <el-select v-model="query.loadBalance" placeholder="选择负载均衡策略" clearable style="width: 200px">
            <el-option label="轮询" value="0" />
            <el-option label="随机" value="1" />
            <el-option label="权重" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="自动降级" label-for="query-autoDegradation">
          <el-select v-model="query.autoDegradation" placeholder="选择自动降级" clearable style="width: 200px">
            <el-option label="开启" value="0" />
            <el-option label="关闭" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetList">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openModal('add', null)">创建路由策略组</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column label="组名称" prop="name" width="300" show-overflow-tooltip />
        <el-table-column label="负载均衡" prop="loadBalance" width="90" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.loadBalance === 0 ? 'success' : 'danger'">{{ scope.row.loadBalance === 0 ? "轮询" : scope.row.loadBalance === 1 ? "随机" : "权重" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="自动降级" prop="autoDegradation" width="100" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.autoDegradation === 0 ? 'success' : 'danger'">{{ scope.row.autoDegradation === 0 ? "开启" : "关闭" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
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

    <!-- 菜单编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑路由策略组' : '添加路由策略组'"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="80px" :validate-on-rule-change="false">
        <el-form-item label="组名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="输入组名称" />
        </el-form-item>
        <el-form-item label="负载均衡" prop="loadBalance">
          <el-select v-model="modalForm.loadBalance" placeholder="选择负载均衡策略" clearable>
            <el-option label="轮询" :value="0" />
            <el-option label="随机" :value="1" />
            <el-option label="权重" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="自动降级" prop="autoDegradation">
          <el-select v-model="modalForm.autoDegradation" placeholder="选择自动降级" clearable>
            <el-option label="开启" :value="0" />
            <el-option label="关闭" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="输入备注" />
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
import type { GetRouteGroupDetailsVo, GetRouteGroupListDto, GetRouteGroupListVo } from "@/api/route/RouteGroupApi";
import RouteGroupApi from "@/api/route/RouteGroupApi";

//列表内容
const query = reactive<GetRouteGroupListDto>({
  name: null,
  remark: null,
  loadBalance: null,
  autoDegradation: null,
  pageNum: 1,
  pageSize: 10,
});

const list = ref<GetRouteGroupListVo[]>([]);
const total = ref(0);
const loading = ref(false);

const loadList = async () => {
  loading.value = true;
  const result = await RouteGroupApi.getRouteGroupList(query);

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
  query.remark = null;
  query.loadBalance = null;
  query.autoDegradation = null;
  query.pageNum = 1;
  query.pageSize = 10;
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该路由策略组吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await RouteGroupApi.removeRouteGroup({ id });
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
const modalForm = reactive<GetRouteGroupDetailsVo>({
  id: null,
  name: "",
  remark: null,
  loadBalance: 0,
  autoDegradation: 0,
});

const modalRules = {
  name: [
    { required: true, message: "请输入组名称", trigger: "blur" },
    { max: 32, message: "组名称长度不能超过32个字符", trigger: "blur" },
  ],
  remark: [{ max: 5000, message: "备注长度不能超过5000个字符", trigger: "blur" }],
  loadBalance: [
    { required: true, message: "请选择负载均衡策略", trigger: "blur" },
    { type: "number", min: 0, max: 2, message: "负载均衡策略只能在0-2之间", trigger: "blur" },
  ],
  autoDegradation: [
    { required: true, message: "请选择自动降级", trigger: "blur" },
    { type: "number", min: 0, max: 1, message: "自动降级只能在0-1之间", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit", row: GetRouteGroupListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    const ret = await RouteGroupApi.getRouteGroupDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.name = ret.data.name;
      modalForm.remark = ret.data.remark;
      modalForm.loadBalance = ret.data.loadBalance;
      modalForm.autoDegradation = ret.data.autoDegradation;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  }

  modalVisible.value = true;
};

const resetModal = () => {
  modalForm.id = null;
  modalForm.name = "";
  modalForm.remark = null;
  modalForm.loadBalance = 0;
  modalForm.autoDegradation = 0;
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
      await RouteGroupApi.addRouteGroup(modalForm);
      ElMessage.success("新增成功");
      resetModal();
    }

    if (modalMode.value === "edit") {
      await RouteGroupApi.editRouteGroup(modalForm);
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
