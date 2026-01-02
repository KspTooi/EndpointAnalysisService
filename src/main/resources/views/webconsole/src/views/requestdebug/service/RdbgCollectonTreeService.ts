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
   * 集合树项拖拽功能打包
   * @param emit 事件发射器
   */
  useCollectionTreeItemDrag: () => {
    //当前正在拖拽的集合
    const draggedCollection = ref<GetCollectionTreeVo>(null);

    //拖拽悬停区域
    const dragHoverZone = ref<"center" | "top" | "bottom">(null);
    const dragHoverTarget = ref<GetCollectionTreeVo>(null);

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
      dragHoverZone.value = null;
      dragHoverTarget.value = null;

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
        ElMessage.success("拖拽到目标上边缘");
        //emit("on-collection-drag", target, [draggedCollection.value], zone);
      }

      //拖拽到目标中心
      if (zone === "center") {
        ElMessage.success("拖拽到目标中心");
        //emit("on-collection-drag", target, [draggedCollection.value], zone);
      }

      //拖拽到目标下边缘
      if (zone === "bottom") {
        ElMessage.success("拖拽到目标下边缘");
        //emit("on-collection-drag", target, [draggedCollection.value], zone);
      }
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
};
