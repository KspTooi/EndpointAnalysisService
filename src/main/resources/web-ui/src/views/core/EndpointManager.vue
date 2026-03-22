<template>
  <StdListLayout show-persist-tip :has-tutorial="true">
    <!-- 文档说明 -->
    <template #tutorial>
      <el-alert type="info" :closable="false" size="small" class="mb-6 mt-4 py-1 px-2">
        <template #title>
          <div class="flex items-center gap-2">
            <el-icon><InfoFilled /></el-icon>
            <span class="font-bold text-sm">端点与权限配置指南</span>
            <el-button link type="primary" size="small" @click="helpVisible = true"> 端点鉴权系统是如何工作的?</el-button>
          </div>
        </template>
        <div class="text-sm leading-relaxed">
          <div><strong>端点</strong>：定义系统中的接口路径（如 /user/list），每个端点可以配置所需的访问权限</div>
          <div><strong>权限</strong>：定义权限代码（如 admin:user:view），用户拥有权限后才能访问配置了该权限的端点</div>
          <div class="mt-2">
            <strong>权限缺失指示器</strong>： <span class="text-green-500 font-bold">● 绿色</span>=权限完整
            <span class="ml-3 text-orange-500 font-bold">● 橙色</span>=部分缺失
            <span class="ml-3 text-red-500 font-bold">● 红色</span>=完全缺失
          </div>
          <div class="text-orange-500 mt-1">⚠️ 修改端点配置后，需要点击"清空权限数据缓存"按钮使配置立即生效</div>
        </div>
      </el-alert>
    </template>

    <template #query>
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="端点名称">
              <el-input v-model="listForm.name" placeholder="请输入端点名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="端点路径">
              <el-input v-model="listForm.path" placeholder="请输入端点路径" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1"> </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" :disabled="listLoading" @click="loadList">查询</el-button>
              <el-button :disabled="listLoading" @click="resetList">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </template>

    <template #actions>
      <el-button type="success" @click="openModal('add', null)">创建端点</el-button>
      <el-button type="success" @click="clearEndpointCache">清空权限数据缓存</el-button>
    </template>

    <template #table>
      <el-table v-loading="listLoading" :data="listData" border row-key="id" default-expand-all height="100%">
        <el-table-column type="index" label="序号" width="60" show-overflow-tooltip align="center" />
        <el-table-column label="端点名称" prop="name" />
        <el-table-column label="端点路径" prop="path" show-overflow-tooltip />
        <el-table-column label="所需权限" show-overflow-tooltip>
          <template #default="scope">
            <span
              :class="{
                'text-red-500': scope.row.missingPermission === 1,
                'text-orange-500': scope.row.missingPermission === 2,
                'text-green-500': scope.row.missingPermission === 0 && scope.row.permission !== '*',
              }"
            >
              {{ scope.row.permission }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="端点描述" prop="description" show-overflow-tooltip />
        <el-table-column label="排序" prop="seq" width="100">
          <template #default="scope">
            <ComSeqFixer
              :id="scope.row.id"
              :seq-field="'seq'"
              :get-detail-api="getEndpointDetail"
              :edit-api="editEndpointSeq"
              :display-value="scope.row.seq"
              :on-success="loadList"
            />
          </template>
        </el-table-column>
        <el-table-column label="已缓存" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.cached === 1 ? 'success' : 'info'">
              {{ scope.row.cached === 1 ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="230">
          <template #default="scope">
            <div class="inline-flex justify-end items-center gap-2 w-full">
              <el-button link type="success" size="small" :icon="PlusIcon" @click="openModal('add-item', scope.row)">
                新增子端点
              </el-button>
              <el-button link type="primary" size="small" :icon="ViewIcon" @click="openModal('edit', scope.row)">
                编辑
              </el-button>
              <el-button
                link
                type="danger"
                size="small"
                :icon="DeleteIcon"
                :disabled="scope.row.children && scope.row.children.length > 0"
                @click="removeList(scope.row.id)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </StdListLayout>

  <!-- 端点编辑模态框 -->
  <el-dialog
    v-model="modalVisible"
    :title="modalMode === 'edit' ? '编辑端点' : '添加端点'"
    width="550px"
    :close-on-click-modal="false"
    @close="
      resetModal(true);
      loadList();
    "
  >
    <el-form
      v-if="modalVisible"
      ref="modalFormRef"
      :model="modalForm"
      :rules="modalRules"
      label-width="100px"
      :validate-on-rule-change="false"
    >
      <el-form-item label="父级端点" prop="parentId">
        <el-tree-select
          v-model="modalForm.parentId"
          :data="endpointTreeForSelect"
          node-key="id"
          :props="{ value: 'id', label: 'name', children: 'children' }"
          check-strictly
          placeholder="请选择父级端点"
          clearable
          default-expand-all
        />
      </el-form-item>
      <el-form-item label="端点名称" prop="name">
        <el-input v-model="modalForm.name" placeholder="请输入端点名称" clearable />
      </el-form-item>
      <el-form-item prop="path">
        <template #label>
          <span>
            <el-tooltip
              class="box-item"
              effect="dark"
              content="支持AntPathMatcher路径匹配。? 匹配一个字符, * 匹配零个或多个字符, ** 匹配路径中的零个或多个目录。例如: /user/**"
              placement="top"
            >
              <el-icon class="align-middle ml-1"><InfoFilled /></el-icon> </el-tooltip>端点路径
          </span>
        </template>
        <el-input v-model="modalForm.path" placeholder="请输入端点路径" clearable />
      </el-form-item>
      <el-form-item label="所需权限" prop="permission">
        <el-input v-model="modalForm.permission" placeholder="请输入所需权限" clearable />
      </el-form-item>
      <el-form-item label="端点描述" prop="description">
        <el-input v-model="modalForm.description" placeholder="请输入端点描述" clearable />
      </el-form-item>
      <el-form-item label="排序" prop="seq">
        <el-input-number v-model.number="modalForm.seq" :min="0" :max="655350" placeholder="请输入排序" clearable />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modalVisible = false">关闭</el-button>
        <el-button type="primary" :loading="modalLoading" @click="submitModal">
          {{ modalMode === "add" ? "创建" : "保存" }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 帮助文档对话框 -->
  <el-dialog v-model="helpVisible" title="端点与权限配置详细说明" width="800px" :close-on-click-modal="false">
    <div class="help-content">
      <el-collapse v-model="activeHelp" accordion>
        <el-collapse-item name="1">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Document /></el-icon>
              一、什么是端点？
            </div>
          </template>
          <div class="help-section">
            <p><strong>端点</strong>是系统中定义的接口访问路径，用于标识一个具体的API接口。</p>
            <p><strong>示例：</strong></p>
            <ul>
              <li><code>/user/list</code> - 获取用户列表的接口</li>
              <li><code>/user/add</code> - 添加用户的接口</li>
              <li><code>/user/**</code> - 匹配所有以 /user/ 开头的接口</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item name="2">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Key /></el-icon>
              二、什么是权限？
            </div>
          </template>
          <div class="help-section">
            <p><strong>权限</strong>是一个权限代码字符串，用于标识用户可以执行的操作。</p>
            <p><strong>权限命名规范：</strong>通常采用"模块:资源:操作"的格式</p>
            <ul>
              <li><code>admin:user:view</code> - 管理员查看用户的权限</li>
              <li><code>admin:user:edit</code> - 管理员编辑用户的权限</li>
              <li><code>admin:user:delete</code> - 管理员删除用户的权限</li>
            </ul>
            <p class="mt-3"><strong>特殊权限：</strong></p>
            <ul>
              <li><code>*</code> - 表示该端点不需要权限验证，所有用户都可以访问</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item name="3">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Connection /></el-icon>
              三、端点与权限的关联关系
            </div>
          </template>
          <div class="help-section">
            <p><strong>关联流程：</strong></p>
            <ol class="leading-loose">
              <li><strong>创建端点</strong>：在本页面创建端点，配置接口路径和所需权限代码</li>
              <li><strong>创建权限</strong>：在"权限管理"页面创建权限，定义权限代码和描述</li>
              <li><strong>分配权限</strong>：在"组管理"页面将权限分配给用户组</li>
              <li><strong>用户访问</strong>：用户访问接口时，系统自动验证用户是否拥有该端点要求的权限</li>
            </ol>

            <div class="bg-gray-50 p-3 rounded mt-4">
              <p class="m-0 font-bold text-blue-500">📌 示例说明：</p>
              <p class="mt-2 mb-0">假设你要保护 <code>/user/delete</code> 接口，只允许管理员删除用户：</p>
              <ol class="mt-2 mb-0 leading-loose">
                <li>在<strong>端点管理</strong>创建端点：路径=/user/delete，权限=admin:user:delete</li>
                <li>在<strong>权限管理</strong>创建权限：代码=admin:user:delete，名称=删除用户</li>
                <li>在<strong>组管理</strong>将"admin:user:delete"权限分配给"管理员"组</li>
                <li>将用户加入"管理员"组，该用户就能访问 /user/delete 接口了</li>
              </ol>
            </div>
          </div>
        </el-collapse-item>

        <el-collapse-item name="4">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Setting /></el-icon>
              四、权限验证流程
            </div>
          </template>
          <div class="help-section">
            <p><strong>当用户访问接口时，系统按以下流程验证权限：</strong></p>
            <ol class="leading-loose">
              <li>系统接收到请求，获取请求路径（如 /user/delete）</li>
              <li>在端点缓存中查找匹配的端点配置</li>
              <li>获取该端点要求的权限代码</li>
              <li>检查用户的权限列表中是否包含该权限代码</li>
              <li>如果有权限则允许访问，否则返回403禁止访问</li>
            </ol>

            <div class="bg-orange-50 p-3 rounded mt-4">
              <p class="m-0 font-bold text-orange-500">⚠️ 特殊情况处理：</p>
              <ul class="mt-2 mb-0 leading-relaxed">
                <li>
                  <strong>端点未配置：</strong>如果接口路径没有配置端点，系统会根据配置项"endpoint.access.denied"决定是否允许访问
                </li>
                <li><strong>权限为*：</strong>如果端点的权限字段设置为"*"，表示该接口无需权限验证，所有用户都可以访问</li>
                <li><strong>多个匹配：</strong>如果多个端点都匹配请求路径，系统会选择最精确的匹配规则</li>
              </ul>
            </div>
          </div>
        </el-collapse-item>

        <el-collapse-item name="5">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Position /></el-icon>
              五、端点路径匹配规则
            </div>
          </template>
          <div class="help-section">
            <p><strong>端点路径支持 AntPathMatcher 通配符匹配：</strong></p>
            <ul class="leading-loose">
              <li><code>?</code> - 匹配单个字符，如 /user/? 可以匹配 /user/1 但不能匹配 /user/12</li>
              <li><code>*</code> - 匹配零个或多个字符，如 /user/* 可以匹配 /user/list 但不能匹配 /user/info/detail</li>
              <li><code>**</code> - 匹配零个或多个目录，如 /user/** 可以匹配 /user/ 下的所有路径</li>
            </ul>

            <p class="mt-3"><strong>匹配优先级：</strong></p>
            <p>当多个端点都匹配请求路径时，系统选择最精确的匹配</p>
            <ul class="leading-relaxed">
              <li><code>/user/delete</code> 优先于 <code>/user/*</code></li>
              <li><code>/user/*</code> 优先于 <code>/user/**</code></li>
              <li><code>/**</code> 优先级最低</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item name="6">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><Refresh /></el-icon>
              六、为什么需要清空缓存？
            </div>
          </template>
          <div class="help-section">
            <p><strong>缓存机制：</strong>为了提高性能，系统会将端点配置缓存到内存中</p>

            <p class="mt-3"><strong>需要清空缓存的情况：</strong></p>
            <ul class="leading-relaxed">
              <li>修改了端点的路径或权限配置</li>
              <li>新增或删除了端点</li>
              <li>发现权限验证结果不符合预期</li>
              <li>怀疑缓存数据与数据库不一致</li>
            </ul>

            <p class="mt-3"><strong>缓存状态：</strong></p>
            <p>列表中的"已缓存"列显示该端点是否在内存缓存中：</p>
            <ul class="leading-relaxed">
              <li><el-tag type="success" size="small">是</el-tag> - 该端点已缓存，当前正在使用</li>
              <li><el-tag type="info" size="small">否</el-tag> - 该端点未缓存，需要清空缓存后才会生效</li>
            </ul>
          </div>
        </el-collapse-item>

        <el-collapse-item name="7">
          <template #title>
            <div class="text-base font-bold">
              <el-icon class="mr-2"><QuestionFilled /></el-icon>
              七、常见问题
            </div>
          </template>
          <div class="help-section">
            <div class="qa-item">
              <p class="question">Q1: 我创建了端点，为什么还是无法访问？</p>
              <p class="answer">A: 请检查以下几点：</p>
              <ul class="answer">
                <li>端点配置的路径是否与实际接口路径匹配</li>
                <li>是否在"权限管理"中创建了对应的权限</li>
                <li>是否在"组管理"中将权限分配给了用户所在的组</li>
                <li>是否点击了"清空权限数据缓存"按钮</li>
              </ul>
            </div>

            <div class="qa-item">
              <p class="question">Q2: 端点和权限的代码需要完全一致吗？</p>
              <p class="answer">A: 是的。端点配置的"所需权限"字段必须与"权限管理"中的权限代码完全一致。</p>
            </div>

            <div class="qa-item">
              <p class="question">Q3: 如何配置不需要权限验证的接口？</p>
              <p class="answer">A: 将端点的"所需权限"字段设置为 * 即可。</p>
            </div>

            <div class="qa-item">
              <p class="question">Q4: 清空缓存会影响系统运行吗？</p>
              <p class="answer">
                A: 清空缓存后，系统会在下次请求时重新从数据库加载端点配置，可能会有轻微的性能影响，但不会影响系统正常运行。
              </p>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <template #footer>
      <el-button type="primary" @click="helpVisible = false">我知道了</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { GetEndpointDetailsVo, GetEndpointTreeDto, GetEndpointTreeVo } from "@/views/core/api/EndpointApi.ts";
import EndpointApi from "@/views/core/api/EndpointApi.ts";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, computed, onMounted } from "vue";
import {
  Delete as DeleteIcon,
  View as ViewIcon,
  Plus as PlusIcon,
  InfoFilled,
  Document,
  Key,
  Connection,
  Setting,
  Position,
  Refresh,
  QuestionFilled,
} from "@element-plus/icons-vue";
import QueryPersistService from "@/commons/service/QueryPersistService.ts";
import ComSeqFixer from "@/soa/com-series/ComSeqFixer.vue";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";

const listForm = reactive<GetEndpointTreeDto>({
  name: "",
  path: "",
});

const listData = ref<GetEndpointTreeVo[]>([]);
const listLoading = ref(false);
// 用于父级选择的完整端点树
const fullEndpointTree = ref<GetEndpointTreeVo[]>([]);

const endpointTreeForSelect = computed(() => {
  const currentEndpoint = modalForm;
  const isEditMode = modalMode.value === "edit";

  const filter = (endpointTree: GetEndpointTreeVo[]): GetEndpointTreeVo[] => {
    return endpointTree.map((item) => {
      let disabled = false;

      // 编辑时，节点自身不能作为父级
      if (isEditMode && item.id === currentEndpoint.id) {
        disabled = true;
      }

      return {
        ...item,
        disabled,
        children: item.children ? filter(item.children) : [],
      };
    });
  };

  return [
    {
      id: null,
      name: "根节点",
      disabled: false,
      children: filter(fullEndpointTree.value),
    },
  ];
});

// 加载完整端点树用于父级选择
const loadFullEndpointTree = async () => {
  const result = await EndpointApi.getEndpointTree({});
  if (Result.isSuccess(result)) {
    fullEndpointTree.value = result.data;
  }
};

const loadList = async () => {
  listLoading.value = true;
  const result = await EndpointApi.getEndpointTree(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    QueryPersistService.persistQuery("endpoint-manager", listForm);
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const resetList = () => {
  listForm.name = "";
  listForm.path = "";
  QueryPersistService.clearQuery("endpoint-manager");
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该端点吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await EndpointApi.removeEndpoint({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
      await loadList();
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const clearEndpointCache = async () => {
  try {
    await ElMessageBox.confirm(
      "<div style='text-align: left;'>" +
        "<div style='margin-bottom: 12px;'><strong>清空缓存会发生什么？</strong></div>" +
        "<div style='margin-bottom: 8px;'>1. 如果你刚修改了端点配置，清空缓存后修改会马上生效</div>" +
        "<div style='margin-bottom: 16px;'>2. 系统会立即按最新的端点配置来判断用户能否访问某个接口</div>" +
        "<div style='margin-bottom: 12px;'><strong>什么时候需要清空缓存？</strong></div>" +
        "<div style='margin-bottom: 6px;'>✓ 修改了端点的路径或权限，希望立即生效</div>" +
        "<div style='margin-bottom: 6px;'>✓ 发现权限验证不正常，需要排查问题</div>" +
        "<div style='margin-bottom: 16px;'>✓ 怀疑缓存数据和数据库不一致</div>" +
        "<div style='margin-top: 16px; color: #E6A23C;'><strong>⚠️ 确定要清空权限缓存吗？</strong></div>" +
        "</div>",
      "⚠️ 清空权限数据缓存 ",
      {
        confirmButtonText: "确认清空",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        autofocus: false,
      }
    );
  } catch (error) {
    return;
  }

  const result = await EndpointApi.clearEndpointCache();
  if (Result.isSuccess(result)) {
    ElMessage.success("权限数据缓存已清空");
    await loadList();
  }
};

onMounted(async () => {
  QueryPersistService.loadQuery("endpoint-manager", listForm);
  await loadList();
  await loadFullEndpointTree();
});

//帮助文档
const helpVisible = ref(false);
const activeHelp = ref("1");

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit" | "add-item">("add"); //add:添加,edit:编辑,add-item:新增子项
const modalCurrentRow = ref<GetEndpointTreeVo | null>(null);
const modalForm = reactive<GetEndpointDetailsVo>({
  id: "",
  parentId: "",
  name: "",
  description: "",
  path: "",
  permission: "",
  seq: 0,
});

const modalRules = {
  name: [
    { required: true, message: "请输入端点名称", trigger: "blur" },
    { min: 2, max: 128, message: "端点名称长度必须在2-128个字符之间", trigger: "blur" },
  ],
  path: [
    { required: true, message: "请输入端点路径", trigger: "blur" },
    { max: 256, message: "端点路径长度不能超过256个字符", trigger: "blur" },
  ],
  permission: [{ max: 320, message: "所需权限长度不能超过320个字符", trigger: "blur" }],
  description: [{ max: 200, message: "端点描述长度不能超过200个字符", trigger: "blur" }],
  seq: [
    { required: true, message: "请输入排序", trigger: "blur" },
    { type: "number", min: 0, max: 655350, message: "排序只能在0-655350之间", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit" | "add-item", currentRow: GetEndpointTreeVo | null) => {
  modalMode.value = mode;
  modalCurrentRow.value = currentRow;
  resetModal();

  if (mode === "add") {
    modalForm.parentId = null;
  }

  if (mode === "add-item" && currentRow) {
    modalForm.parentId = currentRow.id;
  }

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && currentRow) {
    const ret = await EndpointApi.getEndpointDetails({ id: currentRow.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.parentId = ret.data.parentId;
      modalForm.name = ret.data.name;
      modalForm.description = ret.data.description;
      modalForm.path = ret.data.path;
      modalForm.permission = ret.data.permission;
      modalForm.seq = ret.data.seq;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  }

  modalVisible.value = true;
};

/**
 * 重置模态框表单
 * @param force 硬重置，不保留父级ID
 */
const resetModal = (force: boolean = false) => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.description = "";
  //modalForm.path = "";
  modalForm.permission = "";
  modalForm.seq = 0;

  if (force) {
    modalForm.parentId = null;
    modalForm.path = "";
  }
};

const submitModal = async () => {
  //先校验表单
  try {
    await modalFormRef?.value?.validate();
  } catch (error) {
    return;
  }

  modalLoading.value = true;

  //提交表单
  try {
    if (modalMode.value === "add" || modalMode.value === "add-item") {
      await EndpointApi.addEndpoint(modalForm);
      ElMessage.success("操作成功");
      const parentId = modalForm.parentId;
      resetModal();
      modalForm.parentId = parentId;
    }

    if (modalMode.value === "edit") {
      await EndpointApi.editEndpoint(modalForm);
      ElMessage.success("操作成功");
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  } finally {
    modalLoading.value = false;
  }

  await loadList();
};

const getEndpointDetail = async (id: string) => {
  const result = await EndpointApi.getEndpointDetails({ id });
  if (!Result.isSuccess(result)) {
    throw new Error(result.message);
  }
  return result.data;
};

const editEndpointSeq = async (id: string, dto: any) => {
  const result = await EndpointApi.editEndpoint(dto);
  if (!Result.isSuccess(result)) {
    throw new Error(result.message);
  }
};
</script>

<style scoped>
.help-content {
  max-height: 600px;
  overflow-y: auto;
}

.help-section {
  padding: 12px;
  line-height: 1.8;
}

.help-section p {
  margin: 8px 0;
}

.help-section ul,
.help-section ol {
  margin: 8px 0;
  padding-left: 24px;
}

.help-section li {
  margin: 4px 0;
}

.help-section code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  color: #e6a23c;
  font-family: "Courier New", monospace;
}

.qa-item {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #dcdfe6;
}

.qa-item:last-child {
  border-bottom: none;
}

.qa-item .question {
  font-weight: bold;
  color: #409eff;
  margin: 0 0 8px 0;
}

.qa-item .answer {
  margin: 4px 0;
  color: #606266;
}
</style>
