import { ref, type Ref } from "vue";
import { type GetOrgTreeVo } from "@/views/core/api/OrgApi";

export default class CoreOrgDeptSelectModalService {
  /**
   * 部门选择弹窗逻辑
   */
  public static useDeptSelect(): {
    selectedNode: Ref<GetOrgTreeVo | null>;
    selectedNodes: Ref<GetOrgTreeVo[]>;
    onSelect: (node: GetOrgTreeVo | null) => void;
    onCheck: (nodes: GetOrgTreeVo[]) => void;
  } {
    const selectedNode = ref<GetOrgTreeVo | null>(null);
    const selectedNodes = ref<GetOrgTreeVo[]>([]);

    const onSelect = (node: GetOrgTreeVo | null): void => {
      selectedNode.value = node;
    };

    const onCheck = (nodes: GetOrgTreeVo[]): void => {
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
