import { ref, type Ref } from "vue";
import RegistryApi, {
  type GetRegistryListDto,
  type GetRegistryEntryListVo,
  type AddRegistryDto,
  type EditRegistryDto,
} from "@/views/core/api/RegistryApi";
import type CommonIdDto from "@/commons/entity/CommonIdDto";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";

export default class RegistryService {
  /**
   * 注册表条目列表逻辑
   * @param keyPath 当前选中的节点路径
   */
  public static useRegistryList(keyPath: Ref<string | null>) {
    const listData = ref<GetRegistryEntryListVo[]>([]);
    const listLoading = ref(false);
    const listForm = ref<GetRegistryListDto>({
      keyPath: "",
      pageNum: 1,
      pageSize: 20,
    });

    const loadList = async (path?: string | null) => {
      const targetPath = path ?? keyPath.value;
      if (!targetPath) {
        listData.value = [];
        return;
      }
      listLoading.value = true;
      listForm.value.keyPath = targetPath;
      try {
        const data = await RegistryApi.getRegistryEntryList(listForm.value);
        listData.value = data;
      } catch (error: any) {
        ElMessage.error(error.message || "加载注册表条目失败");
      } finally {
        listLoading.value = false;
      }
    };

    const resetList = (path?: string | null) => {
      listForm.value = {
        keyPath: path ?? keyPath.value ?? "",
        pageNum: 1,
        pageSize: 20,
      };
      loadList(path);
    };

    const removeList = async (row: GetRegistryEntryListVo) => {
      try {
        await ElMessageBox.confirm(`确定要删除条目 [${row.nkey}] 吗？`, "提示", {
          type: "warning",
          confirmButtonText: "确定",
          cancelButtonText: "取消",
        });
        const dto: CommonIdDto = { id: row.id };
        await RegistryApi.removeRegistry(dto);
        ElMessage.success("删除成功");
        loadList();
      } catch (error: any) {
        if (error === "cancel") return;
        ElMessage.error(error.message || "删除失败");
      }
    };

    return {
      listForm,
      listData,
      listLoading,
      loadList,
      resetList,
      removeList,
    };
  }

  /**
   * 注册表模态框逻辑
   */
  public static useRegistryModal(formRef: Ref<FormInstance | undefined>, onRefresh: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<"add" | "edit">("add");
    const modalForm = ref<AddRegistryDto | EditRegistryDto | any>({});

    const modalRules = {
      nkey: [{ required: true, message: "请输入节点Key", trigger: "blur" }],
      nvalue: [{ required: true, message: "请输入节点值", trigger: "blur" }],
      kind: [{ required: true, message: "请选择类型", trigger: "change" }],
    };

    const openModal = (mode: "add" | "edit", row: any = null, parentId: string | null = null) => {
      modalMode.value = mode;
      modalVisible.value = true;
      if (mode === "add") {
        modalForm.value = {
          parentId: parentId ?? undefined,
          kind: 1,
          nkey: "",
          nvalueKind: 0,
          nvalue: "",
          label: "",
          remark: "",
          metadata: "{}",
          status: 0,
          seq: 0,
        };
        return;
      }
      if (row) {
        modalForm.value = {
          id: row.id,
          nvalueKind: row.nvalueKind,
          nvalue: row.nvalue,
          label: row.label,
          seq: row.seq,
          remark: row.remark,
        };
      }
    };

    const submitModal = async () => {
      if (!formRef.value) return;
      try {
        await formRef.value.validate();
        modalLoading.value = true;
        if (modalMode.value === "add") {
          await RegistryApi.addRegistry(modalForm.value);
          ElMessage.success("新增成功");
        }
        if (modalMode.value === "edit") {
          await RegistryApi.editRegistry(modalForm.value);
          ElMessage.success("修改成功");
        }
        modalVisible.value = false;
        onRefresh();
      } catch (error: any) {
        ElMessage.error(error.message || "提交失败");
      } finally {
        modalLoading.value = false;
      }
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      modalRules,
      openModal,
      submitModal,
    };
  }
}
