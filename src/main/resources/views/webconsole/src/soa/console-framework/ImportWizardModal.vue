<template>
  <el-dialog
    v-model="visible"
    title="数据导入"
    width="580px"
    :close-on-click-modal="false"
    destroy-on-close
    @close="emit('on-close')"
    class="import-wizard-dialog"
  >
    <!-- 步骤引导区：采用系统主题色，紧凑直角风格 -->
    <div class="guide-steps">
      <div :class="['step-node', { active: true }]">
        <span class="step-index">1</span>
        <span class="step-label">准备数据</span>
      </div>
      <div class="step-divider"></div>
      <div :class="['step-node', { active: !!selectedFile }]">
        <span class="step-index">2</span>
        <span class="step-label">上传文件</span>
      </div>
      <div class="step-divider"></div>
      <div :class="['step-node', { active: !!selectedFile && !importing }]">
        <span class="step-index">3</span>
        <span class="step-label">完成导入</span>
      </div>
    </div>

    <div class="import-body">
      <!-- 第一步：下载 -->
      <div class="import-section">
        <div class="section-title">
          <el-icon><Download /></el-icon>
          <span>第一步：下载空白模板</span>
        </div>
        <div class="section-content">
          <div class="download-card" @click="handleDownload">
            <div class="card-info">
              <span class="filename">数据导入模板 (点击下载).xlsx</span>
              <span class="filesize">下载后请在表格中填写需要导入的信息</span>
            </div>
            <el-button link type="primary">立即下载</el-button>
          </div>
          <p class="section-tip">💡 提示：请不要修改模板中的表头名称和列顺序，否则系统将无法识别您的数据。</p>
        </div>
      </div>

      <!-- 第二步：上传 -->
      <div class="import-section">
        <div class="section-title">
          <el-icon><UploadFilled /></el-icon>
          <span>第二步：上传填写好的表格</span>
        </div>
        <div class="section-content">
          <el-upload
            ref="uploadRef"
            drag
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :on-exceed="handleExceed"
            accept=".xlsx"
            :file-list="fileList"
            class="compact-upload"
          >
            <div v-if="!selectedFile" class="upload-placeholder">
              <el-icon class="upload-icon"><UploadFilled /></el-icon>
              <div class="upload-text">把填好的 Excel 文件 <em>拖到这里</em>，或者 <em>点击选择文件</em></div>
            </div>
            <div v-else class="upload-finished">
              <el-icon class="file-icon"><Document /></el-icon>
              <div class="file-meta">
                <span class="name">{{ selectedFile.name }}</span>
                <span class="status">✅ 检查完毕，文件可以导入</span>
              </div>
              <el-button link type="primary" class="reselect-btn">重新选择文件</el-button>
            </div>
          </el-upload>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false" :disabled="importing">取消导入</el-button>
        <el-button type="primary" @click="handleImport" :loading="importing" :disabled="!selectedFile" class="submit-btn">
          {{ importing ? "正在导入数据，请耐心等待..." : "确认无误,开始导入" }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Download, UploadFilled, Document } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import ExcelTemplateApi from "@/views/core/api/ExcelTemplateApi.ts";
import Http from "@/commons/Http.ts";

const props = defineProps<{
  url: string;
  templateCode: string;
}>();

const emit = defineEmits<{
  (e: "on-success", data: any): void;
  (e: "on-close"): void;
}>();

const visible = ref(false);
const selectedFile = ref<any>(null);
const fileList = ref<any[]>([]);
const importing = ref(false);
const uploadRef = ref();

const openModal = () => {
  visible.value = true;
  selectedFile.value = null;
  fileList.value = [];
  importing.value = false;
};

const handleDownload = async () => {
  if (!props.templateCode) {
    ElMessage.warning("未配置模板编码");
    return;
  }
  try {
    await ExcelTemplateApi.downloadExcelTemplate(props.templateCode);
    ElMessage.success("模板下载成功");
  } catch (e: any) {
    ElMessage.error(e.message || "下载失败");
  }
};

const handleFileChange = (file: any) => {
  selectedFile.value = file;
};

const handleExceed = (files: any) => {
  uploadRef.value!.clearFiles();
  const file = files[0];
  uploadRef.value!.handleStart(file);
};

const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning("请先选择要上传的文件");
    return;
  }

  importing.value = true;

  try {
    const res = await Http.postForm<any>(props.url, {
      file: selectedFile.value.raw,
    });
    ElMessage.success(res.message || "导入成功");
    emit("on-success", res);
    visible.value = false;
  } catch (e: any) {
    ElMessage.error(e.message || "导入失败");
  } finally {
    importing.value = false;
  }
};

defineExpose({
  openModal,
});
</script>

<style scoped lang="scss">
/* 导入引导步骤条 */
.guide-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 0 25px;
  background-color: #fcfcfc;
  border-bottom: 1px solid #f0f0f0;
  margin: -16px -20px 20px; /* 抵消 el-dialog__body 的 padding */

  .step-node {
    display: flex;
    align-items: center;
    gap: 8px;
    opacity: 0.4;
    transition: all 0.3s;

    &.active {
      opacity: 1;
      .step-index {
        background-color: #009688;
        color: #fff;
      }
      .step-label {
        color: #333;
        font-weight: 600;
      }
    }

    .step-index {
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      background-color: #e0e0e0;
      color: #666;
      font-size: 11px;
      font-weight: bold;
      border-radius: 0;
    }

    .step-label {
      font-size: 13px;
      color: #999;
    }
  }

  .step-divider {
    width: 40px;
    height: 1px;
    background-color: #e0e0e0;
    margin: 0 15px;
  }
}

.import-body {
  padding: 0 10px;
}

.import-section {
  margin-bottom: 24px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 600;
    color: #333;
    margin-bottom: 12px;

    .el-icon {
      color: #009688;
      font-size: 16px;
    }
  }

  .section-content {
    padding-left: 24px;
  }
}

/* 下载卡片样式 */
.download-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #f9f9f9;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background-color: #f0f7f7;
    border-color: #009688;
  }

  .card-info {
    display: flex;
    flex-direction: column;

    .filename {
      font-size: 13px;
      color: #333;
      font-weight: 500;
    }

    .filesize {
      font-size: 11px;
      color: #999;
      margin-top: 2px;
    }
  }
}

.section-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
  line-height: 1.5;
}

/* 上传区域样式 */
.compact-upload {
  :deep(.el-upload-dragger) {
    padding: 20px 0;
    height: auto;
    border: 1px dashed #dcdfe6;
    background-color: #fafafa;
    border-radius: 0;

    &:hover {
      border-color: #009688;
      background-color: #f0f7f7;
    }
  }
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;

  .upload-icon {
    font-size: 32px;
    color: #999;
    margin-bottom: 8px;
  }

  .upload-text {
    font-size: 13px;
    color: #666;

    em {
      color: #009688;
      font-style: normal;
      font-weight: 600;
    }
  }
}

.upload-finished {
  display: flex;
  align-items: center;
  padding: 0 30px;
  text-align: left;
  gap: 15px;

  .file-icon {
    font-size: 28px;
    color: #00a8be;
  }

  .file-meta {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;

    .name {
      font-size: 13px;
      color: #333;
      font-weight: 600;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }

    .status {
      font-size: 11px;
      color: #00a8be;
      margin-top: 2px;
    }
  }

  .reselect-btn {
    font-size: 12px;
  }
}

/* 底部按钮 */
.dialog-footer {
  .submit-btn {
    background-color: #009688;
    border-color: #009688;

    &:hover {
      background-color: #00796b;
      border-color: #00796b;
    }

    &.is-disabled {
      background-color: #a2cfcb;
      border-color: #a2cfcb;
    }
  }
}

/* 覆盖全局 dialog-body padding 以适应步骤条 */
:deep(.el-dialog__body) {
  padding-top: 16px !important;
}
</style>
