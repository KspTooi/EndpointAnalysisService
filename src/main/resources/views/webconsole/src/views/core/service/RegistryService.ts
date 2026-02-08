import { ref, type Ref } from "vue";
import RegistryApi, {
  type GetRegistryListDto,
  type GetRegistryEntryListVo,
  type AddRegistryDto,
  type EditRegistryDto,
} from "@/views/core/api/RegistryApi";
import type CommonIdDto from "@/commons/entity/CommonIdDto";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";

/**
 * 注册表业务服务层
 * 处理注册表条目列表的展示、增删改查逻辑
 */
export default class RegistryService {
  /**
   * 注册表条目列表管理 Hook
   * @param keyPath 当前选中的节点路径引用，用于同步查询条件
   */
  public static useRegistryList(keyPath: Ref<string | null>) {
    const listData = ref<GetRegistryEntryListVo[]>([]); // 列表数据
    const listLoading = ref(false); // 列表加载状态标识

    // 分页查询表单数据
    const listForm = ref<GetRegistryListDto>({
      keyPath: "",
      pageNum: 1,
      pageSize: 20,
    });

    /**
     * 加载当前选中路径下的条目列表
     * @param path 可选覆盖路径，若不传则使用 keyPath.value
     */
    const loadList = async (path?: string | null) => {
      const targetPath = path ?? keyPath.value;
      if (!targetPath) {
        // 如果没有选中任何节点，清空列表
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

    /**
     * 重置查询并重新加载
     * @param path 可选重置的路径
     */
    const resetList = (path?: string | null) => {
      listForm.value = {
        keyPath: path ?? keyPath.value ?? "",
        pageNum: 1,
        pageSize: 20,
      };
      loadList(path);
    };

    /**
     * 删除指定条目记录
     * @param row 待删除的行数据
     */
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
        loadList(); // 成功后刷新列表
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
   * 注册表条目新增/编辑模态框管理 Hook
   * @param formRef 绑定的表单组件实例引用
   * @param onRefresh 操作成功后的刷新列表回调
   */
  public static useRegistryModal(formRef: Ref<FormInstance | undefined>, onRefresh: () => void) {
    const modalVisible = ref(false); // 模态框显隐状态
    const modalLoading = ref(false); // 提交加载状态
    const modalMode = ref<"add" | "edit">("add"); // 模态框模式：新增、编辑
    const modalForm = ref<AddRegistryDto | EditRegistryDto | any>({}); // 表单响应式数据对象

    // 条目表单校验规则
    const modalRules = {
      nkey: [{ required: true, message: "请输入节点Key", trigger: "blur" }],
      nvalue: [{ required: true, message: "请输入节点值", trigger: "blur" }],
      kind: [{ required: true, message: "请选择类型", trigger: "change" }],
    };

    /**
     * 打开条目模态框并初始化数据
     * @param mode 操作模式
     * @param row 编辑时的原始数据
     * @param parentId 新增所属的父节点 ID
     */
    const openModal = (mode: "add" | "edit", row: any = null, parentId: string | null = null) => {
      modalMode.value = mode;
      modalVisible.value = true;
      if (mode === "add") {
        // 初始化新增模式的默认值 (kind=1 代表 Entry)
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
        // 绑定编辑模式的字段值 (仅提取后端支持编辑的字段)
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

    /**
     * 执行表单提交过程
     */
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
        onRefresh(); // 通知外部组件刷新
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
