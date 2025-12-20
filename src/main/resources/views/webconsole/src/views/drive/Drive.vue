<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="关键字">
              <el-input v-model="listForm.keyword" placeholder="条目名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="父级ID">
              <el-input v-model="listForm.parentId" placeholder="输入父级ID查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
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

    <!-- 条目列表 -->
    <div class="list-grid" v-loading="listLoading" @contextmenu.prevent="handleGridRightClick">
      <DriveEntryParentItem :target-id="listForm.parentId" name="上级目录" v-show="listForm.parentId" @dblclick="listReturnParentDir" />

      <DriveEntryItem
        v-for="item in listData"
        :key="item.id"
        :id="item.id"
        :name="item.name"
        :kind="item.kind"
        :attach-id="item.attachId"
        :attach-size="item.attachSize"
        :attach-suffix="item.attachSuffix"
        @click="handleEntryClick"
        @dblclick="handleEntryDoubleClick"
        @contextmenu="(event: MouseEvent) => handleEntryRightClick(event, item)"
      />
    </div>

    <!-- 右键菜单 -->
    <DriveEntryRightMenu
      :visible="rightMenuVisible"
      :x="rightMenuX"
      :y="rightMenuY"
      :current-entry="rightMenuCurrentEntry"
      @close="handleRightMenuClose"
      @create-folder="handleCreateFolder"
      @preview="handlePreview"
      @download="handleDownload"
      @cut="handleCut"
      @copy="handleCopy"
      @delete="handleDelete"
      @rename="handleRename"
      @properties="handleProperties"
    />

    <DriveCreateEntryModal
      :visible="modalCreateEntryVisible"
      @close="
        () => {
          modalCreateEntryVisible = false;
        }
      "
      @success="loadList"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import DriveApi, { type GetEntryListDto, type GetEntryListVo } from "@/api/drive/DriveApi.ts";
import DriveCreateEntryModal from "@/views/drive/components/DriveCreateEntryModal.vue";
import DriveEntryItem from "@/views/drive/components/DriveEntryItem.vue";
import DriveEntryRightMenu from "@/views/drive/components/DriveEntryRightMenu.vue";
import DriveEntryParentItem from "@/views/drive/components/DriveEntryParentItem.vue";
import { Result } from "@/commons/entity/Result";

const listForm = reactive<GetEntryListDto>({
  parentId: null,
  keyword: null,
  pageNum: 1,
  pageSize: 50000,
});

const listData = ref<GetEntryListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const modalCreateEntryVisible = ref(false);

const rightMenuVisible = ref(false);
const rightMenuX = ref(0);
const rightMenuY = ref(0);
const rightMenuCurrentEntry = ref<GetEntryListVo | null>(null);
const currentSelectedEntry = ref<GetEntryListVo | null>(null);

const handleEntryClick = (id: string) => {
  const entry = listData.value.find((item) => item.id === id);
  if (entry) {
    currentSelectedEntry.value = entry;
  }
};

const handleGridRightClick = (event: MouseEvent) => {
  rightMenuX.value = event.clientX;
  rightMenuY.value = event.clientY;
  rightMenuCurrentEntry.value = null;
  rightMenuVisible.value = true;
};

const handleEntryRightClick = (event: MouseEvent, entry: GetEntryListVo) => {
  rightMenuX.value = event.clientX;
  rightMenuY.value = event.clientY;
  rightMenuCurrentEntry.value = entry;
  currentSelectedEntry.value = entry;
  rightMenuVisible.value = true;
};

const handleRightMenuClose = () => {
  rightMenuVisible.value = false;
};

const handleEntryDoubleClick = (id: string, kind: number) => {
  if (kind === 1) {
    // 双击文件夹，进入文件夹
    listForm.parentId = id;
    listForm.pageNum = 1;
    loadList();
  }
  if (kind === 0) {
    // 双击文件，可根据需要实现下载或预览
  }
};

const loadList = async () => {
  listLoading.value = true;
  try {
    const res = await DriveApi.getEntryList(listForm);
    if (res.code === 0) {
      listData.value = res.data;
      listTotal.value = res.total;
    }
    if (res.code !== 0) {
      ElMessage.error(res.message || "加载列表失败");
    }
  } catch (error: any) {
    ElMessage.error(error.message || "加载列表失败");
  } finally {
    listLoading.value = false;
  }
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 50000;
  listForm.keyword = null;
  listForm.parentId = null;
  loadList();
};

const handleCreateFolder = () => {
  modalCreateEntryVisible.value = true;
};

const handlePreview = (entry: GetEntryListVo) => {
  // 预览逻辑，可根据需要实现
  ElMessage.info("预览功能待实现");
};

const handleDownload = (entry: GetEntryListVo) => {
  // 下载逻辑，可根据需要实现
  ElMessage.info("下载功能待实现");
};

const handleCut = (entry: GetEntryListVo) => {
  // 剪切逻辑，可根据需要实现
  ElMessage.info("剪切功能待实现");
};

const handleCopy = (entry: GetEntryListVo) => {
  // 复制逻辑，可根据需要实现
  ElMessage.info("复制功能待实现");
};

const handleDelete = (entry: GetEntryListVo) => {
  // 删除逻辑，可根据需要实现
  ElMessage.info("删除功能待实现");
};

const handleRename = (entry: GetEntryListVo) => {
  // 重命名逻辑，可根据需要实现
  ElMessage.info("重命名功能待实现");
};

const handleProperties = (entry: GetEntryListVo) => {
  // 属性逻辑，可根据需要实现
  ElMessage.info("属性功能待实现");
};

loadList();

const listReturnParentDir = async (parentId: string | null) => {
  const result = await DriveApi.getEntryDetails({ id: parentId });

  if (Result.isSuccess(result)) {
    listForm.parentId = result.data.parentId;
    listForm.pageNum = 1;
    listForm.pageSize = 50000;
    await loadList();
    return;
  }

  ElMessage.error(result.message);
};
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-grid {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  padding: 10px 0;
  width: calc(100% - 2px);
  height: calc(100vh - 200px);
  border: 1px solid var(--el-border-color);
  overflow-y: auto;
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
