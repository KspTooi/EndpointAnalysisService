<!-- a6d3b073-c389-4e5a-83f0-96685ddc481c 8ede64e8-11ae-4c2a-a029-4a7e26a42151 -->
# 前端CRUD接口和Date类型同步改造

## 改造目标

1. **API接口升级（4个API文件）**

- UserApi: `saveUser` → `addUser` + `editUser`，返回类型改为 `Promise<Result<string>>`
- GroupApi: `saveGroup` → `addGroup` + `editGroup`，返回类型改为 `Promise<Result<string>>`
- PermissionApi: `savePermission` → `addPermission` + `editPermission`，返回类型改为 `Promise<Result<string>>`
- ConfigApi: `saveConfig` → `addConfig` + `editConfig`，返回类型改为 `Promise<Result<string>>`

2. **组件调用更新（4个Vue组件）**

- UserManager.vue: 按RouteServerManager.vue规范重构，使用`addUser`/`editUser`，使用`Result.isSuccess`/`Result.isError`处理响应
- UserGroupManager.vue: 按RouteServerManager.vue规范重构，使用`addGroup`/`editGroup`，使用`Result.isSuccess`/`Result.isError`处理响应
- PermissionManager.vue: 按RouteServerManager.vue规范重构，使用`addPermission`/`editPermission`，使用`Result.isSuccess`/`Result.isError`处理响应
- ConfigManager.vue: 按RouteServerManager.vue规范重构，使用`addConfig`/`editConfig`，使用`Result.isSuccess`/`Result.isError`处理响应

3. **编码规范要求（参考RouteServerManager.vue）**

- 使用 `modalMode` 变量（'add' | 'edit'）标识模式
- 使用 `modalForm`（reactive）存储表单数据
- 使用 `modalFormRef` 引用表单实例
- 使用 `modalLoading` 标识加载状态
- 使用 `modalVisible` 控制对话框显示
- `submitModal` 根据 `modalMode` 调用不同API
- 使用 `Result.isSuccess` 和 `Result.isError` 处理API响应
- 错误处理使用 try-catch，通过 ElMessage 显示
- 新增成功后调用 `resetModal()` 重置表单
- 使用 `openModal(mode, row)` 打开模态框

4. **Date类型处理**

- 前端通常直接接收字符串，后端LocalDateTime序列化后仍然是字符串，前端无需特殊处理

## 实施步骤

### 第一步：升级API接口定义

#### 1.1 UserApi.ts

- 创建`AddUserDto`接口（从SaveUserDto复制，去掉id字段）
- 创建`EditUserDto`接口（从SaveUserDto复制，id字段必填）
- 添加`addUser`方法（调用`/user/addUser`，返回`Promise<Result<string>>`）
- 添加`editUser`方法（调用`/user/editUser`，返回`Promise<Result<string>>`）
- 删除`saveUser`方法（不再需要）

#### 1.2 GroupApi.ts

- 创建`AddGroupDto`接口（从SaveGroupDto复制，去掉id字段）
- 创建`EditGroupDto`接口（从SaveGroupDto复制，id字段必填）
- 添加`addGroup`方法（调用`/group/addGroup`，返回`Promise<Result<string>>`）
- 添加`editGroup`方法（调用`/group/editGroup`，返回`Promise<Result<string>>`）
- 删除`saveGroup`方法（不再需要）

#### 1.3 PermissionApi.ts

- 创建`AddPermissionDto`接口（从SavePermissionDto复制，去掉id字段）
- 创建`EditPermissionDto`接口（从SavePermissionDto复制，id字段必填）
- 添加`addPermission`方法（调用`/permission/addPermission`，返回`Promise<Result<string>>`）
- 添加`editPermission`方法（调用`/permission/editPermission`，返回`Promise<Result<string>>`）
- 删除`savePermission`方法（不再需要）

#### 1.4 ConfigApi.ts

- 创建`AddConfigDto`接口（从SaveConfigDto复制，去掉id字段）
- 创建`EditConfigDto`接口（从SaveConfigDto复制，id字段必填）
- 添加`addConfig`方法（调用`/config/addConfig`，返回`Promise<Result<string>>`）
- 添加`editConfig`方法（调用`/config/editConfig`，返回`Promise<Result<string>>`）
- 删除`saveConfig`方法（不再需要）

### 第二步：更新组件调用

#### 2.1 UserManager.vue

- 重构为符合RouteServerManager.vue编码规范
- 将`formType`改为`modalMode`（'add' | 'edit'）
- 将`userForm`改为`modalForm`，`userFormRef`改为`modalFormRef`
- 将`submitLoading`改为`modalLoading`，`dialogVisible`改为`modalVisible`
- 修改`submitForm`方法为`submitModal`：根据`modalMode`调用`addUser`或`editUser`
- 使用`Result.isSuccess`和`Result.isError`处理API响应
- 新增成功后调用`resetModal()`重置表单
- 修改`handleAdd`和`handleEdit`为统一的`openModal(mode, row)`函数

#### 2.2 UserGroupManager.vue

- 重构为符合RouteServerManager.vue编码规范
- 将`formType`改为`modalMode`（'add' | 'edit'）
- 将`groupForm`改为`modalForm`，`groupFormRef`改为`modalFormRef`
- 将`submitLoading`改为`modalLoading`，`dialogVisible`改为`modalVisible`
- 修改`submitForm`方法为`submitModal`：根据`modalMode`调用`addGroup`或`editGroup`
- 使用`Result.isSuccess`和`Result.isError`处理API响应
- 新增成功后调用`resetModal()`重置表单
- 修改`handleAdd`和`handleEdit`为统一的`openModal(mode, row)`函数

#### 2.3 PermissionManager.vue

- 重构为符合RouteServerManager.vue编码规范
- 将`mode`改为`modalMode`（'add' | 'edit'），保持语义一致
- 将`details`改为`modalForm`，`formRef`改为`modalFormRef`
- 将`submitLoading`改为`modalLoading`，`dialogVisible`改为`modalVisible`
- 修改`savePermission`方法为`submitModal`：根据`modalMode`调用`addPermission`或`editPermission`
- 使用`Result.isSuccess`和`Result.isError`处理API响应
- 新增成功后调用`resetModal()`重置表单
- 修改`openInsertModal`和`openUpdateModal`为统一的`openModal(mode, row)`函数

#### 2.4 ConfigManager.vue

- 重构为符合RouteServerManager.vue编码规范
- 将`mode`改为`modalMode`（'add' | 'edit'），保持语义一致
- 将`details`改为`modalForm`，`formRef`改为`modalFormRef`
- 将`submitLoading`改为`modalLoading`，`dialogVisible`改为`modalVisible`
- 修改`saveConfig`方法为`submitModal`：根据`modalMode`调用`addConfig`或`editConfig`
- 使用`Result.isSuccess`和`Result.isError`处理API响应
- 新增成功后调用`resetModal()`重置表单
- 修改`openInsertModal`和`openUpdateModal`为统一的`openModal(mode, row)`函数

### 第三步：验证

- 确保所有API接口路径正确
- 确保所有DTO接口字段定义正确
- 确保组件中根据模式正确调用对应接口
- 确保所有组件符合RouteServerManager.vue的编码规范
- 确保使用`Result.isSuccess`和`Result.isError`处理API响应
- 确保错误处理统一使用try-catch和ElMessage

### To-dos

- [ ] 升级UserApi.ts：创建AddUserDto和EditUserDto接口，添加addUser和editUser方法（返回Result<string>）
- [ ] 升级GroupApi.ts：创建AddGroupDto和EditGroupDto接口，添加addGroup和editGroup方法（返回Result<string>）
- [ ] 升级PermissionApi.ts：创建AddPermissionDto和EditPermissionDto接口，添加addPermission和editPermission方法（返回Result<string>）
- [ ] 升级ConfigApi.ts：创建AddConfigDto和EditConfigDto接口，添加addConfig和editConfig方法（返回Result<string>）
- [ ] 重构UserManager.vue：按RouteServerManager.vue规范，使用modalMode/modalForm等命名，使用Result.isSuccess/Result.isError处理响应
- [ ] 重构UserGroupManager.vue：按RouteServerManager.vue规范，使用modalMode/modalForm等命名，使用Result.isSuccess/Result.isError处理响应
- [ ] 重构PermissionManager.vue：按RouteServerManager.vue规范，使用modalMode/modalForm等命名，使用Result.isSuccess/Result.isError处理响应
- [ ] 重构ConfigManager.vue：按RouteServerManager.vue规范，使用modalMode/modalForm等命名，使用Result.isSuccess/Result.isError处理响应