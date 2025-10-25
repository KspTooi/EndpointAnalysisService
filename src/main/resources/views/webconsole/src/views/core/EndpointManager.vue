<template>
  <div class="endpoint-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="端点名称">
              <el-input v-model="query.name" placeholder="请输入端点名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="端点路径">
              <el-input v-model="query.path" placeholder="请输入端点路径" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1"> </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="loading">查询</el-button>
              <el-button @click="resetList" :disabled="loading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">创建端点</el-button>
      <el-button type="success" @click="clearEndpointCache">清空权限数据缓存</el-button>
    </div>

    <!-- 列表 -->
    <div class="endpoint-tree-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column label="端点名称" prop="name" />
        <el-table-column label="端点路径" prop="path" show-overflow-tooltip />
        <el-table-column label="所需权限" prop="permission" show-overflow-tooltip />
        <el-table-column label="端点描述" prop="description" show-overflow-tooltip />
        <el-table-column label="排序" prop="seq" width="100" />
        <el-table-column label="已缓存" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.cached === 1 ? 'success' : 'info'">
              {{ scope.row.cached === 1 ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="scope">
            <div style="display: inline-flex; justify-content: flex-end; align-items: center; gap: 8px; width: 100%">
              <el-button link type="success" size="small" @click="openModal('add-item', scope.row)" :icon="PlusIcon"> 新增子端点 </el-button>
              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
              <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon" :disabled="scope.row.children && scope.row.children.length > 0">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 端点编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑端点' : '添加端点'"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal(true);
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="100px" :validate-on-rule-change="false">
        <el-form-item label="父级端点" prop="parentId">
          <el-tree-select
            v-model="modalForm.parentId"
            :data="endpointTreeForSelect"
            node-key="id"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="请选择父级端点"
            clearable
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="端点名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入端点名称" clearable />
        </el-form-item>
        <el-form-item prop="path">
          <template #label>
            <span>
              <el-tooltip
                class="box-item"
                effect="dark"
                content="支持AntPathMatcher路径匹配。? 匹配一个字符, * 匹配零个或多个字符, ** 匹配路径中的零个或多个目录。例如: /user/**"
                placement="top"
              >
                <el-icon style="vertical-align: middle; margin-left: 4px"><InfoFilled /></el-icon> </el-tooltip
              >端点路径
            </span>
          </template>
          <el-input v-model="modalForm.path" placeholder="请输入端点路径" clearable />
        </el-form-item>
        <el-form-item label="所需权限" prop="permission">
          <el-input v-model="modalForm.permission" placeholder="请输入所需权限" clearable />
        </el-form-item>
        <el-form-item label="端点描述" prop="description">
          <el-input v-model="modalForm.description" placeholder="请输入端点描述" clearable />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model.number="modalForm.seq" :min="0" :max="655350" placeholder="请输入排序" clearable />
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
import type { GetEndpointDetailsVo, GetEndpointTreeDto, GetEndpointTreeVo } from "@/api/core/EndpointApi";
import EndpointApi from "@/api/core/EndpointApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, computed } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, Plus as PlusIcon, InfoFilled } from "@element-plus/icons-vue";

//列表内容
const query = reactive<GetEndpointTreeDto>({
  name: "",
  path: "",
});

const list = ref<GetEndpointTreeVo[]>([]);
const loading = ref(false);

// 用于父级选择的完整端点树
const fullEndpointTree = ref<GetEndpointTreeVo[]>([]);

const endpointTreeForSelect = computed(() => {
  const currentEndpoint = modalForm;
  const isEditMode = modalMode.value === "edit";

  const filter = (endpointTree: GetEndpointTreeVo[]): GetEndpointTreeVo[] => {
    return endpointTree.map((item) => {
      let disabled = false;

      // 编辑时，节点自身不能作为父级
      if (isEditMode && item.id === currentEndpoint.id) {
        disabled = true;
      }

      return {
        ...item,
        disabled,
        children: item.children ? filter(item.children) : [],
      };
    });
  };

  return [
    {
      id: null,
      name: "根节点",
      disabled: false,
      children: filter(fullEndpointTree.value),
    },
  ];
});

// 加载完整端点树用于父级选择
const loadFullEndpointTree = async () => {
  const result = await EndpointApi.getEndpointTree({});
  if (Result.isSuccess(result)) {
    fullEndpointTree.value = result.data;
  }
};

const loadList = async () => {
  loading.value = true;
  const result = await EndpointApi.getEndpointTree(query);

  if (Result.isSuccess(result)) {
    list.value = result.data;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  loading.value = false;
};

const resetList = () => {
  query.name = "";
  query.path = "";
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该端点吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await EndpointApi.removeEndpoint({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
      loadList();
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const clearEndpointCache = async () => {
  try {
    await ElMessageBox.confirm(
      "<div style='text-align: left;'>" +
        "<div style='margin-bottom: 12px;'><strong>清空缓存会发生什么？</strong></div>" +
        "<div style='margin-bottom: 8px;'>1. 如果你刚修改了端点配置，清空缓存后修改会马上生效</div>" +
        "<div style='margin-bottom: 16px;'>2. 系统会立即按最新的端点配置来判断用户能否访问某个接口</div>" +
        "<div style='margin-bottom: 12px;'><strong>什么时候需要清空缓存？</strong></div>" +
        "<div style='margin-bottom: 6px;'>✓ 修改了端点的路径或权限，希望立即生效</div>" +
        "<div style='margin-bottom: 6px;'>✓ 发现权限验证不正常，需要排查问题</div>" +
        "<div style='margin-bottom: 16px;'>✓ 怀疑缓存数据和数据库不一致</div>" +
        "<div style='margin-top: 16px; color: #E6A23C;'><strong>⚠️ 确定要清空权限缓存吗？</strong></div>" +
        "</div>",
      "⚠️ 清空权限数据缓存 ",
      {
        confirmButtonText: "确认清空",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        autofocus: false,
      }
    );
  } catch (error) {
    return;
  }

  const result = await EndpointApi.clearEndpointCache();
  if (Result.isSuccess(result)) {
    ElMessage.success("权限数据缓存已清空");
    loadList();
  }
};

loadList();
loadFullEndpointTree();

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
const modalCurrentRow = ref<GetEndpointTreeVo | null>(null);
const modalForm = reactive<GetEndpointDetailsVo>({
  id: "",
  parentId: "",
  name: "",
  description: "",
  path: "",
  permission: "",
  seq: 0,
});

const modalRules = {
  name: [
    { required: true, message: "请输入端点名称", trigger: "blur" },
    { min: 2, max: 128, message: "端点名称长度必须在2-128个字符之间", trigger: "blur" },
  ],
  path: [
    { required: true, message: "请输入端点路径", trigger: "blur" },
    { max: 256, message: "端点路径长度不能超过256个字符", trigger: "blur" },
  ],
  permission: [{ max: 320, message: "所需权限长度不能超过320个字符", trigger: "blur" }],
  description: [{ max: 200, message: "端点描述长度不能超过200个字符", trigger: "blur" }],
  seq: [
    { required: true, message: "请输入排序", trigger: "blur" },
    { type: "number", min: 0, max: 655350, message: "排序只能在0-655350之间", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit" | "add-item", currentRow: GetEndpointTreeVo | null) => {
  modalMode.value = mode;
  modalCurrentRow.value = currentRow;
  resetModal();

  if (mode === "add") {
    modalForm.parentId = null;
  }

  if (mode === "add-item" && currentRow) {
    modalForm.parentId = currentRow.id;
  }

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && currentRow) {
    const ret = await EndpointApi.getEndpointDetails({ id: currentRow.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.parentId = ret.data.parentId;
      modalForm.name = ret.data.name;
      modalForm.description = ret.data.description;
      modalForm.path = ret.data.path;
      modalForm.permission = ret.data.permission;
      modalForm.seq = ret.data.seq;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  }

  modalVisible.value = true;
};

/**
 * 重置模态框表单
 * @param force 硬重置，不保留父级ID
 */
const resetModal = (force: boolean = false) => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.description = "";
  modalForm.path = "";
  modalForm.permission = "";
  modalForm.seq = 0;

  if (force) {
    modalForm.parentId = null;
  }
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
    if (modalMode.value === "add" || modalMode.value === "add-item") {
      await EndpointApi.addEndpoint(modalForm);
      ElMessage.success("操作成功");
      const parentId = modalForm.parentId;
      resetModal();
      modalForm.parentId = parentId;
    }

    if (modalMode.value === "edit") {
      await EndpointApi.editEndpoint(modalForm);
      ElMessage.success("操作成功");
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  } finally {
    modalLoading.value = false;
  }

  loadList();
};
</script>

<style scoped>
.endpoint-manager-container {
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

.endpoint-tree-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}
</style>
