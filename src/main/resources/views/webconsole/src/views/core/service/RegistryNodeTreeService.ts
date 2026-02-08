import { ref } from "vue";
import RegistryApi, { type GetRegistryNodeTreeVo } from "@/views/core/api/RegistryApi";

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
}
