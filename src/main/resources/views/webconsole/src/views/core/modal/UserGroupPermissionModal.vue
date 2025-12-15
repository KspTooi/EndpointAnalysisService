<template>
  <el-dialog v-model="props.visible" title="管理权限" width="1000px" :close-on-click-modal="false" class="modal-centered" @close="resetModal">
    <div class="modal-content">
      <el-tabs v-model="tab">
        <el-tab-pane label="菜单视图" name="menu">
          <div class="list-table">
            <el-table
              :data="listData"
              v-loading="listLoading"
              border
              row-key="id"
              default-expand-all
              ref="listTableRef"
              max-height="500"
              @selection-change="(val: GetMenuTreeVo[]) => (listSelected = val)"
              @row-click="handleRowClick"
            >
              <el-table-column type="selection" width="40" />
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
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="节点视图" name="node"></el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="resetModal">取消</el-button>
        <el-button type="primary" @click="submitModal">应用权限</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { GetGroupDetailsVo, GetGroupListVo, GroupPermissionDefinitionVo } from "@/api/core/GroupApi";
import type { GetMenuTreeVo } from "@/api/core/MenuApi";
import MenuApi from "@/api/core/MenuApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, type TableInstance } from "element-plus";
import { nextTick, onMounted, ref, watch } from "vue";
import { Icon } from "@iconify/vue";
import GroupApi from "@/api/core/GroupApi";

const props = defineProps<{
  visible: boolean;
  row?: GetGroupListVo | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
}>();

//node显示权限节点，menu显示菜单
const tab = ref<"node" | "menu">("menu");

const listTableRef = ref<TableInstance>();
const listData = ref<GetMenuTreeVo[]>([]);
const listSelected = ref<GetMenuTreeVo[]>([]);
const listLoading = ref(true);

const groupDetails = ref<GetGroupDetailsVo | null>(null);

const loadList = async () => {
  listLoading.value = true;
  const result = await MenuApi.getMenuTree({});

  if (Result.isSuccess(result)) {
    listData.value = result.data;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const onLoad = async () => {
  //查询组详情
  const ret = await GroupApi.getGroupDetails({ id: props.row?.id });
  groupDetails.value = ret;

  //查询菜单树
  await loadList();
  selectMenuByHasPermission(listData.value, ret.permissions);
};

const selectMenuByHasPermission = (menu: GetMenuTreeVo[], exisisPermissions: GroupPermissionDefinitionVo[]) => {
  //更新已有的权限为菜单选中
  for (let item of menu) {
    let originPermissions = item.permission;

    if (originPermissions == null || originPermissions.length == 0) {
      //如果菜单有子级需要递归
      if (item.children && item.children.length > 0) {
        selectMenuByHasPermission(item.children, exisisPermissions);
      }
      continue;
    }

    //如果权限为*，默认拥有
    if (originPermissions == "*") {
      selectRow(item);
      //如果菜单有子级需要递归
      if (item.children && item.children.length > 0) {
        selectMenuByHasPermission(item.children, exisisPermissions);
      }
      continue;
    }

    //有；代表有多个权限，需要分割
    if (originPermissions.includes(";")) {
      let permissions = originPermissions.split(";");

      let has = 0;
      let total = permissions.length;

      for (let permission of permissions) {
        for (let details of exisisPermissions) {
          if (permission.includes(details.code) && details.has === 0) {
            has++;
          }
        }
      }

      if (has >= total) {
        selectRow(item);
      }

      continue;
    }

    //无;代表只有一个权限，直接判断
    let has = false;
    for (let details of exisisPermissions) {
      if (originPermissions.includes(details.code) && details.has === 0) {
        has = true;
        break;
      }
    }
    if (has) {
      selectRow(item);
    }

    //如果菜单有子级需要递归
    if (item.children && item.children.length > 0) {
      selectMenuByHasPermission(item.children, exisisPermissions);
    }
  }
};

const resetModal = () => {
  listSelected.value = [];
  listTableRef.value?.clearSelection();
  emit("close");
};

/**
 * 选择行但不选择子级
 * @param vo 菜单树
 */
const selectRow = (vo: GetMenuTreeVo) => {
  listTableRef.value?.toggleRowSelection(vo, true);

  if (vo.children && vo.children.length > 0) {
    for (let child of vo.children) {
      listTableRef.value?.toggleRowSelection(child, false);
    }
  }
};

/**
 * 处理行点击事件
 */
const handleRowClick = (row: GetMenuTreeVo, column: any, event: Event) => {
  const target = event.target as HTMLElement;
  if (target.closest(".el-checkbox") || target.closest(".el-table-column--selection")) {
    return;
  }

  const isSelected = listSelected.value.some((item) => item.id === row.id);
  listTableRef.value?.toggleRowSelection(row, !isSelected);
};

/**
 * 应用权限
 */
const submitModal = async () => {
  //搜集选中的权限代码
  const permissionCodes = grabSelectedPermissionCodes(listSelected.value);

  //应用权限
  const result = await GroupApi.applyPermission({
    groupId: props.row?.id || "",
    permissionCodes: permissionCodes,
  });

  if (Result.isSuccess(result)) {
    ElMessage.success("应用权限成功");
    await onLoad();
  }
};

const grabSelectedPermissionCodes = (menus: GetMenuTreeVo[]): string[] => {
  const permissions: string[] = [];

  for (const menu of menus) {
    if (!menu.permission || menu.permission === "*" || menu.permission.trim() === "") {
      continue;
    }

    const permParts = menu.permission.split(";");
    for (const part of permParts) {
      const trimmed = part.trim();
      if (trimmed && trimmed !== "*") {
        permissions.push(trimmed);
      }
    }
  }

  return permissions;
};

watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      await onLoad();
    }
  }
);
</script>

<style scoped>
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.el-table__body tr) {
  cursor: pointer;
}
</style>
