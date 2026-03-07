<template>
  <el-dialog v-model="modalVisible" :title="title" width="1200px" :close-on-click-modal="false" @close="onClose">
    <el-tabs v-model="activeTab" type="border-card" style="height: 600px">
      <!-- 原始模型 TAB -->
      <el-tab-pane label="原始模型" name="origin" style="height: 100%; display: flex; flex-direction: column">
        <div style="display: flex; flex-direction: column; height: 540px">
          <StdListAreaAction class="flex gap-2">
            <el-button type="success" @click="openOriginModal('add', null)">新增原始字段</el-button>
          </StdListAreaAction>

          <StdListAreaTable style="flex: 1">
            <el-table :data="originListData" stripe v-loading="originListLoading" border height="100%">
              <el-table-column prop="name" label="原始字段名" min-width="150" show-overflow-tooltip />
              <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
              <el-table-column prop="length" label="长度" min-width="80" show-overflow-tooltip />
              <el-table-column prop="require" label="必填" min-width="70" show-overflow-tooltip />
              <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
              <el-table-column prop="seq" label="排序" min-width="70" show-overflow-tooltip />
              <el-table-column label="操作" fixed="right" min-width="120" align="center">
                <template #default="scope">
                  <el-button link type="primary" size="small" @click="openOriginModal('edit', scope.row)" :icon="EditIcon">
                    编辑
                  </el-button>
                  <el-button link type="danger" size="small" @click="removeOriginList(scope.row)" :icon="DeleteIcon">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <template #pagination>
              <el-pagination
                v-model:current-page="originListForm.pageNum"
                v-model:page-size="originListForm.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="originListTotal"
                @size-change="
                  (val: number) => {
                    originListForm.pageSize = val;
                    loadOriginList();
                  }
                "
                @current-change="
                  (val: number) => {
                    originListForm.pageNum = val;
                    loadOriginList();
                  }
                "
                background
              />
            </template>
          </StdListAreaTable>
        </div>
      </el-tab-pane>

      <!-- 聚合模型 TAB -->
      <el-tab-pane label="聚合模型" name="poly" style="height: 100%; display: flex; flex-direction: column">
        <div style="display: flex; flex-direction: column; height: 540px">
          <StdListAreaAction class="flex gap-2">
            <el-button type="success" @click="openPolyModal('add', null)">新增聚合字段</el-button>
          </StdListAreaAction>

          <StdListAreaTable style="flex: 1">
            <el-table :data="polyListData" stripe v-loading="polyListLoading" border height="100%">
              <el-table-column prop="name" label="聚合字段名" min-width="150" show-overflow-tooltip />
              <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
              <el-table-column prop="length" label="长度" min-width="80" show-overflow-tooltip />
              <el-table-column prop="require" label="必填" min-width="70" show-overflow-tooltip />
              <el-table-column prop="policyCrudJson" label="可见性策略" min-width="150" show-overflow-tooltip />
              <el-table-column prop="policyQuery" label="查询策略" min-width="80" show-overflow-tooltip />
              <el-table-column prop="policyView" label="显示策略" min-width="80" show-overflow-tooltip />
              <el-table-column prop="placeholder" label="placeholder" min-width="120" show-overflow-tooltip />
              <el-table-column prop="seq" label="排序" min-width="70" show-overflow-tooltip />
              <el-table-column label="操作" fixed="right" min-width="120" align="center">
                <template #default="scope">
                  <el-button link type="primary" size="small" @click="openPolyModal('edit', scope.row)" :icon="EditIcon">
                    编辑
                  </el-button>
                  <el-button link type="danger" size="small" @click="removePolyList(scope.row)" :icon="DeleteIcon">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <template #pagination>
              <el-pagination
                v-model:current-page="polyListForm.pageNum"
                v-model:page-size="polyListForm.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="polyListTotal"
                @size-change="
                  (val: number) => {
                    polyListForm.pageSize = val;
                    loadPolyList();
                  }
                "
                @current-change="
                  (val: number) => {
                    polyListForm.pageNum = val;
                    loadPolyList();
                  }
                "
                background
              />
            </template>
          </StdListAreaTable>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 原始模型 新增/编辑模态框 -->
    <el-dialog
      v-model="originModalVisible"
      :title="originModalMode === 'edit' ? '编辑原始字段' : '新增原始字段'"
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetOriginModal();
        loadOriginList();
      "
    >
      <el-form
        v-if="originModalVisible"
        ref="originModalFormRef"
        :model="originModalForm"
        :rules="originModalRules"
        label-width="110px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="原始字段名" prop="name">
          <el-input v-model="originModalForm.name" placeholder="请输入原始字段名" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="原始数据类型" prop="kind">
          <el-input v-model="originModalForm.kind" placeholder="请输入原始数据类型" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="原始长度" prop="length">
          <el-input v-model="originModalForm.length" placeholder="请输入原始长度" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="原始必填" prop="require">
          <el-input v-model.number="originModalForm.require" placeholder="0:否 1:是" clearable />
        </el-form-item>
        <el-form-item label="原始备注" prop="remark">
          <el-input v-model="originModalForm.remark" placeholder="请输入原始备注" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="原始排序" prop="seq">
          <el-input v-model.number="originModalForm.seq" placeholder="请输入原始排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="originModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitOriginModal" :loading="originModalLoading">
            {{ originModalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 聚合模型 新增/编辑模态框 -->
    <el-dialog
      v-model="polyModalVisible"
      :title="polyModalMode === 'edit' ? '编辑聚合字段' : '新增聚合字段'"
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetPolyModal();
        loadPolyList();
      "
    >
      <el-form
        v-if="polyModalVisible"
        ref="polyModalFormRef"
        :model="polyModalForm"
        :rules="polyModalRules"
        label-width="130px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="原始字段ID" prop="outputModelOriginId">
          <el-input v-model="polyModalForm.outputModelOriginId" placeholder="请输入原始字段ID" clearable />
        </el-form-item>
        <el-form-item label="聚合字段名" prop="name">
          <el-input v-model="polyModalForm.name" placeholder="请输入聚合字段名" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合数据类型" prop="kind">
          <el-input v-model="polyModalForm.kind" placeholder="请输入聚合数据类型" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合长度" prop="length">
          <el-input v-model="polyModalForm.length" placeholder="请输入聚合长度" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合必填" prop="require">
          <el-input v-model.number="polyModalForm.require" placeholder="0:否 1:是" clearable />
        </el-form-item>
        <el-form-item label="可见性策略" prop="policyCrudJson">
          <el-input v-model="polyModalForm.policyCrudJson" placeholder="ADD、EDIT、LIST_QUERY、LIST_VIEW" clearable />
        </el-form-item>
        <el-form-item label="查询策略" prop="policyQuery">
          <el-input v-model.number="polyModalForm.policyQuery" placeholder="0:等于 1:模糊" clearable />
        </el-form-item>
        <el-form-item label="显示策略" prop="policyView">
          <el-input v-model.number="polyModalForm.policyView" placeholder="0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT" clearable />
        </el-form-item>
        <el-form-item label="placeholder" prop="placeholder">
          <el-input v-model="polyModalForm.placeholder" placeholder="请输入placeholder" clearable maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合排序" prop="seq">
          <el-input v-model.number="polyModalForm.seq" placeholder="请输入聚合排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="polyModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPolyModal" :loading="polyModalLoading">
            {{ polyModalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, markRaw, computed } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage, ElMessageBox } from "element-plus";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import OutModelOriginApi from "@/views/gen/api/OutModelOriginApi";
import OutModelPolyApi from "@/views/gen/api/OutModelPolyApi";
import { Result } from "@/commons/entity/Result";
import type {
  GetOutModelOriginListVo,
  GetOutModelOriginDetailsVo,
  AddOutModelOriginDto,
  EditOutModelOriginDto,
} from "@/views/gen/api/OutModelOriginApi";
import type {
  GetOutModelPolyListVo,
  GetOutModelPolyDetailsVo,
  AddOutModelPolyDto,
  EditOutModelPolyDto,
} from "@/views/gen/api/OutModelPolyApi";
import type { GetOutSchemaListVo } from "@/views/gen/api/OutSchemaApi";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

type ModalMode = "add" | "edit";

const modalVisible = ref(false);
const activeTab = ref("origin");
const outputSchemaId = ref("");
const outSchemaVo = ref<GetOutSchemaListVo | null>(null);

const emit = defineEmits<{
  (e: "on-close"): void;
}>();

const title = computed(() => {
  if (outSchemaVo.value) {
    return `模型设计: ${outSchemaVo.value.name}（${outSchemaVo.value.modelName}）`;
  }
  return "模型设计";
});

// ==================== 原始模型列表 ====================

const originListForm = ref({
  pageNum: 1,
  pageSize: 20,
  id: "",
  outputSchemaId: "",
  name: "",
  kind: "",
  length: "",
  require: null as number | null,
  remark: "",
  seq: null as number | null,
});

const originListData = ref<GetOutModelOriginListVo[]>([]);
const originListTotal = ref(0);
const originListLoading = ref(false);

const loadOriginList = async () => {
  originListLoading.value = true;
  const result = await OutModelOriginApi.getOutModelOriginList(originListForm.value);

  if (Result.isSuccess(result)) {
    originListData.value = result.data;
    originListTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  originListLoading.value = false;
};

const removeOriginList = async (row: GetOutModelOriginListVo) => {
  try {
    await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await OutModelOriginApi.removeOutModelOrigin({ id: row.id });
    ElMessage.success("删除成功");
    await loadOriginList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

// ==================== 原始模型模态框 ====================

const originModalVisible = ref(false);
const originModalLoading = ref(false);
const originModalMode = ref<ModalMode>("add");
const originModalFormRef = ref<FormInstance>();
const originModalForm = reactive<GetOutModelOriginDetailsVo>({
  id: "",
  outputSchemaId: "",
  name: "",
  kind: "",
  length: "",
  require: 0,
  remark: "",
  seq: 0,
});

const originModalRules: FormRules = {
  name: [
    { required: true, message: "请输入原始字段名", trigger: "blur" },
    { max: 255, message: "原始字段名长度不能超过255", trigger: "blur" },
  ],
  kind: [
    { required: true, message: "请输入原始数据类型", trigger: "blur" },
    { max: 255, message: "原始数据类型长度不能超过255", trigger: "blur" },
  ],
  require: [{ required: true, message: "请输入原始必填 0:否 1:是", trigger: "blur" }],
  seq: [{ required: true, message: "请输入原始排序", trigger: "blur" }],
};

const openOriginModal = async (mode: ModalMode, row: GetOutModelOriginListVo | null) => {
  originModalMode.value = mode;

  if (mode === "add") {
    originModalForm.id = "";
    originModalForm.outputSchemaId = outputSchemaId.value;
    originModalForm.name = "";
    originModalForm.kind = "";
    originModalForm.length = "";
    originModalForm.require = 0;
    originModalForm.remark = "";
    originModalForm.seq = 0;
    originModalVisible.value = true;
    return;
  }

  if (!row) {
    ElMessage.error("未选择要编辑的数据");
    return;
  }

  try {
    const details = await OutModelOriginApi.getOutModelOriginDetails({ id: row.id });
    originModalForm.id = details.id;
    originModalForm.outputSchemaId = details.outputSchemaId;
    originModalForm.name = details.name;
    originModalForm.kind = details.kind;
    originModalForm.length = details.length;
    originModalForm.require = details.require;
    originModalForm.remark = details.remark;
    originModalForm.seq = details.seq;
    originModalVisible.value = true;
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const resetOriginModal = () => {
  if (!originModalFormRef.value) {
    return;
  }
  originModalFormRef.value.resetFields();
  originModalForm.id = "";
  originModalForm.outputSchemaId = "";
  originModalForm.name = "";
  originModalForm.kind = "";
  originModalForm.length = "";
  originModalForm.require = 0;
  originModalForm.remark = "";
  originModalForm.seq = 0;
};

const submitOriginModal = async () => {
  if (!originModalFormRef.value) {
    return;
  }

  try {
    await originModalFormRef.value.validate();
  } catch (error) {
    return;
  }

  originModalLoading.value = true;

  if (originModalMode.value === "add") {
    try {
      const addDto: AddOutModelOriginDto = {
        outputSchemaId: originModalForm.outputSchemaId,
        name: originModalForm.name,
        kind: originModalForm.kind,
        length: originModalForm.length,
        require: originModalForm.require,
        remark: originModalForm.remark,
        seq: originModalForm.seq,
      };
      await OutModelOriginApi.addOutModelOrigin(addDto);
      ElMessage.success("新增成功");
      originModalVisible.value = false;
      resetOriginModal();
      loadOriginList();
    } catch (error: any) {
      ElMessage.error(error.message);
    }
    originModalLoading.value = false;
    return;
  }

  if (!originModalForm.id) {
    ElMessage.error("缺少ID参数");
    originModalLoading.value = false;
    return;
  }

  try {
    const editDto: EditOutModelOriginDto = {
      id: originModalForm.id,
      outputSchemaId: originModalForm.outputSchemaId,
      name: originModalForm.name,
      kind: originModalForm.kind,
      length: originModalForm.length,
      require: originModalForm.require,
      remark: originModalForm.remark,
      seq: originModalForm.seq,
    };
    await OutModelOriginApi.editOutModelOrigin(editDto);
    ElMessage.success("编辑成功");
    originModalVisible.value = false;
    resetOriginModal();
    loadOriginList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
  originModalLoading.value = false;
};

// ==================== 聚合模型列表 ====================

const polyListForm = ref({
  pageNum: 1,
  pageSize: 20,
  id: "",
  outputSchemaId: "",
  outputModelOriginId: "",
  name: "",
  kind: "",
  length: "",
  require: null as number | null,
  policyCrudJson: "",
  policyQuery: null as number | null,
  policyView: null as number | null,
  placeholder: "",
  seq: null as number | null,
});

const polyListData = ref<GetOutModelPolyListVo[]>([]);
const polyListTotal = ref(0);
const polyListLoading = ref(false);

const loadPolyList = async () => {
  polyListLoading.value = true;
  const result = await OutModelPolyApi.getOutModelPolyList(polyListForm.value);

  if (Result.isSuccess(result)) {
    polyListData.value = result.data;
    polyListTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  polyListLoading.value = false;
};

const removePolyList = async (row: GetOutModelPolyListVo) => {
  try {
    await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    await OutModelPolyApi.removeOutModelPoly({ id: row.id });
    ElMessage.success("删除成功");
    await loadPolyList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

// ==================== 聚合模型模态框 ====================

const polyModalVisible = ref(false);
const polyModalLoading = ref(false);
const polyModalMode = ref<ModalMode>("add");
const polyModalFormRef = ref<FormInstance>();
const polyModalForm = reactive<GetOutModelPolyDetailsVo>({
  id: "",
  outputSchemaId: "",
  outputModelOriginId: "",
  name: "",
  kind: "",
  length: "",
  require: 0,
  policyCrudJson: "",
  policyQuery: 0,
  policyView: 0,
  placeholder: "",
  seq: 0,
});

const polyModalRules: FormRules = {
  outputModelOriginId: [{ required: true, message: "请输入原始字段ID", trigger: "blur" }],
  name: [{ required: true, message: "请输入聚合字段名", trigger: "blur" }],
  kind: [{ required: true, message: "请输入聚合数据类型", trigger: "blur" }],
  require: [{ required: true, message: "请输入聚合必填 0:否 1:是", trigger: "blur" }],
  policyCrudJson: [{ required: true, message: "请输入聚合可见性策略", trigger: "blur" }],
  policyQuery: [{ required: true, message: "请输入聚合查询策略", trigger: "blur" }],
  policyView: [{ required: true, message: "请输入聚合显示策略", trigger: "blur" }],
  placeholder: [{ required: true, message: "请输入placeholder", trigger: "blur" }],
  seq: [{ required: true, message: "请输入聚合排序", trigger: "blur" }],
};

const openPolyModal = async (mode: ModalMode, row: GetOutModelPolyListVo | null) => {
  polyModalMode.value = mode;

  if (mode === "add") {
    polyModalForm.id = "";
    polyModalForm.outputSchemaId = outputSchemaId.value;
    polyModalForm.outputModelOriginId = "";
    polyModalForm.name = "";
    polyModalForm.kind = "";
    polyModalForm.length = "";
    polyModalForm.require = 0;
    polyModalForm.policyCrudJson = "";
    polyModalForm.policyQuery = 0;
    polyModalForm.policyView = 0;
    polyModalForm.placeholder = "";
    polyModalForm.seq = 0;
    polyModalVisible.value = true;
    return;
  }

  if (!row) {
    ElMessage.error("未选择要编辑的数据");
    return;
  }

  try {
    const details = await OutModelPolyApi.getOutModelPolyDetails({ id: row.id });
    polyModalForm.id = details.id;
    polyModalForm.outputSchemaId = details.outputSchemaId;
    polyModalForm.outputModelOriginId = details.outputModelOriginId;
    polyModalForm.name = details.name;
    polyModalForm.kind = details.kind;
    polyModalForm.length = details.length;
    polyModalForm.require = details.require;
    polyModalForm.policyCrudJson = details.policyCrudJson;
    polyModalForm.policyQuery = details.policyQuery;
    polyModalForm.policyView = details.policyView;
    polyModalForm.placeholder = details.placeholder;
    polyModalForm.seq = details.seq;
    polyModalVisible.value = true;
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const resetPolyModal = () => {
  if (!polyModalFormRef.value) {
    return;
  }
  polyModalFormRef.value.resetFields();
  polyModalForm.id = "";
  polyModalForm.outputSchemaId = "";
  polyModalForm.outputModelOriginId = "";
  polyModalForm.name = "";
  polyModalForm.kind = "";
  polyModalForm.length = "";
  polyModalForm.require = 0;
  polyModalForm.policyCrudJson = "";
  polyModalForm.policyQuery = 0;
  polyModalForm.policyView = 0;
  polyModalForm.placeholder = "";
  polyModalForm.seq = 0;
};

const submitPolyModal = async () => {
  if (!polyModalFormRef.value) {
    return;
  }

  try {
    await polyModalFormRef.value.validate();
  } catch (error) {
    return;
  }

  polyModalLoading.value = true;

  if (polyModalMode.value === "add") {
    try {
      const addDto: AddOutModelPolyDto = {
        outputSchemaId: polyModalForm.outputSchemaId,
        outputModelOriginId: polyModalForm.outputModelOriginId,
        name: polyModalForm.name,
        kind: polyModalForm.kind,
        length: polyModalForm.length,
        require: polyModalForm.require,
        policyCrudJson: polyModalForm.policyCrudJson,
        policyQuery: polyModalForm.policyQuery,
        policyView: polyModalForm.policyView,
        placeholder: polyModalForm.placeholder,
        seq: polyModalForm.seq,
      };
      await OutModelPolyApi.addOutModelPoly(addDto);
      ElMessage.success("新增成功");
      polyModalVisible.value = false;
      resetPolyModal();
      loadPolyList();
    } catch (error: any) {
      ElMessage.error(error.message);
    }
    polyModalLoading.value = false;
    return;
  }

  if (!polyModalForm.id) {
    ElMessage.error("缺少ID参数");
    polyModalLoading.value = false;
    return;
  }

  try {
    const editDto: EditOutModelPolyDto = {
      id: polyModalForm.id,
      outputSchemaId: polyModalForm.outputSchemaId,
      outputModelOriginId: polyModalForm.outputModelOriginId,
      name: polyModalForm.name,
      kind: polyModalForm.kind,
      length: polyModalForm.length,
      require: polyModalForm.require,
      policyCrudJson: polyModalForm.policyCrudJson,
      policyQuery: polyModalForm.policyQuery,
      policyView: polyModalForm.policyView,
      placeholder: polyModalForm.placeholder,
      seq: polyModalForm.seq,
    };
    await OutModelPolyApi.editOutModelPoly(editDto);
    ElMessage.success("编辑成功");
    polyModalVisible.value = false;
    resetPolyModal();
    loadPolyList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }
  polyModalLoading.value = false;
};

// ==================== 对外暴露 ====================

const onClose = () => {
  emit("on-close");
};

defineExpose({
  openModal: async (row: GetOutSchemaListVo) => {
    outSchemaVo.value = row;
    outputSchemaId.value = row.id;
    originListForm.value.outputSchemaId = row.id;
    polyListForm.value.outputSchemaId = row.id;
    activeTab.value = "origin";
    modalVisible.value = true;
    await Promise.all([loadOriginList(), loadPolyList()]);
  },
});
</script>

<style scoped></style>
