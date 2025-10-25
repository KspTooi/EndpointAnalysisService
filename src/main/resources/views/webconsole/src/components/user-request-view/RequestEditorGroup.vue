<template>
  <div class="reg-container">
    <!-- Loading 遮罩 -->
    <div v-show="globalLoading" class="global-loading-overlay">
      <div class="global-loading-spinner"></div>
      <div class="global-loading-text">正在处理...</div>
    </div>

    <div class="header">
      <div class="header-title">
        {{ formData?.name }}
      </div>
      <div>
        <el-button type="primary" @click="loadGroupDetail">加载组</el-button>
        <el-button type="primary" @click="saveGroup">保存组</el-button>
      </div>
    </div>

    <div class="content-tab">
      <div class="tab-item" :class="{ active: PreferenceHolder().getRequestGroupEditorTab === 'filter' }" @click="PreferenceHolder().setRequestGroupEditorTab('filter')">
        过滤器配置
      </div>
      <div
        class="tab-item"
        :class="{ active: PreferenceHolder().getRequestGroupEditorTab === 'filter-group' }"
        @click="PreferenceHolder().setRequestGroupEditorTab('filter-group')"
      >
        过滤器组
      </div>
      <div
        class="tab-item"
        :class="{ active: PreferenceHolder().getRequestGroupEditorTab === 'request-list' }"
        @click="PreferenceHolder().setRequestGroupEditorTab('request-list')"
      >
        请求列表
      </div>
    </div>

    <div class="content-filter" v-show="PreferenceHolder().getRequestGroupEditorTab === 'filter'">
      <div class="section">
        <div class="section-header">
          <div class="section-title">
            在此组上应用的过滤器 (?)
            <el-tooltip content="操作定义了过滤器触发后要执行的动作。可以对数据进行持久化、缓存、注入或URL覆写等操作。" placement="top">
              <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
            </el-tooltip>
          </div>
          <div style="margin-bottom: 15px">
            <el-switch v-model="inheritParentFilters" active-text="继承父级过滤器" inactive-text="不继承" />
          </div>

          <el-table :data="appliedFilters" :border="true" size="small" :disabled="inheritParentFilters">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="direction" label="方向">
              <template #default="{ row }">
                <span v-if="row.direction === 0" class="gradient-text gradient-blue">请求</span>
                <span v-if="row.direction === 1" class="gradient-text gradient-green">响应</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <span v-if="row.status === 0" class="gradient-text gradient-blue">启用</span>
                <span v-if="row.status === 1" class="gradient-text gradient-green">禁用</span>
              </template>
            </el-table-column>
            <el-table-column prop="triggerCount" label="触发器数量" />
            <el-table-column prop="operationCount" label="操作数量" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="removeFilter(row)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <div class="section">
        <div class="section-header">
          <div class="section-title">
            可用的过滤器 (?)
            <el-tooltip content="操作定义了过滤器触发后要执行的动作。可以对数据进行持久化、缓存、注入或URL覆写等操作。" placement="top">
              <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
            </el-tooltip>
          </div>

          <el-table :data="availableFilters" :border="true" size="small">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="direction" label="方向">
              <template #default="{ row }">
                <span v-if="row.direction === 0" class="gradient-text gradient-blue">请求</span>
                <span v-if="row.direction === 1" class="gradient-text gradient-green">响应</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <span v-if="row.status === 0" class="gradient-text gradient-blue">启用</span>
                <span v-if="row.status === 1" class="gradient-text gradient-green">禁用</span>
              </template>
            </el-table-column>
            <el-table-column prop="triggerCount" label="触发器数量" />
            <el-table-column prop="operationCount" label="操作数量" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="addFilter(row)" :disabled="isFilterApplied(row.id)">
                  {{ isFilterApplied(row.id) ? "已添加" : "添加" }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>

    <div class="content-request-list" v-show="PreferenceHolder().getRequestGroupEditorTab === 'request-list'">
      <div class="section">
        <div class="section-header">
          <div class="section-title">请求列表</div>
          <div class="empty-state">
            <p>该功能不可用</p>
          </div>
        </div>
      </div>
    </div>
    <div class="content-filter-group" v-show="PreferenceHolder().getRequestGroupEditorTab === 'filter-group'">
      <div class="section">
        <div class="section-header">
          <div class="section-title">过滤器组</div>
          <div class="empty-state">
            <p>该功能不可用</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GetUserRequestGroupDetailsVo } from "@/api/requestdebug/UserRequestGroupApi.ts";
import { onMounted, ref, watch } from "vue";
import { RequestTreeHolder } from "@/store/RequestTreeHolder.ts";
import UserRequestGroupApi from "@/api/requestdebug/UserRequestGroupApi.ts";
import { ElMessage } from "element-plus";
import { PreferenceHolder } from "@/store/PreferenceHolder";
import SimpleFilterApi, { type GetSimpleFilterListVo } from "@/api/requestdebug/SimpleFilterApi.ts";
import { EventHolder } from "@/store/EventHolder";

const globalLoading = ref(false);

const formData = ref<GetUserRequestGroupDetailsVo>();
const appliedFilters = ref<GetSimpleFilterListVo[]>([]);
const availableFilters = ref<GetSimpleFilterListVo[]>([]);
const inheritParentFilters = ref(false); // 新增的继承父级过滤器选项

const loadGroupDetail = async () => {
  if (RequestTreeHolder().getActiveGroupId == null) {
    return;
  }
  globalLoading.value = true;
  try {
    const res = await UserRequestGroupApi.getUserRequestGroupDetails({ id: RequestTreeHolder().getActiveGroupId });
    formData.value = res;
    appliedFilters.value = res.simpleFilters || [];
    // inheritParentFilters.value = res.inheritParentFilters || false; // 从后端加载继承状态
  } catch (e) {
    ElMessage.error(`无法加载请求组配置:${e}`);
  } finally {
    globalLoading.value = false;
  }
};

const loadSimpleFilters = async () => {
  const res = await SimpleFilterApi.getSimpleFilterList({
    name: null,
    direction: null,
    pageNum: 1,
    pageSize: 1000,
    status: 0,
  });
  availableFilters.value = res.data;
};

const addFilter = (filter: GetSimpleFilterListVo) => {
  if (isFilterApplied(filter.id)) {
    return;
  }
  appliedFilters.value.push(filter);
};

const removeFilter = (filter: GetSimpleFilterListVo) => {
  const index = appliedFilters.value.findIndex((f) => f.id === filter.id);
  if (index > -1) {
    appliedFilters.value.splice(index, 1);
  }
};

const isFilterApplied = (filterId: string): boolean => {
  return appliedFilters.value.some((f) => f.id === filterId);
};

const saveGroup = async () => {
  if (!formData.value) {
    ElMessage.error("请先加载组详情");
    return;
  }
  globalLoading.value = true;
  try {
    await UserRequestGroupApi.editUserRequestGroup({
      id: formData.value.id,
      name: formData.value.name,
      description: formData.value.description,
      simpleFilterIds: appliedFilters.value.map((f) => f.id),
      // inheritParentFilters: inheritParentFilters.value, // 保存继承状态
    });

    //通知树重新加载
    EventHolder().requestReloadTree();
    ElMessage.success("保存成功");
  } catch (e) {
    ElMessage.error(`保存失败:${e}`);
  } finally {
    globalLoading.value = false;
  }
};

onMounted(() => {
  loadGroupDetail();
  loadSimpleFilters();
});

watch(
  () => RequestTreeHolder().getActiveGroupId,
  () => {
    loadGroupDetail();
  }
);

watch(
  () => EventHolder().isNeedReloadRequestDetails,
  (newVal: number) => {
    loadGroupDetail();
  }
);
</script>

<style scoped>
/* Loading 遮罩样式 */
.global-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100000;
}

.global-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.global-loading-text {
  margin-top: 16px;
  color: #495057;
  font-size: 14px;
}

.reg-container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header {
  background: #f8f9fa;
  padding: 10px 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
}

.header-name-input {
  width: 100%;
  height: 32px;
}

.header-description-input {
  width: 100%;
  height: 32px;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-tab {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e9ecef;
}

.tab-item {
  padding: 5px 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  color: #6c757d;
  font-weight: 400;
  font-size: 14px;
}

.tab-item.active {
  color: #007bff;
  border-bottom-color: #007bff;
  background: #fff;
}

.content-filter {
  flex: 1;
  overflow-y: auto;
}

.section {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.section:last-child {
  border-bottom: none;
}

.section-header {
  margin-bottom: 16px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 16px;
}

.gradient-text.gradient-blue {
  background: linear-gradient(45deg, #007bff, #0056b3);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 500;
}

.gradient-text.gradient-green {
  background: linear-gradient(45deg, #28a745, #155724);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  font-weight: 500;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
