import { ref, type Ref } from "vue";
import RegistryApi, { type GetRegistryNodeTreeVo, type AddRegistryDto } from "@/views/core/api/RegistryApi";
import { ElMessage, type FormInstance } from "element-plus";

export default class RegistryNodeTreeService {
    /**
     * 注册表节点树逻辑
     */
    public static useRegistryNodeTree() {
        const treeData = ref<GetRegistryNodeTreeVo[]>([]);
        const loading = ref(false);
        const filterText = ref("");
        const selectedNode = ref<GetRegistryNodeTreeVo | null>(null);

        const onSelectNode = (node: GetRegistryNodeTreeVo | null) => {
            selectedNode.value = node;
        };

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

        return {
            treeData,
            loading,
            filterText,
            loadTreeData,
            selectedNode,
            onSelectNode,
        };
    }

    /**
     * 节点创建模态框逻辑
     */
    public static useNodeModal(formRef: Ref<FormInstance | undefined>, onRefresh: () => void) {
        const modalVisible = ref(false);
        const modalLoading = ref(false);
        const modalForm = ref<AddRegistryDto>({
            parentId: undefined,
            kind: 0,
            nkey: "",
            label: "",
            remark: "",
            seq: 0,
        });

        const modalRules = {
            nkey: [
                { required: true, message: "请输入节点Key", trigger: "blur" },
                { pattern: /^[a-zA-Z0-9_\-]+$/, message: "节点Key只能包含字母、数字、下划线或中划线", trigger: "blur" },
            ],
            seq: [{ required: true, message: "请输入排序", trigger: "blur" }],
        };

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

        const submitModal = async () => {
            if (!formRef.value) return;
            try {
                await formRef.value.validate();
                modalLoading.value = true;
                await RegistryApi.addRegistry(modalForm.value);
                ElMessage.success("新增节点成功");
                modalVisible.value = false;
                onRefresh();
            } catch (error: any) {
                if (error.message) {
                    ElMessage.error(error.message || "提交失败");
                }
            } finally {
                modalLoading.value = false;
            }
        };

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
