# 前端 CRUD 模板使用说明

## 模板文件

- `api/ExampleApi.ts` - API 层（含 DTO/VO 定义 + CRUD 方法）
- `service/ExampleService.ts` - Service 层（含列表和统一模态框管理）
- `Example.vue` - Vue 组件（含查询/表格/统一模态框）

## 设计特点

- **统一模态框设计**：参考 UserManager.vue 方案，使用单一模态框处理新增和编辑，通过 `modalMode` 区分操作模式
- **表单验证**：集成 Element Plus FormInstance，支持完整的表单验证规则
- **代码复用**：避免重复代码，提高可维护性

## 使用步骤

### 1. 复制到目标目录

```
src/soa/template/api/ExampleApi.ts → src/views/{module}/api/{Module}Api.ts
src/soa/template/service/ExampleService.ts → src/views/{module}/service/{Module}Service.ts
src/soa/template/Example.vue → src/views/{module}/{Module}.vue
```

### 2. 全局替换（区分大小写）

| 原文本         | 替换为          | 用途          |
| -------------- | --------------- | ------------- |
| `Example`      | `{Module}`      | 类名/组件名   |
| `example`      | `{module}`      | 变量名/文件名 |
| `/example/`    | `/{module}/`    | API 路径      |
| `example-list` | `{module}-list` | 持久化 key    |

### 3. 修改业务字段

**API 层** - 修改所有 DTO/VO 接口的字段定义：

```typescript
GetExampleListDto / GetExampleListVo / AddExampleDto / EditExampleDto / GetExampleDetailsVo;
```

**Service 层** - 修改以下位置：

1. 列表表单初始值 `listForm.value`
2. 模态框表单初始值 `modalForm.value`（在 `openModal` 的 add 模式和 `resetModal` 方法中）
3. 表单验证规则 `modalRules`

**注意**：`modalForm` 使用 `GetExampleDetailsVo` 类型（字段最全），在提交时自动按照 `AddDto` 和 `EditDto` 定义提取对应字段

**Vue 层** - 修改三个位置：

1. 查询表单 `<StdListAreaQuery>` - 修改查询条件输入框
2. 表格列 `<StdListAreaTable>` 内部的 `<el-table-column>` - 修改显示字段
3. 模态框表单 `<el-dialog>` - 修改新增/编辑字段（统一模态框）

## 核心方法说明

### useExampleModal

统一的模态框管理方法，支持新增和编辑两种模式。

**参数**：
- `modalFormRef`: 表单引用，用于表单验证
- `reloadCallback`: 提交成功后的回调函数（通常是刷新列表）

**返回值**：
- `modalVisible`: 模态框是否可见
- `modalLoading`: 模态框提交加载状态
- `modalMode`: 当前模式（'add' | 'edit'）
- `modalForm`: 表单数据（类型为 `GetExampleDetailsVo`，包含所有字段）
- `modalRules`: 表单验证规则
- `openModal(mode, row)`: 打开模态框方法
- `resetModal()`: 重置模态框方法
- `submitModal()`: 提交模态框方法

**设计说明**：

- `modalForm` 使用 `GetExampleDetailsVo` 类型（字段最全的 VO）
- **新增模式**：初始化所有字段为默认值，提交时按 `AddExampleDto` 定义提取字段
- **编辑模式**：直接赋值 API 返回的 details，提交时按 `EditExampleDto` 定义提取字段
- 严格遵循后端 5 个 DTO 的字段定义，避免类型不一致

**使用示例**：

```typescript
// 打开新增模态框
openModal('add', null);

// 打开编辑模态框
openModal('edit', row);
```

## 代码规范

- 短路优先：if-return / if-continue / if-throw
- 禁止 switch 和 else
- TypeScript 强类型
- Vue 3 Composition API
- 使用 FormInstance 进行表单验证
