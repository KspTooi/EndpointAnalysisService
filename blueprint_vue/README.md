# Blueprint Vue 前端代码生成模板

此目录包含用于生成前端 Vue3 + TypeScript CRUD 代码的 Velocity 模板。

## 模板文件

```
blueprint_vue/
└── src/
    └── views/
        └── #{PTSCN}/           # 模块目录（小驼峰表名）
            ├── api/
            │   └── #{PTSTN}Api.ts.vm        # API 接口层
            ├── service/
            │   └── #{PTSTN}Service.ts.vm    # Service 业务逻辑层
            └── #{PTSTN}.vue.vm              # Vue 组件
```

## 可用变量

### 表级变量
- `${ptstn}` - 大驼峰表名（如 NoticeTemplate）
- `${ptscn}` - 小驼峰表名（如 noticeTemplate）
- `${comment}` - 表注释
- `${fields}` - 字段列表

### 字段级变量
- `${field.pfscn}` - 小驼峰字段名（如 userId）
- `${field.type}` - TypeScript 类型（string/number/boolean）
- `${field.comment}` - 字段注释
- `${field.primaryKey}` - 是否主键
- `${field.required}` - 是否必填

## 生成规则

### API 层 (#{PTSTN}Api.ts.vm)
- 生成 5 个 DTO/VO 接口：
  - `Get${ptstn}ListDto` - 查询列表 DTO（排除系统字段）
  - `Get${ptstn}ListVo` - 列表 VO（包含所有字段）
  - `Get${ptstn}DetailsVo` - 详情 VO（包含所有字段）
  - `Add${ptstn}Dto` - 新增 DTO（排除主键和系统字段）
  - `Edit${ptstn}Dto` - 编辑 DTO（包含主键，排除系统字段）

- 排除的系统字段：
  - `createTime`, `updateTime`
  - `creatorId`, `updaterId`
  - `deleteTime`

- 生成 5 个接口方法：
  - `get${ptstn}List` - 分页查询
  - `get${ptstn}Details` - 查询详情
  - `add${ptstn}` - 新增
  - `edit${ptstn}` - 编辑
  - `remove${ptstn}` - 删除

### Service 层 (#{PTSTN}Service.ts.vm)
- 提供 2 个组合式函数：
  - `use${ptstn}List()` - 列表管理（加载、重置、删除）
  - `use${ptstn}Modal()` - **统一的模态框管理（新增+编辑）**

- **核心设计**：
  - 使用 `ModalMode` 类型区分 "add" | "edit" 模式
  - **`modalForm` 使用 `Get${ptstn}DetailsVo` 类型**（字段最全的 VO）
  - 接收 `FormInstance` 引用和 `reloadCallback` 回调
  - 内置表单验证规则 `FormRules`
  - 统一的 `openModal`, `resetModal`, `submitModal` 方法
  - **提交时严格按照 `Add${ptstn}Dto` 和 `Edit${ptstn}Dto` 定义提取字段**

- 功能特性：
  - 查询条件持久化（QueryPersistService）
  - Loading 状态管理
  - 统一错误处理
  - ElMessage 提示
  - 表单验证支持

### Vue 组件层 (#{PTSTN}.vue.vm)
- 包含完整的 CRUD 界面：
  - 查询表单（动态生成所有查询字段）
  - 分页表格（排除部分系统字段）
  - **统一的新增/编辑模态框**（根据 mode 动态显示）
  - 删除确认

- **模态框特性**：
  - 单一对话框支持新增和编辑两种模式
  - 动态标题（"新增${comment}" / "编辑${comment}"）
  - 动态按钮文字（"创建" / "保存"）
  - 表单验证支持（使用 `ref` 和 `FormInstance`）
  - 关闭时自动重置和刷新

- 表格显示规则：
  - 排除 `deleteTime`, `creatorId`, `updaterId`
  - 保留其他所有字段

## 使用方法

1. **配置数据库连接**：修改 `CodeGeneratorVue.java` 中的数据库连接信息

2. **选择要生成的表**：
```java
factory.selectTables("core_notice", "core_notice_rcd", "core_notice_template");
```

3. **设置表名前缀**（会被自动移除）：
```java
factory.removeTablePrefixes("tb_", "core_", "sys_", "pd_");
```

4. **执行生成**：运行 `CodeGeneratorVue.main()`

5. **生成位置**：
```
src/main/resources/views/webconsole/src/views/{tableName}/
├── api/{TableName}Api.ts
├── service/{TableName}Service.ts
└── {TableName}.vue
```

## 类型映射

MySQL 类型会通过 `MysqlToTypeScriptPolyConverter` 转换为 TypeScript 类型：

| MySQL 类型 | TypeScript 类型 |
|-----------|----------------|
| VARCHAR, CHAR, TEXT | string |
| INT, BIGINT, DECIMAL | number |
| DATETIME, DATE, TIMESTAMP | string |
| TINYINT(1) | number |
| BIT, BOOLEAN | boolean |

## 设计模式亮点

### 1. 统一的模态框模式
- **旧版本**：分离的 `useAddExample()` 和 `useEditExample()`，两个独立对话框
- **新版本**：统一的 `useExampleModal()`，单一模态框支持两种模式
- **优势**：代码更简洁，维护更容易，用户体验更一致

### 2. 严格的 DTO 类型定义
- **核心原则**：完全按照后端 5 个 DTO 严格定义
- **modalForm 类型**：使用 `GetExampleDetailsVo`（字段最全的详情 VO）
- **提交数据**：严格按照 `AddExampleDto` 和 `EditExampleDto` 定义提取字段
- **优势**：
  - 避免类型不一致问题
  - 减少自定义接口定义
  - 字段对齐更准确
  - 与后端 API 完全同步

### 3. FormInstance 引用传递
- 通过参数传递 `modalFormRef: Ref<FormInstance | undefined>`
- Service 层可以直接调用表单验证和重置方法
- 解耦了表单 ref 的管理逻辑

### 4. 回调函数模式
- `reloadCallback: () => void` 参数传递刷新逻辑
- Service 层不直接依赖具体的 `loadList` 方法
- 更好的可测试性和可复用性

## 代码规范

生成的代码遵循以下规范：
- ✅ 短路优先（if-return）
- ✅ 禁止 switch/else
- ✅ TypeScript 强类型
- ✅ Vue 3 Composition API
- ✅ 统一的错误处理
- ✅ 查询条件持久化
- ✅ Element Plus 表单验证

## 注意事项

1. 生成后需要手动调整：
   - 查询表单中可能不需要所有字段，请根据业务需求删减
   - 表格列显示顺序可能需要调整
   - 对话框表单可能需要更换输入组件（如 select、date-picker 等）
   - 添加字段验证规则

2. 生成的文件会覆盖同名文件，请谨慎操作

3. 生成后的代码位于 `src/views/{moduleName}/` 下，需要手动移动到对应的业务域目录

4. 记得注册路由：
```typescript
{
  path: '/{moduleName}',
  component: () => import('@/views/{moduleName}/{ModuleName}.vue')
}
```
