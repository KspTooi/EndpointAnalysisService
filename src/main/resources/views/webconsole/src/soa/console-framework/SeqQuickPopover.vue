<template>
  <el-popover placement="bottom" trigger="click" :width="300" @before-enter="handleBeforeShow">
    <template #reference>
      <el-button>修改排序</el-button>
    </template>
    <el-form :model="queryForm" v-loading="loading">
      <el-form-item label="排序">
        <el-input-number v-model.number="queryForm.seq" :min="0" :max="655350" placeholder="请输入排序" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
        <el-button @click="handleCancel">取消</el-button>
      </el-form-item>
    </el-form>
  </el-popover>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";

const props = defineProps<{
  seqField: string; //排序字段名称
  getDetailApi: () => Promise<any>; //获取详情接口
  editApi: (dto: any) => Promise<any>; //编辑接口
}>();

const queryForm = reactive({
  seq: 0,
});

const loading = ref(false);
const popoverVisible = ref(false);

const handleBeforeShow = async () => {
  loading.value = true;
  try {
    const res = await props.getDetailApi();
    if (!res) {
      return;
    }
    queryForm.seq = res[props.seqField] || 0;
  } catch (error) {
    ElMessage.error("获取数据失败");
    console.error("获取详情失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleConfirm = async () => {
  loading.value = true;
  try {
    const dto: any = {};
    dto[props.seqField] = queryForm.seq;
    await props.editApi(dto);
    ElMessage.success("修改成功");
    popoverVisible.value = false;
  } catch (error) {
    ElMessage.error("修改失败");
    console.error("修改失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleCancel = () => {
  popoverVisible.value = false;
};
</script>

<style scoped></style>
