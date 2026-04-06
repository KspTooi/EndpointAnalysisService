<template>
  <StdListContainer>
    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button v-if="cdrcCanReturn" type="primary" @click="cdrcReturn">{{ cdrcReturnName }}</el-button>
    </StdListAreaAction>

    <!-- 原始模型信息区域 -->
    <div class="schema-info-bar">
      <div class="schema-info-item">
        <span class="schema-info-label">输出方案</span>
        <span class="schema-info-value">{{ schemaInfo.name }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">模型名称</span>
        <span class="schema-info-value">{{ schemaInfo.modelName }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">数据源表名</span>
        <span class="schema-info-value">{{ schemaInfo.tableName }}</span>
      </div>
    </div>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table v-loading="listLoading" :data="listData" stripe border height="100%">
        <el-table-column prop="seq" label="序号" min-width="45" show-overflow-tooltip align="center" />
        <el-table-column prop="name" label="字段名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="length" label="长度" min-width="100" show-overflow-tooltip />
        <el-table-column prop="require" label="必填" min-width="100" show-overflow-tooltip align="center">
          <template #default="scope">
            <span :style="{ color: scope.row.require === 1 ? '#F56C6C' : '#909399' }">
              {{ scope.row.require === 1 ? "是" : "否" }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="注释" min-width="120" show-overflow-tooltip />
      </el-table>
    </StdListAreaTable>
  </StdListContainer>
</template>

<script setup lang="ts">
import RawModelService from "@/views/assembly/service/RawModelService";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext.ts";
import type { GetOpSchemaListVo } from "@/views/assembly/api/OpSchemaApi";

//使用CDRC打包上下文
const { cdrcCanReturn, cdrcReturnName, cdrcReturn, getCdrcQuery } = ComDirectRouteContext.useDirectRouteContext();

const schemaInfo = getCdrcQuery() as GetOpSchemaListVo;

const { listData, listLoading } = RawModelService.useRawModelList(schemaInfo, cdrcReturn);
</script>

<style scoped>
.schema-info-bar {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 8px;
  background: #fff;
  border-radius: 0;
  border: 1px solid #e4e7ed;
  border-left: 4px solid #409eff;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.schema-info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 20px;
}

.schema-info-label {
  font-size: 11px;
  color: #909399;
  letter-spacing: 0.5px;
}

.schema-info-value {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.schema-info-divider {
  width: 1px;
  height: 36px;
  background: #e4e7ed;
  flex-shrink: 0;
}
</style>
