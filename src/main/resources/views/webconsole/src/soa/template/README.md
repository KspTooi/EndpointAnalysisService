# 前端 CRUD 模板使用说明

## 模板文件

- `api/ExampleApi.ts` - API 层（含 DTO/VO 定义 + CRUD 方法）
- `service/ExampleService.ts` - Service 层（含列表/新增/编辑管理）
- `Example.vue` - Vue 组件（含查询/表格/对话框）

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

**Service 层** - 修改表单初始值：

```typescript
listForm.value = { pageNum: 1, pageSize: 10, ...你的字段 };
addForm.value = { ...你的字段 };
editForm.value = { id: "", ...你的字段 };
```

**Vue 层** - 修改三个位置：

1. 查询表单 `<div class="query-form">` - 修改查询条件输入框
2. 表格列 `<el-table-column>` - 修改显示字段
3. 对话框表单 `<el-dialog>` - 修改新增/编辑字段（两个对话框）

## 代码规范

- 短路优先：if-return / if-continue / if-throw
- 禁止 switch 和 else
- TypeScript 强类型
- Vue 3 Composition API
