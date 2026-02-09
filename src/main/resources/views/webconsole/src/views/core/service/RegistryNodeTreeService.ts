import { ref, reactive, type Ref } from "vue";
import RegistryApi, {
  type GetRegistryNodeTreeVo,
  type AddRegistryDto,
  type EditRegistryDto,
  type GetRegistryDetailsVo,
} from "@/views/core/api/RegistryApi";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";

/**
 * 注册表节点树服务层
 * 封装了树数据的加载、筛选、节点选择以及新增节点的业务逻辑
 */
export default {
  /**
   * 注册表节点树基础逻辑 Hook
   * 处理数据加载、搜索过滤和统一的选择状态管理
   */
  useRegistryNodeTree() {
    const treeData = ref<GetRegistryNodeTreeVo[]>([]); // 树形结构数据
    const loading = ref(false); // 加载状态标识
    const filterText = ref(""); // 搜索关键字映射
    const selectedNode = ref<GetRegistryNodeTreeVo | null>(null); // 当前选中的节点对象

    /**
     * 选择节点事件
     * @param node 被选中的节点，null 表示选择“全部”
     */
    const onSelectNode = (node: GetRegistryNodeTreeVo | null) => {
      selectedNode.value = node;
    };

    /**
     * 调用 API 加载注册表树结构
     */
    const loadTreeData = async () => {
      loading.value = true;
      try {
        const data = await RegistryApi.getRegistryNodeTree();
        treeData.value = data;
      } catch (error) {
        console.error("加载注册表节点树失败", error);
      } finally {
        loading.value = false;
      }
    };

    /**
     * 删除指定节点
     * @param node 待删除的节点对象
     * @param onRefresh 删除成功后的刷新回调
     */
    const removeNode = async (node: GetRegistryNodeTreeVo, onRefresh: () => void) => {
      try {
        await ElMessageBox.confirm(`确定要删除节点 [${node.nkey}] 吗？`, "确认删除", {
          confirmButtonText: "确认",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await RegistryApi.removeRegistry({ id: node.id });
        ElMessage.success("删除节点成功");
        onRefresh();
      } catch (error: any) {
        ElMessage.error(error.message || "删除节点失败");
      }
    };

    return {
      treeData,
      loading,
      filterText,
      loadTreeData,
      selectedNode,
      onSelectNode,
      removeNode,
    };
  },

  /**
   * 节点新增模态框逻辑 Hook
   * @param formRef 表单实例引用，用于提交校验
   * @param onRefresh 提交成功后的刷新回调
   */
  useNodeModal(formRef: Ref<FormInstance | undefined>, onRefresh: () => void) {
    const modalVisible = ref(false); // 模态框显示状态
    const modalLoading = ref(false); // 提交中状态
    const modalMode = ref<"add" | "edit">("add"); // 模态框模式

    // 初始表单数据模型
    const modalForm = reactive<GetRegistryDetailsVo>({
      id: "",
      parentId: undefined,
      kind: 0, // 0 固定表示节点
      nkey: "",
      nvalueKind: 0,
      nvalue: "",
      label: "",
      remark: "",
      metadata: "{}",
      status: 0,
      seq: 0,
    });

    // 表单校验规则
    const modalRules = {
      nkey: [
        { required: true, message: "请输入节点Key", trigger: "blur" },
        { max: 128, message: "节点Key长度不能超过128个字符", trigger: "blur" },
        { pattern: /^[a-zA-Z0-9_\-]+$/, message: "节点Key只能包含字母、数字、下划线或中划线", trigger: "blur" },
      ],
      label: [{ max: 32, message: "节点标签长度不能超过32个字符", trigger: "blur" }],
      remark: [{ max: 1000, message: "说明长度不能超过1000个字符", trigger: "blur" }],
      seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
    };

    /**
     * 重置表单状态
     */
    const resetModal = () => {
      modalForm.id = "";
      modalForm.parentId = undefined;
      modalForm.kind = 0;
      modalForm.nkey = "";
      modalForm.nvalueKind = 0;
      modalForm.nvalue = "";
      modalForm.label = "";
      modalForm.remark = "";
      modalForm.metadata = "{}";
      modalForm.status = 0;
      modalForm.seq = 0;

      if (formRef.value) {
        formRef.value.resetFields();
      }
    };

    /**
     * 打开新增/编辑模态框
     * @param mode 操作模式
     * @param node 当前节点数据（仅编辑模式需要）
     * @param parentNode 父级节点（仅新增模式需要）
     */
    const openModal = (
      mode: "add" | "edit",
      node: GetRegistryNodeTreeVo | null = null,
      parentNode: GetRegistryNodeTreeVo | null = null
    ) => {
      modalMode.value = mode;
      resetModal();

      if (mode === "add") {
        modalForm.parentId = parentNode?.id ?? undefined;
        modalVisible.value = true;
        return;
      }

      if (node) {
        modalForm.id = node.id;
        modalForm.nkey = node.nkey;
        modalForm.label = node.label;
        modalForm.seq = node.seq;
        modalForm.remark = ""; // 树节点 VO 不含说明，若需可扩展详情接口
        modalVisible.value = true;
      }
    };

    /**
     * 提交表单数据并保存
     */
    const submitModal = async () => {
      if (!formRef.value) {
        return;
      }

      try {
        await formRef.value.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      try {
        if (modalMode.value === "add") {
          const addDto: AddRegistryDto = {
            parentId: modalForm.parentId,
            kind: modalForm.kind,
            nkey: modalForm.nkey,
            label: modalForm.label,
            remark: modalForm.remark,
            seq: modalForm.seq,
          };
          await RegistryApi.addRegistry(addDto);
          resetModal();
          ElMessage.success("新增节点成功");
        }

        if (modalMode.value === "edit") {
          if (!modalForm.id) {
            throw new Error("节点ID不能为空");
          }
          const editDto: EditRegistryDto = {
            id: modalForm.id,
            kind: modalForm.kind,
            label: modalForm.label,
            seq: modalForm.seq,
            remark: modalForm.remark,
          };
          await RegistryApi.editRegistry(editDto);
          ElMessage.success("修改节点成功");
        }

        //modalVisible.value = false;
        onRefresh(); // 刷新外部树数据
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
      resetModal,
    };
  },
};
