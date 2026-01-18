import { computed, ref, type Ref } from "vue";
import type { GetCollectionTreeVo } from "../api/CollectionApi";
import { ElMessage } from "element-plus";

/**
 * 集合树项拖拽事件发射器
 */
export interface CollectionDragEmitter {
  (e: "on-click", collection: GetCollectionTreeVo): void;
  (e: "on-dblclick", collection: GetCollectionTreeVo): void;
  (e: "on-contextmenu", collection: GetCollectionTreeVo, event: MouseEvent): void;
  (e: "on-drag-start", collection: GetCollectionTreeVo, event: DragEvent): void;
  (e: "on-drag-over", collection: GetCollectionTreeVo, event: DragEvent): void;
  (e: "on-drag-leave", collection: GetCollectionTreeVo, event: DragEvent): void;
  (e: "on-drag-drop", collection: GetCollectionTreeVo, event: DragEvent): void;
}

export default {
  /**
   * 集合树项事件发射器打包
   * @param emit 事件发射器
   */
  useCollectionTreeItemEmitter: (collection: GetCollectionTreeVo, emit: CollectionDragEmitter) => {
    return {
      //点击
      onClick: (collection: GetCollectionTreeVo) => {
        emit("on-click", collection);
      },

      //双击
      onDoubleClick: (collection: GetCollectionTreeVo) => {
        emit("on-dblclick", collection);
      },

      //右键菜单
      onContextmenu: (event: MouseEvent) => {
        emit("on-contextmenu", collection, event);
      },

      //拖拽开始
      onDragStart: (event: DragEvent) => {
        emit("on-drag-start", collection, event);
      },

      //拖拽经过
      onDragOver: (event: DragEvent) => {
        emit("on-drag-over", collection, event);
      },

      //拖拽离开
      onDragLeave: (event: DragEvent) => {
        emit("on-drag-leave", collection, event);
      },

      //拖拽放置
      onDrop: (event: DragEvent) => {
        emit("on-drag-drop", collection, event);
      },
    };
  },

  /**
   * 集合树项拖拽悬停状态计算
   * @param currentCollection 当前集合
   * @param dragHoverTarget 拖拽目标集合
   * @param dragHoverZone 拖拽目标悬停区域
   */
  useCollectionItemDragHover: (props: {
    node: GetCollectionTreeVo;
    dragHoverTarget: GetCollectionTreeVo | null;
    dragHoverZone: "center" | "top" | "bottom" | null;
  }) => {
    const isDragHoverTop = computed(() => {
      if (props.dragHoverTarget?.id !== props.node.id) {
        return false;
      }
      return props.dragHoverZone === "top";
    });

    const isDragHoverCenter = computed(() => {
      if (props.dragHoverTarget?.id !== props.node.id) {
        return false;
      }
      return props.dragHoverZone === "center";
    });

    const isDragHoverBottom = computed(() => {
      if (props.dragHoverTarget?.id !== props.node.id) {
        return false;
      }
      return props.dragHoverZone === "bottom";
    });

    return {
      isDragHoverTop,
      isDragHoverCenter,
      isDragHoverBottom,
    };
  },

  /**
   * 集合树项计算属性打包
   * @param props 集合树项属性
   */
  useCollectionItemComputed: (props: { node: GetCollectionTreeVo }) => {
    //请求方法名称 请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS
    const method = ["GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"];
    const methodName = computed(() => {
      if (props.node.reqMethod == null) {
        return "未知";
      }
      //最多4个字母
      return method[props.node.reqMethod].substring(0, 4);
    });

    //请求方法CSS类名
    const methodClass = computed(() => {
      if (props.node.reqMethod == null) {
        return "method-unknown";
      }
      if (props.node.reqMethod >= 0 && props.node.reqMethod < method.length) {
        return `method-${method[props.node.reqMethod]}`.toLowerCase();
      }
      return "method-unknown".toLowerCase();
    });

    return {
      methodName,
      methodClass,
    };
  },

  /**
   * 是否是组
   * @param node 集合树项
   */
  isGroup: (node: GetCollectionTreeVo) => node.kind === 1,
  /**
   * 是否有子节点
   * @param node 集合树项
   */
  hasChildren: (node: GetCollectionTreeVo) => node.children && node.children.length > 0,
  /**
   * 是否展开
   * @param nodeId 节点ID
   * @param activeNodes 激活的节点ID列表
   */
  isExpanded: (nodeId: string, props: { expandedIds: string[] }) => props.expandedIds.includes(nodeId),
};
