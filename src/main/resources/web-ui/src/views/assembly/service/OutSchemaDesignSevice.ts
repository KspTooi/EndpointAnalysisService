import { computed, onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetOutModelPolyListDto,
  GetOutModelPolyListVo,
  EditOutModelPolyDto,
  AddOutModelPolyDto,
} from "@/views/assembly/api/OutModelPolyApi";
import OutModelPolyApi from "@/views/assembly/api/OutModelPolyApi";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import OutModelOriginApi, {
  type GetOutModelOriginListDto,
  type GetOutModelOriginListVo,
} from "@/views/assembly/api/OutModelOriginApi";

export default {
  /**
   * 原始模型列表管理
   */
  useOutModelOriginList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetOutModelOriginListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetOutModelOriginListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async (): Promise<void> => {
      listLoading.value = true;
      const result = await OutModelOriginApi.getOutModelOriginList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    return {
      listData,
      listTotal,
      listLoading,
      loadList,
    };
  },

  /**
   * 聚合模型列表管理
   */
  useOutModelPolyList(outputSchemaId: Ref<string>) {
    const listForm = ref<GetOutModelPolyListDto>({
      outputSchemaId: outputSchemaId.value,
    });

    const listData = ref<GetOutModelPolyListVo[]>([]);
    const listLoading = ref(false);

    const loadList = async (): Promise<void> => {
      listLoading.value = false;
      listForm.value.outputSchemaId = outputSchemaId.value;
      const result = await OutModelPolyApi.getOutModelPolyList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    const syncFromOrigin = async (): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定从原始模型同步聚合模型吗？已有字段将被覆盖。", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await OutModelPolyApi.syncFromOriginBySchema({ id: outputSchemaId.value });
        ElMessage.success("同步成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    const removeList = async (row: GetOutModelPolyListVo): Promise<void> => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch {
        return;
      }

      try {
        await OutModelPolyApi.removeOutModelPoly({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    return {
      listData,
      listLoading,
      loadList,
      removeList,
      syncFromOrigin,
    };
  },

  /**
   * 表格行内编辑提交
   */
  useCellEdit() {
    const buildEditDto = (row: GetOutModelPolyListVo): EditOutModelPolyDto => ({
      id: row.id,
      outputSchemaId: row.outputSchemaId,
      outputModelOriginId: row.outputModelOriginId,
      name: row.name,
      kind: row.kind,
      length: row.length,
      require: row.require,
      policyCrudJson: row.policyCrudJson,
      policyQuery: row.policyQuery,
      policyView: row.policyView,
      remark: row.remark,
      seq: row.seq,
    });

    /**
     * 提交整行当前值到后端（文本类 input blur 时调用）
     */
    const submitRow = async (row: GetOutModelPolyListVo): Promise<boolean> => {
      const editDto = buildEditDto(row);

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        return true;
      } catch (error: any) {
        ElMessage.error(error.message);
        return false;
      }
    };

    /**
     * 提交指定字段新值（checkbox / select change 时调用）
     */
    const commitField = async (row: GetOutModelPolyListVo, field: string, newValue: any): Promise<boolean> => {
      const oldValue = (row as any)[field];
      if (JSON.stringify(newValue) === JSON.stringify(oldValue)) {
        return false;
      }

      (row as any)[field] = newValue;
      const editDto = buildEditDto(row);

      try {
        await OutModelPolyApi.editOutModelPoly(editDto);
        return true;
      } catch (error: any) {
        (row as any)[field] = oldValue;
        ElMessage.error(error.message);
        return false;
      }
    };

    return {
      submitRow,
      commitField,
    };
  },

  /**
   * 新增聚合字段模态框管理
   */
  usePolyAddModal(
    outputSchemaId: Ref<string>,
    modalFormRef: Ref<FormInstance | undefined>,
    reloadCallback: () => Promise<void>
  ) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const modalForm = reactive<AddOutModelPolyDto>({
      outputSchemaId: outputSchemaId.value,
      outputModelOriginId: "",
      name: "",
      kind: "",
      length: "",
      require: 0,
      policyCrudJson: [],
      policyQuery: 0,
      policyView: 0,
      remark: "",
      seq: 0,
    });

    const modalRequireChecked = computed({
      get: () => modalForm.require === 1,
      set: (val: boolean) => {
        modalForm.require = val ? 1 : 0;
      },
    });

    const modalRules: FormRules = {
      outputModelOriginId: [{ required: true, message: "请输入原始字段ID", trigger: "blur" }],
      name: [{ required: true, message: "请输入聚合字段名", trigger: "blur" }],
      kind: [{ required: true, message: "请输入聚合数据类型", trigger: "blur" }],
      require: [{ required: true, message: "请选择聚合必填", trigger: "change" }],
      policyCrudJson: [{ required: true, message: "请选择聚合可见性策略", trigger: "change" }],
      policyQuery: [{ required: true, message: "请选择聚合查询策略", trigger: "change" }],
      policyView: [{ required: true, message: "请选择聚合显示策略", trigger: "change" }],
      remark: [{ required: true, message: "请输入聚合字段备注", trigger: "blur" }],
      seq: [{ required: true, message: "请输入聚合排序", trigger: "blur" }],
    };

    const openModal = (): void => {
      modalForm.outputSchemaId = outputSchemaId.value;
      modalForm.outputModelOriginId = "";
      modalForm.name = "";
      modalForm.kind = "";
      modalForm.length = "";
      modalForm.require = 0;
      modalForm.policyCrudJson = [];
      modalForm.policyQuery = 0;
      modalForm.policyView = 0;
      modalForm.remark = "";
      modalForm.seq = 0;
      modalVisible.value = true;
    };

    const resetModal = (): void => {
      modalFormRef.value?.resetFields();
    };

    const submitModal = async (): Promise<void> => {
      if (!modalFormRef.value) {
        return;
      }

      try {
        await modalFormRef.value.validate();
      } catch {
        return;
      }

      modalLoading.value = true;

      try {
        await OutModelPolyApi.addOutModelPoly(modalForm);
        ElMessage.success("新增成功");
        modalVisible.value = false;
        await reloadCallback();
      } catch (error: any) {
        ElMessage.error(error.message);
      }

      modalLoading.value = false;
    };

    return {
      modalVisible,
      modalLoading,
      modalForm,
      modalRequireChecked,
      modalRules,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
