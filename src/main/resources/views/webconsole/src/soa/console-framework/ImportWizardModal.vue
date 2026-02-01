<template>
  <el-dialog
    v-model="visible"
    title="导入向导"
    width="600px"
    :close-on-click-modal="false"
    destroy-on-close
    class="import-wizard-dialog"
  >
    <!-- 步骤指示器 -->
    <div class="steps-container">
      <div :class="['step-item', { active: currentStep >= 0 }]">1 下载并填写模板</div>
      <div class="step-line">--------</div>
      <div :class="['step-item', { active: currentStep >= 1 }]">2 上传模板</div>
      <div class="step-line">--------</div>
      <div :class="['step-item', { active: currentStep >= 2 }]">3 导入数据</div>
    </div>

    <!-- 步骤内容 -->
    <div class="step-content">
      <!-- 步骤1: 下载 -->
      <div v-if="currentStep === 0" class="step-pane">
        <div class="tip-text">请先下载模板，按格式填写数据后再进行上传。</div>
        <el-button type="primary" @click="handleDownload" :icon="DownloadIcon">下载模板</el-button>
      </div>

      <!-- 步骤2: 上传 -->
      <div v-if="currentStep === 1" class="step-pane">
        <el-upload
          ref="uploadRef"
          drag
          :auto-upload="false"
          :on-change="handleFileChange"
          :limit="1"
          :on-exceed="handleExceed"
          accept=".xlsx"
          :file-list="fileList"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">将文件拖到此处，或 <em>点击选择</em></div>
          <template #tip>
            <div class="el-upload__tip">只能上传 .xlsx 文件</div>
          </template>
        </el-upload>
      </div>

      <!-- 步骤3: 导入 -->
      <div v-if="currentStep === 2" class="step-pane">
        <div class="confirm-info" v-if="selectedFile">
          <el-icon><Document /></el-icon>
          <span class="filename">{{ selectedFile.name }}</span>
        </div>
        <div class="tip-text">确认无误后点击下方按钮开始导入数据。</div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button v-if="currentStep > 0" @click="currentStep--" :disabled="importing">上一步</el-button>
        <el-button v-if="currentStep < 2" type="primary" @click="handleNext">下一步</el-button>
        <el-button v-if="currentStep === 2" type="success" @click="handleImport" :loading="importing">开始导入</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Download, UploadFilled, Document } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import ExcelTemplateApi from "@/views/core/api/ExcelTemplateApi.ts";
import Http from "@/commons/Http.ts";

const props = defineProps<{
  url: string;
  templateCode: string;
}>();

const DownloadIcon = markRaw(Download);

const visible = ref(false);
const currentStep = ref(0);
const selectedFile = ref<any>(null);
const fileList = ref<any[]>([]);
const importing = ref(false);
const uploadRef = ref();

const openModal = () => {
  visible.value = true;
  currentStep.value = 0;
  selectedFile.value = null;
  fileList.value = [];
};

const handleDownload = async () => {
  if (!props.templateCode) {
    ElMessage.warning("未配置模板编码");
    return;
  }
  try {
    await ExcelTemplateApi.downloadExcelTemplate(props.templateCode);
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

const handleNext = () => {
  if (currentStep.value === 1 && !selectedFile.value) {
    ElMessage.warning("请先选择要上传的文件");
    return;
  }
  currentStep.value++;
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

<style scoped>
.steps-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  font-weight: bold;
}

.step-item {
  color: #999;
  font-size: 16px;
}

.step-item.active {
  color: #f5222d;
}

.step-line {
  margin: 0 15px;
  color: #f5222d;
  letter-spacing: -1px;
}

.step-content {
  min-height: 240px;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px dashed #dcdfe6;
  margin-bottom: 20px;
  padding: 20px;
}

.step-pane {
  text-align: center;
  width: 100%;
}

.tip-text {
  margin-bottom: 20px;
  color: #606266;
  font-size: 14px;
}

.confirm-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 16px;
}

.filename {
  color: #409eff;
  font-weight: bold;
}

/* 直角风格适配 */
:deep(.el-dialog) {
  border-radius: 0;
}

:deep(.el-button) {
  border-radius: 0;
}

:deep(.el-upload-dragger) {
  border-radius: 0;
  width: 100%;
}

:deep(.el-message-box) {
  border-radius: 0;
}

.el-icon--upload {
  font-size: 48px;
  color: #909399;
  margin-bottom: 10px;
}
</style>
