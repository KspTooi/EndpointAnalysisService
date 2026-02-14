<script setup lang="ts">
import MaintainService from "@/views/core/service/MaintainService.ts";

/**
 * 维护中心管理逻辑
 */
const { loading, maintainOperations, executeOperation } = MaintainService.useMaintainOperation();

</script>

<template>
  <el-scrollbar class="maintain-scrollbar-wrapper w-full">
    <div class="maintain-admin-portal">
      
      <header class="portal-header">
        <h1 class="portal-title">系统核心维护中心</h1>
        <p class="portal-subtitle">管理底层数据校验、权限同步及核心资产重置逻辑</p>
      </header>

      <div class="maintain-grid">
        <div 
          v-for="(operation, index) in maintainOperations" 
          :key="index" 
          class="maintain-card"
          :style="{ '--op-accent': operation.iconColor }"
        >
          <!-- 顶部发光强调条 -->
          <div class="card-accent-bar"></div>
          
          <div class="card-content">
            <!-- 图标区域 -->
            <div class="icon-wrapper" :style="{ color: operation.iconColor, backgroundColor: operation.bgColor }">
              <el-icon :size="28">
                <component :is="operation.icon" />
              </el-icon>
            </div>

            <!-- 文本区域 -->
            <div class="text-wrapper">
              <h3 class="label">{{ operation.title }}</h3>
              <p class="desc">{{ operation.description }}</p>
            </div>

            <!-- 操作区域 -->
            <div class="action-wrapper">
              <el-button 
                type="primary" 
                class="execute-btn"
                :loading="loading"                 @click="executeOperation(operation)"
              >
                <el-icon class="mr-1"><component :is="operation.icon" /></el-icon>
                {{ operation.buttonText }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

    </div>
  </el-scrollbar>
</template>

<style scoped lang="scss">
.maintain-scrollbar-wrapper {
  height: 100%;
  background-color: #fff;
}

.maintain-admin-portal {
  padding: 30px;
  width: 100%;
  box-sizing: border-box;
}

.portal-header {
  margin-bottom: 40px;
  border-left: 4px solid #009688;
  padding-left: 20px;

  .portal-title {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
  }

  .portal-subtitle {
    font-size: 13px;
    color: #999;
    margin: 0;
  }
}

.maintain-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.maintain-card {
  position: relative;
  background-color: #fff;
  border: 1px solid #ebeef5;
  transition: all 0.25s cubic-bezier(0.645, 0.045, 0.355, 1);
  display: flex;
  flex-direction: column;
  min-height: 280px;

  &:hover {
    border-color: var(--op-accent);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
    
    .card-accent-bar {
      opacity: 1;
    }
  }

  .card-accent-bar {
    position: absolute;
    top: -1px;
    left: -1px;
    right: -1px;
    height: 3px;
    background: linear-gradient(90deg, var(--op-accent) 0%, #00a8be 100%);
    opacity: 0.6;
    transition: opacity 0.3s;
  }
}

.card-content {
  padding: 32px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  height: 100%;
  box-sizing: border-box;
}

.icon-wrapper {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  flex-shrink: 0;
}

.text-wrapper {
  flex-grow: 1;
  margin-bottom: 24px;

  .label {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 12px 0;
  }

  .desc {
    font-size: 13px;
    color: #666;
    line-height: 1.6;
    margin: 0;
  }
}

.action-wrapper {
  width: 100%;

  .execute-btn {
    width: 100%;
    height: 38px;
    font-size: 13px;
    border-radius: 0 !important;
  }
}

</style>
