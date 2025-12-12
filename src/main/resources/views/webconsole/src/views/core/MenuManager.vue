<template>
  <div class="list-container">
    <!-- 说明文档 -->
    <el-alert type="info" :closable="false" style="margin-bottom: 20px; margin-top: 20px">
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px">
          <el-icon><InfoFilled /></el-icon>
          <span style="font-weight: bold">权限节点缺失指示器</span>
        </div>
      </template>
      <div style="font-size: 13px; line-height: 1.6">
        <div>
          <span style="color: #67c23a; font-weight: bold">● 绿色</span>：权限完整，所有权限节点已在系统中定义
          <span style="margin-left: 16px; color: #e6a23c; font-weight: bold">● 橙色</span>：部分缺失，部分权限节点未在系统中定义
          <span style="margin-left: 16px; color: #f56c6c; font-weight: bold">● 红色</span>：完全缺失，所有权限节点均未在系统中定义
        </div>
      </div>
    </el-alert>

    <!-- 查询表单 -->
    <div class="query-form">
      <QueryPersistTip />
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="菜单名称">
              <el-input v-model="listForm.name" placeholder="请输入菜单名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="所需权限">
              <el-input v-model="listForm.permission" placeholder="请输入所需权限" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="菜单类型">
              <el-select v-model="listForm.menuKind" placeholder="请选择菜单类型" clearable>
                <el-option label="目录" value="0" />
                <el-option label="菜单" value="1" />
                <el-option label="按钮" value="2" />
              </el-select>
            </el-form-item>
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
      <el-button type="success" @click="openModal('add', null)">创建菜单</el-button>
      <el-button type="primary" @click="listExpandToggle()"> {{ listExpand ? "收起全部" : "展开全部" }} </el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id" default-expand-all ref="listTableRef">
        <el-table-column label="菜单名称" prop="name" show-overflow-tooltip width="360">
          <template #default="scope">
            <Icon v-if="scope.row.menuIcon" :icon="scope.row.menuIcon" :width="16" :height="16" style="margin-right: 8px; vertical-align: middle" />
            {{ scope.row.name }}
            <span v-if="scope.row.menuKind === 2" style="color: #999; font-size: 14px"> ({{ scope.row.menuBtnId }}) </span>
          </template>
        </el-table-column>
        <el-table-column label="类型" prop="menuKind" width="70">
          <template #default="scope">
            <el-tag v-if="scope.row.menuKind === 0">目录</el-tag>
            <el-tag v-if="scope.row.menuKind === 1" type="success">菜单</el-tag>
            <el-tag v-if="scope.row.menuKind === 2" type="info">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="菜单路径" prop="menuPath" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuKind === 0 || scope.row.menuKind === 2" style="color: #999; font-size: 12px">不适用</span>
            <span v-else>{{ scope.row.menuPath }}</span>
          </template>
        </el-table-column>
        <el-table-column label="所需权限" prop="permission" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuKind === 0" style="color: #999; font-size: 12px">不适用</span>
            <span
              v-else
              :style="{
                color:
                  scope.row.missingPermission === 1
                    ? '#f56c6c'
                    : scope.row.missingPermission === 2
                      ? '#e6a23c'
                      : scope.row.missingPermission === 0 && scope.row.permission !== '*'
                        ? '#67c23a'
                        : '',
              }"
            >
              {{ scope.row.permission }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="排序" prop="seq" width="65" />
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="scope">
            <div style="display: inline-flex; justify-content: flex-end; align-items: center; gap: 8px; width: 100%">
              <!-- 按钮下无法新增子项 -->
              <el-button v-if="scope.row.parentId === null || scope.row.menuKind == 1" link type="success" size="small" @click="openModal('add-item', scope.row)" :icon="PlusIcon">
                新增子项
              </el-button>

              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
              <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon" :disabled="scope.row.children && scope.row.children.length > 0">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 菜单编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑' + modalFormLabel : '添加' + modalFormLabel"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal(true);
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
        <el-form-item label="菜单类型" prop="menuKind">
          <el-select v-model="modalForm.menuKind" placeholder="请选择菜单类型" clearable :disabled="modalMode === 'edit'">
            <el-option label="目录" :value="0" :disabled="modalMode === 'add-item'" />
            <el-option label="菜单" :value="1" :disabled="modalMode === 'add-item' && modalCurrentRow?.menuKind == 1" />
            <el-option label="按钮" :value="2" :disabled="modalMode === 'add-item' && modalCurrentRow?.menuKind == 0" />
          </el-select>
        </el-form-item>
        <el-form-item :label="modalFormLabel + '名称'" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入菜单名称" clearable />
        </el-form-item>
        <el-form-item label="按钮ID" prop="menuBtnId" v-if="modalForm.menuKind == 2">
          <el-input v-model="modalForm.menuBtnId" placeholder="请输入按钮ID" clearable />
        </el-form-item>
        <el-form-item :label="modalFormLabel + '路径'" prop="menuPath" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuPath" placeholder="请输入菜单路径" clearable />
        </el-form-item>
        <el-form-item label="所需权限" prop="permission" v-if="modalForm.menuKind == 1 || modalForm.menuKind == 2">
          <el-input v-model="modalForm.permission" placeholder="请输入所需权限" clearable />
        </el-form-item>
        <el-form-item :label="modalFormLabel + '描述'" prop="description">
          <el-input v-model="modalForm.description" placeholder="请输入菜单描述" clearable />
        </el-form-item>
        <el-form-item :label="modalFormLabel + '图标'" prop="menuIcon" v-if="modalForm.menuKind == 0 || modalForm.menuKind == 1">
          <IconPicker v-model="modalForm.menuIcon" />
        </el-form-item>
        <el-form-item label="查询参数" prop="menuQueryParam" v-if="modalForm.menuKind == 1">
          <el-input v-model="modalForm.menuQueryParam" placeholder="请输入菜单查询参数" clearable />
        </el-form-item>
        <el-form-item label="是否隐藏" prop="menuHidden">
          <el-radio-group v-model="modalForm.menuHidden">
            <el-radio :value="0">不隐藏</el-radio>
            <el-radio :value="1">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model.number="modalForm.seq" :min="0" :max="655350" placeholder="请输入排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">关闭</el-button>
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
import { ElMessage, ElMessageBox, type FormInstance, type TableInstance } from "element-plus";
import { reactive, ref, watch, computed, onMounted } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, Plus as PlusIcon, InfoFilled } from "@element-plus/icons-vue";
import IconPicker from "@/components/common/IconPicker.vue";
import { Icon } from "@iconify/vue";
import { EventHolder } from "@/store/EventHolder";
import QueryPersistService from "@/service/QueryPersistService";
import QueryPersistTip from "@/components/common/QueryPersistTip.vue";

const listForm = reactive<GetMenuTreeDto>({
  name: "",
  menuKind: null,
  permission: "",
});
const listExpand = ref(false);
const listTableRef = ref<TableInstance>();
const listData = ref<GetMenuTreeVo[]>([]);
const listLoading = ref(true);

// 用于父级选择的完整菜单树
const fullMenuTree = ref<GetMenuTreeVo[]>([]);

const menuTreeForSelect = computed(() => {
  const currentMenu = modalForm;
  const isEditMode = modalMode.value === "edit";

  const filter = (menuTree: GetMenuTreeVo[]): GetMenuTreeVo[] => {
    return menuTree
      .filter((item) => item.menuKind !== 2) // 按钮不能作为父级
      .map((item) => {
        let disabled = false;

        // 编辑时，节点自身不能作为父级
        if (isEditMode && item.id === currentMenu.id) {
          disabled = true;
        }

        // 根据当前操作的菜单类型，判断父级是否可选
        // 0-目录 1-菜单 2-按钮

        // 当前是目录，父级只能是目录或根节点
        if (currentMenu.menuKind === 0) {
          if (item.menuKind === 1) disabled = true;
        }

        // 当前是菜单，父级只能是目录
        if (currentMenu.menuKind === 1) {
          if (item.menuKind !== 0) disabled = true;
        }

        // 当前是按钮，父级只能是菜单
        if (currentMenu.menuKind === 2) {
          if (item.menuKind !== 1) disabled = true;
        }

        return {
          ...item,
          disabled,
          children: item.children ? filter(item.children) : [],
        };
      });
  };

  let rootDisabled = false;
  // 菜单和按钮不能直接挂在根节点下
  if (currentMenu.menuKind === 1 || currentMenu.menuKind === 2) {
    rootDisabled = true;
  }

  return [
    {
      id: null,
      name: "根节点",
      disabled: rootDisabled,
      children: filter(fullMenuTree.value),
    },
  ];
});

// 加载完整菜单树用于父级选择
const loadFullMenuTree = async () => {
  const result = await MenuApi.getMenuTree({});
  if (Result.isSuccess(result)) {
    fullMenuTree.value = result.data;
  }
};

const loadList = async () => {
  listLoading.value = true;
  const result = await MenuApi.getMenuTree(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    QueryPersistService.persistQuery("menu-manager", listForm);
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const resetList = () => {
  listForm.name = "";
  listForm.menuKind = null;
  listForm.permission = "";
  QueryPersistService.clearQuery("menu-manager");
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
  await loadList();
};

const listExpandToggle = () => {
  //收起所有子级
  if (listExpand.value) {
    listExpand.value = false;
    listExpandToggleInner(listData.value, false);
    return;
  }
  listExpandToggleInner(listData.value, true);
  listExpand.value = true;
};

const listExpandToggleInner = (data: GetMenuTreeVo[], expand: boolean) => {
  for (let item of data) {
    listTableRef.value?.toggleRowExpansion(item, expand);
    if (item.children != undefined && item.children.length > 0) {
      listExpandToggleInner(item.children, expand);
    }
  }
};

onMounted(async () => {
  QueryPersistService.loadQuery("menu-manager", listForm);
  await loadList();
  await loadFullMenuTree();
});

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
const modalCurrentRow = ref<GetMenuTreeVo | null>(null);
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
  menuHidden: 0,
  menuBtnId: "",
  permission: "",
  seq: 0,
});

//计算表单label
const modalFormLabel = computed(() => {
  if (modalForm.menuKind == 0) {
    return "目录";
  }
  if (modalForm.menuKind == 1) {
    return "菜单";
  }
  if (modalForm.menuKind == 2) {
    return "按钮";
  }
  return "";
});
const modalRules = {
  name: [
    { required: true, message: "请输入菜单名称", trigger: "blur" },
    { min: 2, max: 32, message: "菜单名称长度必须在2-32个字符之间", trigger: "blur" },
  ],
  menuKind: [{ required: true, message: "请选择菜单类型", trigger: "blur" }],
  menuPath: [
    { required: true, message: "请输入菜单路径", trigger: "blur" },
    { max: 256, message: "菜单路径长度不能超过256个字符", trigger: "blur" },
  ],
  permission: [{ max: 320, message: "所需权限长度不能超过320个字符", trigger: "blur" }],
  description: [{ max: 200, message: "菜单描述长度不能超过200个字符", trigger: "blur" }],
  seq: [
    { required: true, message: "请输入排序", trigger: "blur" },
    { type: "number", min: 0, max: 655350, message: "排序只能在0-655350之间", trigger: "blur" },
  ],
  menuQueryParam: [{ max: 512, message: "菜单查询参数长度不能超过512个字符", trigger: "blur" }],
  menuIcon: [{ max: 64, message: "菜单图标长度不能超过64个字符", trigger: "blur" }],
  menuHidden: [{ required: true, message: "请选择是否隐藏", trigger: "blur" }],
  menuBtnId: [
    { required: () => modalForm.menuKind == 2, message: "类型为按钮时，按钮ID不能为空", trigger: "blur" },
    { max: 64, message: "按钮ID长度不能超过64个字符", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit" | "add-item", currentRow: GetMenuTreeVo | null) => {
  modalMode.value = mode;
  modalCurrentRow.value = currentRow;
  resetModal();

  if (mode === "add") {
    modalForm.parentId = null;
  }

  if (mode === "add-item" && currentRow) {
    modalForm.parentId = currentRow.id;

    //当前选项是目录 则首选菜单
    if (modalCurrentRow.value?.menuKind == 0) {
      modalForm.menuKind = 1;
    }

    //当前选项是菜单 则首选按钮
    if (modalCurrentRow.value?.menuKind == 1) {
      modalForm.menuKind = 2;
    }
  }

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && currentRow) {
    const ret = await MenuApi.getMenuDetails({ id: currentRow.id });

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
      modalForm.menuHidden = ret.data.menuHidden;
      modalForm.menuBtnId = ret.data.menuBtnId;
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
 * @param force 硬重置，不保留父级ID、菜单类型
 */
const resetModal = (force: boolean = false) => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.description = "";
  modalForm.kind = 0;
  modalForm.menuPath = "";
  modalForm.menuQueryParam = "";
  modalForm.menuIcon = "";
  modalForm.menuHidden = 0;
  modalForm.menuBtnId = "";
  modalForm.permission = "";
  modalForm.seq = 0;

  if (force) {
    modalForm.parentId = null;
    modalForm.menuKind = 0;
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
  await loadList();

  //通知左侧菜单重新加载
  EventHolder().requestReloadLeftMenu();
};

/**
 * 监听菜单类型变化
 */
watch(
  () => modalForm.menuKind,
  (newVal: number | null | undefined) => {
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
.list-container {
  padding: 0 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}
</style>
