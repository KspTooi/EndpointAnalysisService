import { ref, type Ref } from "vue";
import RegistryApi, { type GetRegistryNodeTreeVo, type AddRegistryDto } from "@/views/core/api/RegistryApi";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";

/**
 * 注册表节点树服务层
 * 封装了树数据的加载、筛选、节点选择以及新增节点的业务逻辑
 */
export default class RegistryNodeTreeService {
    /**
     * 注册表节点树基础逻辑 Hook
     * 处理数据加载、搜索过滤和统一的选择状态管理
     */
    public static useRegistryNodeTree() {
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
                await ElMessageBox.confirm(`确定要删除节点 [${node.nkey}] 及其下所有子节点吗？`, "确认删除", {
                    confirmButtonText: "确认",
                    cancelButtonText: "取消",
                    type: "warning",
                });
                await RegistryApi.removeRegistry({ id: node.id });
                ElMessage.success("删除节点成功");
                onRefresh();
            } catch (error: any) {
                if (error === "cancel") return;
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
    }

    /**
     * 节点新增模态框逻辑 Hook
     * @param formRef 表单实例引用，用于提交校验
     * @param onRefresh 提交成功后的刷新回调
     */
    public static useNodeModal(formRef: Ref<FormInstance | undefined>, onRefresh: () => void) {
        const modalVisible = ref(false); // 模态框显示状态
        const modalLoading = ref(false); // 提交中状态

        // 初始表单数据模型 (kind=0 固定表示节点)
        const modalForm = ref<AddRegistryDto>({
            parentId: undefined,
            kind: 0,
            nkey: "",
            label: "",
            remark: "",
            seq: 0,
        });

        // 表单校验规则
        const modalRules = {
            nkey: [
                { required: true, message: "请输入节点Key", trigger: "blur" },
                { pattern: /^[a-zA-Z0-9_\-]+$/, message: "节点Key只能包含字母、数字、下划线或中划线", trigger: "blur" },
            ],
            seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
        };

        /**
         * 打开新增模态框
         * @param parentNode 父级节点，若为空则创建根节点
         */
        const openModal = (parentNode: GetRegistryNodeTreeVo | null = null) => {
            modalVisible.value = true;
            modalForm.value = {
                parentId: parentNode?.id ?? undefined,
                kind: 0,
                nkey: "",
                label: "",
                remark: "",
                seq: 0,
            };
        };

        /**
         * 提交表单数据并保存
         */
        const submitModal = async () => {
            if (!formRef.value) return;
            try {
                await formRef.value.validate();
                modalLoading.value = true;
                await RegistryApi.addRegistry(modalForm.value);
                ElMessage.success("新增节点成功");
                modalVisible.value = false;
                onRefresh(); // 刷新外部树数据
            } catch (error: any) {
                if (error.message) {
                    ElMessage.error(error.message || "提交失败");
                }
            } finally {
                modalLoading.value = false;
            }
        };

        /**
         * 重置表单状态
         */
        const resetModal = () => {
            formRef.value?.resetFields();
        };

        return {
            modalVisible,
            modalLoading,
            modalForm,
            modalRules,
            openModal,
            submitModal,
            resetModal,
        };
    }
}
