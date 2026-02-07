# Std-Series 标准列表页组件系列

## 概述

Std-Series 是一套用于构建标准列表页面的原子化布局组件，提供统一的样式规范和灵活的组合方式。

## 设计原则

- **原子化**：每个组件只负责局部样式和布局，不负责业务逻辑
- **可组合**：可以自由组合使用，适应不同的页面布局需求
- **样式统一**：统一管理间距、边框、背景色等样式规范

## 组件列表

### 1. StdListContainer.vue

最外层容器组件，提供统一的 padding 和宽度限制。

```vue
<StdListContainer>
  <!-- 内容 -->
</StdListContainer>
```

### 2. StdListAreaQuery.vue

查询表单区域容器，提供底部间距。

```vue
<StdListAreaQuery>
  <el-form>
    <!-- 查询表单 -->
  </el-form>
</StdListAreaQuery>
```

### 3. StdListAreaAction.vue

操作按钮区域容器，提供顶部虚线分隔和间距。

```vue
<StdListAreaAction>
  <el-button type="success">创建</el-button>
</StdListAreaAction>
```

### 4. StdListAreaTable.vue

表格区域容器，提供底部间距和横向滚动。

```vue
<StdListAreaTable>
  <el-table :data="list">
    <!-- 表格列 -->
  </el-table>
</StdListAreaTable>
```

### 5. StdListLayout.vue

快捷布局组件，内部组合了上述所有组件，提供插槽式使用方式。

```vue
<StdListLayout>
  <template #query>
    <!-- 查询表单 -->
  </template>
  
  <template #actions>
    <!-- 操作按钮 -->
  </template>
  
  <template #table>
    <!-- 表格 -->
  </template>
</StdListLayout>
```

## 使用场景

### 场景A：标准列表页（推荐使用 StdListLayout）

适用于简单的 CRUD 列表页，使用 `StdListLayout` 快速搭建。

```vue
<template>
  <StdListLayout>
    <template #query>
      <el-form :model="queryForm">
        <el-input v-model="queryForm.name" />
      </el-form>
    </template>
    
    <template #actions>
      <el-button type="success" @click="create">创建</el-button>
    </template>
    
    <template #table>
      <el-table :data="list">
        <el-table-column prop="name" label="名称" />
      </el-table>
    </template>
  </StdListLayout>
</template>
```

### 场景B：复杂布局（使用原子组件自由组合）

适用于需要左右分栏、嵌套布局等复杂场景。

```vue
<template>
  <splitpanes>
    <pane>
      <!-- 左侧树 -->
    </pane>
    <pane>
      <StdListContainer>
        <StdListAreaQuery>
          <!-- 查询表单 -->
        </StdListAreaQuery>
        
        <StdListAreaAction>
          <!-- 操作按钮 -->
        </StdListAreaAction>
        
        <StdListAreaTable>
          <!-- 表格 -->
        </StdListAreaTable>
      </StdListContainer>
    </pane>
  </splitpanes>
</template>
```

## 样式规范

| 组件 | 样式说明 |
|------|---------|
| StdListContainer | padding: 20px, 横向滚动支持 |
| StdListAreaQuery | margin-bottom: 15px |
| StdListAreaAction | border-top: 2px dashed, padding-top: 15px, margin-bottom: 15px |
| StdListAreaTable | margin-bottom: 20px, 横向滚动支持 |

## 参考示例

- 标准布局：`@/views/core/OrgManager.vue`
- 复杂布局：`@/views/core/UserManager.vue`
