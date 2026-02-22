<template>
  <el-button link type="primary" @click="toggle" :disabled="disabled">
    {{ isExpanded ? "收起" : "展开" }}
    <el-icon class="el-icon--right">
      <arrow-up v-if="isExpanded" />
      <arrow-down v-else />
    </el-icon>
  </el-button>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ArrowUp, ArrowDown } from "@element-plus/icons-vue";
import QueryPersistService from "@/service/QueryPersistService.ts";

const props = defineProps<{
  modelValue: boolean;
  disabled?: boolean;
}>();

const emit = defineEmits(["update:modelValue"]);

const route = useRoute();
const persistenceKey = `expand-state-${route.name as string}`;

const isExpanded = ref(props.modelValue);

const toggle = () => {
  isExpanded.value = !isExpanded.value;
  emit("update:modelValue", isExpanded.value);
  QueryPersistService.persistQuery(persistenceKey, { expanded: isExpanded.value });
};

watch(
  () => props.modelValue,
  (newValue) => {
    isExpanded.value = newValue;
  }
);

onMounted(() => {
  const persistedState = { expanded: false };
  QueryPersistService.loadQuery(persistenceKey, persistedState);
  if (persistedState.expanded !== undefined) {
    const expandedValue = persistedState.expanded as any;
    isExpanded.value = expandedValue === true || expandedValue === "true";
    emit("update:modelValue", isExpanded.value);
  }
});
</script>

<style scoped></style>
