<template>
  <el-popover
    v-model:visible="popoverVisible"
    placement="bottom"
    trigger="hover"
    :width="180"
    :hide-after="50"
    @before-enter="handleBeforeShow"
  >
    <template #reference>
      <el-button link type="success" :icon="Edit">{{ displayValue ?? "修改排序" }}</el-button>
    </template>
    <div v-loading="loading">
      <div class="popover-title">修改排序</div>
      <div class="popover-content">
        <el-input-number
          v-model.number="queryForm.seq"
          :min="0"
          :max="655350"
          size="small"
          :controls="false"
          class="seq-input"
        />
        <el-button-group class="quick-btns">
          <el-button
            size="small"
            :icon="ArrowUp"
            :disabled="queryForm.seq <= 0"
            @click="
              queryForm.seq--;
              handleConfirm();
            "
          />
          <el-button
            size="small"
            :icon="ArrowDown"
            @click="
              queryForm.seq++;
              handleConfirm();
            "
          />
        </el-button-group>
      </div>
      <div class="popover-footer">
        <el-button type="primary" size="small" @click="handleConfirm">修改</el-button>
        <el-button size="small" @click="handleCancel">关闭</el-button>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { Edit, ArrowUp, ArrowDown } from "@element-plus/icons-vue";
import SeqQuickPopoverService from "./service/SeqQuickPopoverService";

const props = defineProps<{
  id: string; //数据ID
  seqField: string; //排序字段名称
  getDetailApi: (id: string) => Promise<any>; //获取详情接口
  editApi: (id: string, dto: any) => Promise<any>; //编辑接口
  displayValue?: number | string; //显示值
  onSuccess?: () => void; //成功回调
}>();

const { queryForm, loading, popoverVisible, handleBeforeShow, handleConfirm, handleCancel } =
  SeqQuickPopoverService.useSeqQuickPopover(props.id, props.getDetailApi, props.editApi, props.seqField, props.onSuccess);
</script>

<style scoped>
.popover-title {
  font-size: 13px;
  font-weight: bold;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}

.popover-content {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 8px;
}

.seq-input {
  width: 80px;
}

.quick-btns {
  display: flex;
  flex-direction: row;
}

.popover-footer {
  display: flex;
  justify-content: flex-end;
}

:deep(.el-form-item) {
  margin-bottom: 0;
}
</style>
