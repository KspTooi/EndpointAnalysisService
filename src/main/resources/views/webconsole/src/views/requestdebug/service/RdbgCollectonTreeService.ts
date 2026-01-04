import { onMounted, ref } from "vue";
import type { GetCollectionTreeVo } from "../api/CollectionApi";
import CollectionApi from "../api/CollectionApi";
import { Result } from "@/commons/entity/Result";
import type { CollectionDragEmitter } from "./RdbgCollectionTreeItemService";
import { ElMessage } from "element-plus";

export default {
  /**
   * 请求集合树功能打包
   */
  useCollectionTree: () => {
    const listData = ref<GetCollectionTreeVo[]>([]);
    const listTotal = ref<number>(0);
    const listFilter = ref<string>(null);
    const listLoading = ref<boolean>(false);

    /**
     * 加载请求集合树
     */
    const loadList = async () => {
      listLoading.value = true;
      try {
        const res = await CollectionApi.getCollectionTree();
        if (Result.isSuccess(res)) {
          listData.value = res.data;
          listTotal.value = res.data.length;
        }
      } catch (error) {
        console.error(error);
      } finally {
        listLoading.value = false;
      }
    };

    onMounted(() => {
      loadList();
    });

    return {
      listData,
      listTotal,
      listFilter,
      listLoading,
      loadList,
    };
  },

  /**
   * 集合选择与展开功能打包
   */
  useCollectionSelection: () => {
    //当前展开的节点ID列表
    const expandedIds = ref<string[]>([]);

    //当前选中的节点ID列表
    const selectedIds = ref<string[]>([]);

    /**
     * 切换节点展开/收起
     * @param nodeId 节点ID
     */
    const toggleNode = (node: GetCollectionTreeVo) => {
      if (!node) {
        return;
      }

      //处理组 组允许切换展开/收起
      if (node.kind === 1) {
        const hasChildren = node.children && node.children.length > 0;
        const isExpanded = expandedIds.value.includes(node.id);

        //组中没有内容时仅允许收起
        if (!hasChildren) {
          if (isExpanded) {
            expandedIds.value = expandedIds.value.filter((id) => id !== node.id);
          }
        }

        //组中有内容时允许展开/收起
        if (hasChildren) {
          if (isExpanded) {
            expandedIds.value = expandedIds.value.filter((id) => id !== node.id);
          }
          if (!isExpanded) {
            expandedIds.value.push(node.id);
          }
        }
      }

      //处理请求与组 它们都允许被选中
      if (node.kind === 0 || node.kind === 1) {
        const isSelected = selectedIds.value.includes(node.id);

        if (isSelected) {
          //已选中时再次点击不移除选中状态 只是清空其他选中
          //selectedIds.value = selectedIds.value.filter((id) => id !== node.id);
        }
        if (!isSelected) {
          selectedIds.value.push(node.id);
        }
      }

      //清空其他选中状态
      selectedIds.value = [node.id];
    };

    /**
     * 选中节点
     * @param node 节点
     */
    const selectNode = (node: GetCollectionTreeVo) => {
      if (!node) {
        return;
      }

      //判断是否已经被选中
      if (selectedIds.value.includes(node.id)) {
        return;
      }

      //选中
      selectedIds.value.push(node.id);
    };

    /**
     * 取消选中节点
     * @param node 节点
     */
    const unselectNode = (node: GetCollectionTreeVo) => {
      if (!node) {
        return;
      }
      selectedIds.value = selectedIds.value.filter((id) => id !== node.id);
    };

    return {
      expandedIds,
      selectedIds,
      toggleNode,
      selectNode,
      unselectNode,
    };
  },

  /**
   * 集合树项拖拽功能打包
   * @param emit 事件发射器
   */
  useCollectionTreeItemDrag: (
    onDrag: (target: GetCollectionTreeVo, entries: GetCollectionTreeVo[], zone: "center" | "top" | "bottom") => void
  ) => {
    //当前正在拖拽的集合
    const draggedCollection = ref<GetCollectionTreeVo>(null);

    //拖拽悬停区域
    const dragHoverZone = ref<"center" | "top" | "bottom" | null>(null);
    const dragHoverTarget = ref<GetCollectionTreeVo | null>(null);

    /**
     * 开始拖拽
     * @param collection 拖拽的集合
     * @param event 拖拽事件
     */
    const onDragStart = (collection: GetCollectionTreeVo, event: DragEvent) => {
      if (!collection.id) {
        return;
      }
      draggedCollection.value = collection;
      if (event.dataTransfer) {
        event.dataTransfer.effectAllowed = "move";
      }
    };

    /**
     * 拖拽经过某集合
     * @param collection 拖拽的集合
     * @param event 拖拽事件
     */
    const onDragOver = (collection: GetCollectionTreeVo, event: DragEvent) => {
      const target = event.currentTarget as HTMLElement;
      const rect = target.getBoundingClientRect();
      const y = event.clientY - rect.top;
      const zoneHeight = rect.height;
      const threshold = Math.max(10, zoneHeight * 0.25);

      dragHoverTarget.value = collection;

      if (y < threshold) {
        dragHoverZone.value = "top";
        return;
      }
      if (y > zoneHeight - threshold) {
        dragHoverZone.value = "bottom";
        return;
      }
      dragHoverZone.value = "center";
    };

    /**
     * 拖拽离开某集合
     * @param collection 拖拽的集合
     * @param event 拖拽事件
     */
    const onDragLeave = (collection: GetCollectionTreeVo, event: DragEvent) => {
      dragHoverZone.value = null;
      dragHoverTarget.value = null;
    };

    /**
     * 放置在某集合上
     * @param collection 拖拽的集合
     * @param event 拖拽事件
     */
    const onDrop = (target: GetCollectionTreeVo, event: DragEvent) => {
      //拖拽到根节点
      if (target == null) {
        onDrag(null, [draggedCollection.value], null);
        return;
      }

      const targetId = target.id;

      //自身或同一元素不处理
      if (targetId === draggedCollection.value.id) {
        ElMessage.error("拖拽集合到自身");
        return;
      }

      //获取拖拽区域
      const zone = dragHoverZone.value;

      //拖拽到目标上边缘
      if (zone === "top") {
        onDrag(target, [draggedCollection.value], zone);
      }

      //拖拽到目标中心
      if (zone === "center") {
        onDrag(target, [draggedCollection.value], zone);
      }

      //拖拽到目标下边缘
      if (zone === "bottom") {
        onDrag(target, [draggedCollection.value], zone);
      }
      dragHoverZone.value = null;
      dragHoverTarget.value = null;
    };
    return {
      draggedCollection,
      dragHoverZone,
      dragHoverTarget,
      onDragStart,
      onDragOver,
      onDragLeave,
      onDrop,
    };
  },

  /**
   * 集合树右键菜单功能打包
   */
  useCollectionTreeRightMenu: () => {
    const rightMenuVisible = ref<boolean>(false);
    const rightMenuX = ref<number>(0);
    const rightMenuY = ref<number>(0);
    const rightMenuNode = ref<GetCollectionTreeVo | null>(null);
  },
};
