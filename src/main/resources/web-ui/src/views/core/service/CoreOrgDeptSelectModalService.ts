import { ref, watch } from "vue";
import { type GetOrgTreeVo } from "@/views/core/api/OrgApi";

export default class CoreOrgDeptSelectModalService {
    /**
     * 部门选择弹窗逻辑
     */
    public static useDeptSelect(multiple: boolean) {
        const selectedNode = ref<GetOrgTreeVo | null>(null);
        const selectedNodes = ref<GetOrgTreeVo[]>([]);

        const onSelect = (node: GetOrgTreeVo | null) => {
            selectedNode.value = node;
        };

        const onCheck = (nodes: GetOrgTreeVo[]) => {
            selectedNodes.value = nodes;
        };

        return {
            selectedNode,
            selectedNodes,
            onSelect,
            onCheck,
        };
    }
}
