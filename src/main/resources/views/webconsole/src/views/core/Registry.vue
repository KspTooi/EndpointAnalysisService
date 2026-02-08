<template>
  <div class="list-layout">
    <splitpanes class="custom-theme">
      <!-- 左侧树形列表 -->
      <pane size="20" min-size="10" max-size="40">
        <div class="mt-2 px-1">
          <RegistryNodeTree @on-select="onSelectNode" />
        </div>
      </pane>

      <!-- 右侧内容区 -->
      <pane size="80">
        <StdListContainer>
          <StdListAreaQuery>
            <el-form :model="listForm" inline class="flex justify-between">
              <div>
                <el-form-item label="模糊搜索">
                  <el-input v-model="searchText" placeholder="搜索条目Key/标签" clearable @keyup.enter="handleSearch" />
                </el-form-item>
              </div>
              <el-form-item>
                <el-button type="primary" @click="handleSearch" :disabled="listLoading">查询</el-button>
                <el-button @click="resetList(currentKeyPath)" :disabled="listLoading">重置</el-button>
              </el-form-item>
            </el-form>
          </StdListAreaQuery>

          <StdListAreaAction class="flex gap-2">
            <el-button type="success" :disabled="!currentKeyPath" @click="openModal('add', null, currentNodeId)">
              新增条目
            </el-button>
          </StdListAreaAction>

          <StdListAreaTable>
            <el-table :data="filteredListData" stripe v-loading="listLoading" border height="100%">
              <el-table-column prop="nkey" label="Key" min-width="150" show-overflow-tooltip />
              <el-table-column prop="label" label="标签" min-width="120" />
              <el-table-column label="数据类型" width="100">
                <template #default="scope">
                  <el-tag size="small" v-if="scope.row.nvalueKind === 0">字符串</el-tag>
                  <el-tag size="small" v-if="scope.row.nvalueKind === 1" type="success">整数</el-tag>
                  <el-tag size="small" v-if="scope.row.nvalueKind === 2" type="warning">浮点</el-tag>
                  <el-tag size="small" v-if="scope.row.nvalueKind === 3" type="info">日期</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="nvalue" label="数据值" min-width="200" show-overflow-tooltip />
              <el-table-column prop="remark" label="说明" min-width="150" show-overflow-tooltip />
              <el-table-column label="状态" width="80">
                <template #default="scope">
                  <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
                    {{ scope.row.status === 0 ? "正常" : "停用" }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" min-width="170" />
              <el-table-column label="操作" fixed="right" width="140">
                <template #default="scope">
                  <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
                    编辑
                  </el-button>
                  <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </StdListAreaTable>
        </StdListContainer>
      </pane>
    </splitpanes>

    <!-- 注册表编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑条目' : '新增条目'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="100px"
      >
        <el-form-item label="Key" prop="nkey" v-if="modalMode === 'add'">
          <el-input v-model="modalForm.nkey" placeholder="请输入Key" />
        </el-form-item>
        <el-form-item label="标签" prop="label">
          <el-input v-model="modalForm.label" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="数据类型" prop="nvalueKind">
          <el-select v-model="modalForm.nvalueKind" placeholder="选择类型" style="width: 100%">
            <el-option label="字符串" :value="0" />
            <el-option label="整数" :value="1" />
            <el-option label="浮点" :value="2" />
            <el-option label="日期" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据值" prop="nvalue">
          <el-input v-model="modalForm.nvalue" type="textarea" :rows="3" placeholder="请输入数据值" />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model="modalForm.seq" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status" v-if="modalMode === 'add'">
          <el-radio-group v-model="modalForm.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="说明" prop="remark">
          <el-input v-model="modalForm.remark" type="textarea" placeholder="请输入说明" />
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
import { ref, computed, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import RegistryService from "@/views/core/service/RegistryService";
import RegistryNodeTree from "@/views/core/components/RegistryNodeTree.vue";
import type { GetRegistryNodeTreeVo } from "@/views/core/api/RegistryApi";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

const currentKeyPath = ref<string | null>(null);
const currentNodeId = ref<string | null>(null);
const searchText = ref("");

const onSelectNode = (node: GetRegistryNodeTreeVo | null) => {
  currentKeyPath.value = node?.keyPath ?? null;
  currentNodeId.value = node?.id ?? null;
  loadList(currentKeyPath.value);
};

const { listForm, listData, listLoading, loadList, resetList, removeList } = RegistryService.useRegistryList(currentKeyPath);

const modalFormRef = ref<FormInstance>();
const _loadList = () => loadList(currentKeyPath.value);

const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, submitModal } = 
  RegistryService.useRegistryModal(modalFormRef, _loadList);

const handleSearch = () => {
  // 本地过滤示例，或者根据需要修改 DTO 进行服务端过滤
  // 这里暂时实现本地过滤以简化演示
};

const filteredListData = computed(() => {
  if (!searchText.value) return listData.value;
  const lowerSearch = searchText.value.toLowerCase();
  return listData.value.filter(item => 
    (item.nkey?.toLowerCase().includes(lowerSearch)) || 
    (item.label?.toLowerCase().includes(lowerSearch))
  );
});
</script>

<style scoped>
.list-layout {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
}

:deep(.splitpanes.custom-theme) {
  border: none;
}

:deep(.splitpanes.custom-theme .splitpanes__pane) {
  background-color: transparent;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter) {
  background-color: var(--el-border-color-extra-light);
  width: 1px;
  border: none;
  cursor: col-resize;
  position: relative;
  transition: background-color 0.2s;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:hover) {
  background-color: var(--el-color-primary);
  width: 3px;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:after) {
  content: "";
  position: absolute;
  left: -5px;
  right: -5px;
  top: 0;
  bottom: 0;
  z-index: 1;
}

:deep(.splitpanes__pane) {
  transition: none !important;
}
</style>
