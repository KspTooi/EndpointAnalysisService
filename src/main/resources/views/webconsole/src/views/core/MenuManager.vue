<template>
  <div class="menu-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item label="菜单名称">
          <el-input v-model="query.name" placeholder="请输入菜单名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="所需权限">
          <el-input v-model="query.permission" placeholder="请输入所需权限" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-select v-model="query.menuKind" placeholder="请选择菜单类型" clearable style="width: 200px">
            <el-option label="目录" value="0" />
            <el-option label="菜单" value="1" />
            <el-option label="按钮" value="2" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetList">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="openModal('add', null)">创建菜单</el-button>
    </div>

    <!-- 列表 -->
    <div class="menu-tree-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column label="菜单名称" prop="name">
          <template #default="scope">
            <Icon v-if="scope.row.menuIcon" :icon="scope.row.menuIcon" :width="16" :height="16" style="margin-right: 8px; vertical-align: middle" />
            {{ scope.row.name }}
          </template>
        </el-table-column>
        <el-table-column label="菜单类型" prop="menuKind" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.menuKind === 0">目录</el-tag>
            <el-tag v-if="scope.row.menuKind === 1" type="success">菜单</el-tag>
            <el-tag v-if="scope.row.menuKind === 2" type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="菜单路径" prop="menuPath" show-overflow-tooltip />
        <el-table-column label="所需权限" prop="permission" show-overflow-tooltip />
        <el-table-column label="排序" prop="seq" width="100" />
        <el-table-column label="操作" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.parentId === '-1' && scope.row.menuKind !== 2" link type="success" size="small" @click="openModal('add-item', scope.row)" :icon="PlusIcon">
              新增子项
            </el-button>
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon" :disabled="scope.row.children && scope.row.children.length > 0">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 菜单编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑菜单' : '添加菜单'"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="80px" :validate-on-rule-change="false">
        <el-form-item label="父级菜单" prop="parentId">
          <el-tree-select
            v-model="modalForm.parentId"
            :data="menuTreeForSelect"
            node-key="id"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            check-strictly
            placeholder="请选择父级菜单"
            clearable
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuKind">
          <el-select v-model="modalForm.menuKind" placeholder="请选择菜单类型" clearable>
            <el-option label="目录" :value="0" />
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜单路径" prop="menuPath" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuPath" placeholder="请输入菜单路径" clearable />
        </el-form-item>
        <el-form-item label="所需权限" prop="permission" v-if="modalForm.menuKind == 1 || modalForm.menuKind == 2">
          <el-input v-model="modalForm.permission" placeholder="请输入所需权限" clearable />
        </el-form-item>
        <el-form-item label="菜单描述" prop="description">
          <el-input v-model="modalForm.description" placeholder="请输入菜单描述" clearable />
        </el-form-item>
        <el-form-item label="菜单图标" prop="menuIcon">
          <IconPicker v-model="modalForm.menuIcon" />
        </el-form-item>
        <el-form-item label="查询参数" prop="menuQueryParam" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuQueryParam" placeholder="请输入菜单查询参数" clearable />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model="modalForm.seq" placeholder="请输入排序" clearable />
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

//列表内容
const query = reactive<GetMenuTreeDto>({
  name: "",
  menuKind: null,
  permission: "",
});

const list = ref<GetMenuTreeVo[]>([]);
const loading = ref(false);

const filterMenuTree = (menuTree: GetMenuTreeVo[], depth = 0): GetMenuTreeVo[] => {
  return menuTree
    .filter((item) => item.menuKind !== 2) // 过滤掉按钮
    .map((item) => ({
      ...item,
      disabled: depth >= 1, // 只允许选择根节点和一级菜单作为父级
      children: item.children ? filterMenuTree(item.children, depth + 1) : [],
    }));
};

const menuTreeForSelect = computed(() => {
  return [
    {
      id: "-1",
      name: "根节点",
      children: filterMenuTree(list.value),
    },
  ];
});

const loadList = async () => {
  loading.value = true;
  const result = await MenuApi.getMenuTree(query);

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
  query.menuKind = null;
  query.permission = "";
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该菜单吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await MenuApi.removeMenu({ id });
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  }
  //通知左侧菜单重新加载
  EventHolder().requestReloadLeftMenu();
  loadList();
};

loadList();

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
const modalForm = reactive<GetMenuDetailsVo>({
  id: "",
  parentId: "",
  name: "",
  description: "",
  kind: 0,
  menuKind: 0,
  menuPath: "",
  menuQueryParam: "",
  menuIcon: "",
  permission: "",
  seq: 0,
});

const modalRules = {
  name: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
  menuKind: [{ required: true, message: "请选择菜单类型", trigger: "blur" }],
  menuPath: [{ required: true, message: "请输入菜单路径", trigger: "blur" }],
  permission: [{ required: true, message: "请输入所需权限", trigger: "blur" }],
  seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
};

const openModal = async (mode: "add" | "edit" | "add-item", row: GetMenuTreeVo | null) => {
  modalMode.value = mode;
  resetModal();

  if (mode === "add") {
    modalForm.parentId = "-1";
  }

  if (mode === "add-item" && row) {
    modalForm.parentId = row.id;
  }

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    const ret = await MenuApi.getMenuDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.parentId = ret.data.parentId;
      modalForm.name = ret.data.name;
      modalForm.description = ret.data.description;
      modalForm.kind = ret.data.kind;
      modalForm.menuKind = ret.data.menuKind;
      modalForm.menuPath = ret.data.menuPath;
      modalForm.menuQueryParam = ret.data.menuQueryParam;
      modalForm.menuIcon = ret.data.menuIcon;
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

const resetModal = () => {
  modalForm.id = "";
  modalForm.parentId = "";
  modalForm.name = "";
  modalForm.description = "";
  modalForm.kind = 0;
  modalForm.menuKind = 0;
  modalForm.menuPath = "";
  modalForm.menuQueryParam = "";
  modalForm.menuIcon = "";
  modalForm.permission = "";
  modalForm.seq = 0;
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
      await MenuApi.addMenu(modalForm);
      ElMessage.success("操作成功");
      const parentId = modalForm.parentId;
      resetModal();
      modalForm.parentId = parentId;
    }

    if (modalMode.value === "edit") {
      //如果是目录则将权限设置为null
      if (modalForm.menuKind == 0) {
        modalForm.permission = "";
      }
      await MenuApi.editMenu(modalForm);
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

  //通知左侧菜单重新加载
  EventHolder().requestReloadLeftMenu();
};

watch(
  () => modalForm.menuKind,
  (newVal: number) => {
    console.log("modalForm.menuKind");
    console.log(newVal);
    if (newVal == 0) {
      modalForm.menuPath = "";
      modalForm.menuQueryParam = "";
      modalForm.permission = "";
    }
  },
  { immediate: true }
);
</script>

<style scoped>
.menu-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.query-form {
  margin-bottom: 20px;
}

.menu-tree-table {
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
