<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listQuery">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="中继通道名称">
              <el-input v-model="listQuery.name" placeholder="请输入中继通道名称" clearable style="width: 200px" />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="桥接目标URL">
              <el-input v-model="listQuery.forwardUrl" placeholder="请输入桥接目标URL" clearable style="width: 200px" />
            </el-form-item>
          </el-col>
          <el-col :span="8" :offset="4" style="display: flex; justify-content: flex-end">
            <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
            <el-button @click="resetList" :disabled="listLoading">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建中继通道</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id" default-expand-all>
        <el-table-column label="通道名称" prop="name" />
        <el-table-column label="主机" prop="host" />
        <el-table-column label="桥接目标" prop="forwardUrl" show-overflow-tooltip>
          <template #default="scope">
            <span v-show="scope.row.forwardType === 0"> {{ scope.row.forwardUrl }} </span>
            <span v-show="scope.row.forwardType === 1" style="color: #7354af"> 已配置路由 </span>
          </template>
        </el-table-column>
        <el-table-column label="自动运行" prop="autoStart" width="90" align="center">
          <template #default="scope">
            <span v-show="scope.row.autoStart === 1" style="color: #67c23a"> 已启用 </span>
            <span v-show="scope.row.autoStart === 0" style="color: #f56c6c"> 未启用 </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" min-width="50">
          <template #default="scope">
            <span v-show="scope.row.status === 0" style="color: #999"> 已禁用 </span>
            <span v-show="scope.row.status === 1" style="color: #e6a23c"> 未启动 </span>
            <span v-show="scope.row.status === 2" style="color: #67c23a"> 运行中 </span>
            <span v-show="scope.row.status === 3" style="color: #f56c6c"> 启动失败 </span>
            <el-button
              style="margin-left: 0"
              inline
              link
              type="primary"
              size="small"
              v-show="scope.row.status === 1 || scope.row.status === 3"
              @click="startRelayServer(scope.row)"
              :icon="CaretTopIcon"
            >
              启动
            </el-button>
            <el-button style="margin-left: 0" inline link type="primary" size="small" v-show="scope.row.status === 2" @click="stopRelayServer(scope.row)" :icon="CaretBottomIcon">
              停止
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="listQuery.pageNum"
          v-model:page-size="listQuery.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="listTotal"
          @size-change="
            (val: number) => {
              listQuery.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              listQuery.pageNum = val;
              loadList();
            }
          "
          background
        />
      </div>
    </div>

    <!-- 请求编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑中继通道' : '添加中继通道'"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="115px" :validate-on-rule-change="false">
        <el-form-item label="名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入中继通道名称" />
        </el-form-item>
        <el-form-item label="主机" prop="host">
          <el-input v-model="modalForm.host" placeholder="192.168.1.1" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input v-model="modalForm.port" placeholder="8080" type="number" />
        </el-form-item>
        <el-form-item label="桥接模式" prop="forwardType">
          <el-select v-model="modalForm.forwardType" placeholder="请选择桥接目标类型">
            <el-option label="直接" :value="0" />
            <el-option label="路由" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由规则" prop="routeRules" v-show="modalForm.forwardType === 1">
          <el-select v-model="modalForm.routeRules" placeholder="请选择路由规则" multiple value-key="routeRuleId" :collapse-tags="2">
            <el-option
              v-for="item in modalRouteRuleData"
              :key="item.id"
              :label="item.name"
              :value="{
                routeRuleId: item.id,
                routeRuleName: item.name,
                seq: 1,
              }"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="路由权重" v-show="modalForm.forwardType === 1">
          <el-table :data="modalForm.routeRules" style="width: 100%" max-height="250" border size="small">
            <el-table-column prop="routeRuleName" label="路由规则名称" width="180" />
            <el-table-column label="权重">
              <template #default="scope">
                <el-input-number v-model="scope.row.seq" :min="1" :max="100" size="small" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>

        <el-form-item label="桥接目标URL" prop="forwardUrl" v-show="modalForm.forwardType === 0">
          <el-input v-model="modalForm.forwardUrl" placeholder="https://www.baidu.com" />
        </el-form-item>
        <el-form-item label="自动运行" prop="autoStart">
          <el-switch v-model="modalForm.autoStart" :active-value="1" :inactive-value="0" style="margin-right: 10px" />
          <el-tooltip content="自动运行：如果勾选，则中继服务器会在EAS系统启动后自动启动，否则当EAS系统停机后再运行时需要手动启动这些中继服务器" placement="top">
            <div style="display: flex; align-items: center; gap: 5px; color: #999">
              <span>查看说明</span>
              <el-icon><InfoFilled /></el-icon>
            </div>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="重定向覆写" prop="overrideRedirect">
          <el-switch v-model="modalForm.overrideRedirect" :active-value="1" :inactive-value="0" style="margin-right: 10px" />
          <el-tooltip
            content="当桥接目标响应30X重定向时,桥接通道会将其跳转地址统一改为下方填写的‘重定向覆写URL’；不勾选则原样返回目标站重定向。仅对重定向响应生效，普通 200 响应不受影响。"
            placement="top"
          >
            <div style="display: flex; align-items: center; gap: 5px; color: #999">
              <span>查看说明</span>
              <el-icon><InfoFilled /></el-icon>
            </div>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="重定向覆写URL" prop="overrideRedirectUrl" v-show="modalForm.overrideRedirect === 1">
          <el-input v-model="modalForm.overrideRedirectUrl" placeholder="例如：https://example.com/after-login 或 https://example.com/home" />
        </el-form-item>
        <el-form-item label="请求ID策略" prop="requestIdStrategy">
          <el-switch v-model="modalForm.requestIdStrategy" :active-value="1" :inactive-value="0" style="margin-right: 10px" />
          <el-tooltip content="请求ID策略：0-随机生成，1-从请求头获取。勾选表示从请求头获取，需要指定请求头名称" placement="top">
            <div style="display: flex; align-items: center; gap: 5px; color: #999">
              <span>查看说明</span>
              <el-icon><InfoFilled /></el-icon>
            </div>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="请求ID头名称" prop="requestIdHeaderName" v-show="modalForm.requestIdStrategy === 1">
          <el-input v-model="modalForm.requestIdHeaderName" placeholder="例如：X-Request-ID 或 Request-ID" />
        </el-form-item>
        <el-form-item label="业务错误策略" prop="bizErrorStrategy">
          <el-switch v-model="modalForm.bizErrorStrategy" :active-value="1" :inactive-value="0" style="margin-right: 10px" />
          <el-tooltip content="业务错误策略：0-由HTTP状态码决定，1-由业务错误码决定。勾选表示使用业务错误码判断" placement="top">
            <div style="display: flex; align-items: center; gap: 5px; color: #999">
              <span>查看说明</span>
              <el-icon><InfoFilled /></el-icon>
            </div>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="业务错误码字段" prop="bizErrorCodeField" v-show="modalForm.bizErrorStrategy === 1">
          <el-input v-model="modalForm.bizErrorCodeField" placeholder="例如：$.code 或 $.result.errorCode" />
        </el-form-item>
        <el-form-item label="业务成功码值" prop="bizSuccessCodeValue" v-show="modalForm.bizErrorStrategy === 1">
          <el-input v-model="modalForm.bizSuccessCodeValue" placeholder="例如：0 或 success" />
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
import { reactive, ref, onMounted, watch } from "vue";
import RelayServerApi, { type GetRelayServerListDto } from "@/api/relay/RelayServerApi.ts";
import type { GetRelayServerListVo, GetRelayServerDetailsVo, RelayServerRouteRuleDto, RelayServerRouteRuleVo } from "@/api/relay/RelayServerApi.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import { Edit, DocumentCopy, View, Delete, InfoFilled, CaretTop, CaretBottom } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance } from "element-plus";
import type { GetRouteRuleListVo } from "@/api/relay/RouteRuleApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import RouteRuleApi from "@/api/relay/RouteRuleApi.ts";

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const ViewIcon = markRaw(View);
const DeleteIcon = markRaw(Delete);
const CaretTopIcon = markRaw(CaretTop);
const CaretBottomIcon = markRaw(CaretBottom);

//列表内容
const listQuery = reactive<GetRelayServerListDto>({
  name: null,
  forwardUrl: null,
  pageNum: 1,
  pageSize: 10,
});

//列表
const listData = ref<GetRelayServerListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const loadList = async () => {
  listLoading.value = true;
  try {
    const res = await RelayServerApi.getRelayServerList(listQuery);
    listData.value = res.data;
    listTotal.value = res.total;
    console.log(res);
  } catch (e) {
    ElMessage.error("加载中继通道列表失败");
  } finally {
    listLoading.value = false;
  }
};

const resetList = () => {
  listQuery.name = null;
  listQuery.forwardUrl = null;
  listQuery.pageNum = 1;
  listQuery.pageSize = 10;
  loadList();
};

const removeList = async (row: GetRelayServerListVo) => {
  //无法删除一个正在运行中的中继通道
  if (row.status === 2) {
    ElMessage.error("无法删除一个正在运行中的中继通道");
    return;
  }

  try {
    await ElMessageBox.confirm("确定删除中继通道 [" + row.name + "] 吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await RelayServerApi.removeRelayServer(row.id.toString());
    ElMessage.success("删除中继通道成功");
    loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

loadList();

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"view" | "edit" | "add">("view"); //view:预览,edit:编辑,add:添加
const modalRouteRuleData = ref<GetRouteRuleListVo[]>([]); //路由规则数据
const modalForm = reactive<GetRelayServerDetailsVo>({
  id: null,
  name: null,
  host: null,
  port: null,
  forwardType: 0, //桥接目标类型 0:直接 1:路由
  routeRules: [] as RelayServerRouteRuleVo[],
  forwardUrl: null,
  autoStart: null,
  status: null,
  errorMessage: null,
  overrideRedirect: null,
  overrideRedirectUrl: null,
  requestIdStrategy: null,
  requestIdHeaderName: null,
  bizErrorStrategy: null,
  bizErrorCodeField: null,
  bizSuccessCodeValue: null,
  createTime: null,
});

// 表单校验规则
const modalRules = {
  name: [{ required: true, message: "请输入中继通道名称", trigger: "blur" }],
  host: [
    { required: true, message: "请输入中继服务器主机", trigger: "blur" },
    { pattern: /^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$/, message: "主机名必须为有效IP地址", trigger: "blur" },
  ],
  port: [
    { required: true, message: "请输入中继服务器端口", trigger: "blur" },
    { pattern: /^[0-9]{1,5}$/, message: "端口必须为1-65535之间的整数", trigger: "blur" },
  ],
  forwardType: [{ required: true, message: "请选择桥接目标类型", trigger: "blur" }],
  routeRules: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (modalForm.forwardType === 1 && value.length === 0) {
          callback(new Error("请选择路由规则"));
          return;
        }
        callback();
      },
      trigger: "change",
    },
  ],
  forwardUrl: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (modalForm.forwardType === 0) {
          if (!value) {
            callback(new Error("请输入桥接目标URL"));
            return;
          }
          if (!/^https?:\/\/[^\s/$.?#].[^\s]*$/i.test(value)) {
            callback(new Error("桥接目标URL必须为有效URL"));
            return;
          }
          callback();
          return;
        }
        if (modalForm.forwardType !== 0) {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  autoStart: [{ required: true, message: "请选择是否自动运行", trigger: "blur" }],
  requestIdStrategy: [{ required: true, message: "请选择请求ID策略", trigger: "blur" }],
  requestIdHeaderName: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (modalForm.requestIdStrategy === 1 && !value) {
          callback(new Error("当请求ID策略为从请求头获取时，请求ID头名称不能为空"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  bizErrorStrategy: [{ required: true, message: "请选择业务错误策略", trigger: "blur" }],
  bizErrorCodeField: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (modalForm.bizErrorStrategy === 1 && !value) {
          callback(new Error("当业务错误策略为业务错误码决定时，业务错误码字段不能为空"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  bizSuccessCodeValue: [
    {
      validator: (rule: any, value: any, callback: any) => {
        if (modalForm.bizErrorStrategy === 1 && !value) {
          callback(new Error("当业务错误策略为业务错误码决定时，业务错误码值不能为空"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

const openModal = async (mode: "add" | "edit", row: GetRelayServerListVo | null) => {
  modalMode.value = mode;
  resetModal();

  if (mode === "edit" && row) {
    if (row.status === 2) {
      ElMessage.error("无法修改一个正在运行中的中继通道");
      return;
    }

    try {
      //获取请求数据
      const res = await RelayServerApi.getRelayServerDetails(row.id.toString());
      modalForm.id = res.id;
      modalForm.name = res.name;
      modalForm.host = res.host;
      modalForm.port = res.port;
      modalForm.forwardType = res.forwardType;
      modalForm.routeRules = res.routeRules;
      modalForm.forwardUrl = res.forwardUrl;
      modalForm.autoStart = res.autoStart;
      modalForm.status = res.status;
      modalForm.errorMessage = res.errorMessage;
      modalForm.overrideRedirect = res.overrideRedirect;
      modalForm.overrideRedirectUrl = res.overrideRedirectUrl;
      modalForm.requestIdStrategy = res.requestIdStrategy;
      modalForm.requestIdHeaderName = res.requestIdHeaderName;
      modalForm.bizErrorStrategy = res.bizErrorStrategy;
      modalForm.bizErrorCodeField = res.bizErrorCodeField;
      modalForm.bizSuccessCodeValue = res.bizSuccessCodeValue;
      modalForm.createTime = res.createTime;
      modalVisible.value = true;
    } catch (error: any) {
      ElMessage.error(error.message);
      console.error("获取请求详情失败", error);
    }
  }

  //加载路由规则数据
  const res = await RouteRuleApi.getRouteRuleList({
    pageNum: 1,
    pageSize: 100000,
  });

  if (Result.isSuccess(res)) {
    modalRouteRuleData.value = res.data;
  }

  modalVisible.value = true;
};

const resetModal = () => {
  modalForm.id = null;
  modalForm.name = null;
  modalForm.host = "0.0.0.0";
  modalForm.port = 8080;
  modalForm.forwardType = 0;
  modalForm.routeRules = [];
  modalForm.forwardUrl = null;
  modalForm.autoStart = 1;
  modalForm.status = null;
  modalForm.errorMessage = null;
  modalForm.overrideRedirect = 0;
  modalForm.overrideRedirectUrl = null;
  modalForm.requestIdStrategy = 0;
  modalForm.requestIdHeaderName = null;
  modalForm.bizErrorStrategy = null;
  modalForm.bizErrorCodeField = null;
  modalForm.bizSuccessCodeValue = null;
  modalForm.createTime = null;
};

const submitModal = async () => {
  //验证表单
  try {
    await modalFormRef.value?.validate();
  } catch (error) {
    return;
  }

  try {
    if (modalMode.value === "add") {
      await RelayServerApi.addRelayServer({
        name: modalForm.name,
        host: modalForm.host,
        port: modalForm.port,
        forwardType: modalForm.forwardType,
        routeRules: modalForm.routeRules,
        forwardUrl: modalForm.forwardUrl,
        autoStart: modalForm.autoStart,
        overrideRedirect: modalForm.overrideRedirect,
        overrideRedirectUrl: modalForm.overrideRedirectUrl,
        requestIdStrategy: modalForm.requestIdStrategy,
        requestIdHeaderName: modalForm.requestIdHeaderName,
        bizErrorStrategy: modalForm.bizErrorStrategy,
        bizErrorCodeField: modalForm.bizErrorCodeField,
        bizSuccessCodeValue: modalForm.bizSuccessCodeValue,
      });
      ElMessage.success("添加中继通道成功");
      //新增需要重置表单
      resetModal();
    }

    if (modalMode.value === "edit") {
      await RelayServerApi.editRelayServer({
        id: modalForm.id,
        name: modalForm.name,
        host: modalForm.host,
        port: modalForm.port,
        forwardType: modalForm.forwardType,
        routeRules: modalForm.routeRules,
        forwardUrl: modalForm.forwardUrl,
        autoStart: modalForm.autoStart,
        overrideRedirect: modalForm.overrideRedirect,
        overrideRedirectUrl: modalForm.overrideRedirectUrl,
        requestIdStrategy: modalForm.requestIdStrategy,
        requestIdHeaderName: modalForm.requestIdHeaderName,
        bizErrorStrategy: modalForm.bizErrorStrategy,
        bizErrorCodeField: modalForm.bizErrorCodeField,
        bizSuccessCodeValue: modalForm.bizSuccessCodeValue,
      });
      ElMessage.success("编辑中继通道成功");
    }

    //modalVisible.value = false;

    loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
    console.error("操作中继通道失败", error);
  }
};

const startRelayServer = async (row: GetRelayServerListVo) => {
  try {
    await RelayServerApi.startRelayServer(row.id.toString());
    ElMessage.success("启动中继通道成功");
    loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const stopRelayServer = async (row: GetRelayServerListVo) => {
  try {
    await RelayServerApi.stopRelayServer(row.id.toString());
    ElMessage.success("停止中继通道成功");
    loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

watch(
  () => modalForm.forwardType,
  (newVal: number | null) => {
    //如果桥接目标类型为直接，则清空路由规则
    if (newVal === 0) {
      modalForm.routeRules = [];
    }

    //如果桥接目标类型为路由，则清空桥接目标URL
    if (newVal === 1) {
      modalForm.forwardUrl = null;
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

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
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

.copy-icon {
  cursor: pointer;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
