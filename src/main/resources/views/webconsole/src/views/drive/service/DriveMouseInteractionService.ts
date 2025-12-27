// hooks/useSelectionBox.ts
import type { GetEntryListVo } from "@/api/drive/DriveApi.ts";
import { ref, onUnmounted, type Ref } from "vue";

/**
 * 框选逻辑钩子
 * @param containerRef 容器的 DOM 引用 (gridRef)
 * @param itemRefsMap 条目 DOM 元素的 Map (entryRefs)
 */
export function useEntrySelection(containerRef: Ref<HTMLElement | null>, itemRefsMap: Ref<Map<string, HTMLElement>>) {
  // 是否正在框选
  const entrySelecting = ref(false);

  // 框选矩形
  const entrySelectionBox = ref({
    left: 0,
    top: 0,
    width: 0,
    height: 0,
  });

  // 选中项ID集合
  const entrySelectedIds = ref<Set<string>>(new Set());

  // 内部临时变量
  const startX = ref(0);
  const startY = ref(0);

  // 更新选中项 (核心碰撞检测算法)
  const updateEntrySelectedIds = () => {
    //如果容器不存在则返回
    if (!containerRef.value) {
      return;
    }

    const newSelectedIds = new Set<string>();
    const boxRect = {
      left: entrySelectionBox.value.left,
      top: entrySelectionBox.value.top,
      right: entrySelectionBox.value.left + entrySelectionBox.value.width,
      bottom: entrySelectionBox.value.top + entrySelectionBox.value.height,
    };

    const gridRect = containerRef.value.getBoundingClientRect();
    const scrollLeft = containerRef.value.scrollLeft;
    const scrollTop = containerRef.value.scrollTop;

    itemRefsMap.value.forEach((entryEl, id) => {
      const entryRect = entryEl.getBoundingClientRect();

      // 计算条目相对于容器内容的坐标
      const entryBox = {
        left: entryRect.left - gridRect.left + scrollLeft,
        top: entryRect.top - gridRect.top + scrollTop,
        right: entryRect.right - gridRect.left + scrollLeft,
        bottom: entryRect.bottom - gridRect.top + scrollTop,
      };

      // 碰撞检测
      if (boxRect.left < entryBox.right && boxRect.right > entryBox.left && boxRect.top < entryBox.bottom && boxRect.bottom > entryBox.top) {
        newSelectedIds.add(id);
      }
    });

    entrySelectedIds.value = newSelectedIds;
  };

  const onMouseMove = (event: MouseEvent) => {
    if (!entrySelecting.value || !containerRef.value) return;

    const rect = containerRef.value.getBoundingClientRect();
    const currentX = event.clientX - rect.left + containerRef.value.scrollLeft;
    const currentY = event.clientY - rect.top + containerRef.value.scrollTop;

    const left = Math.min(startX.value, currentX);
    const top = Math.min(startY.value, currentY);
    const width = Math.abs(currentX - startX.value);
    const height = Math.abs(currentY - startY.value);

    entrySelectionBox.value = { left, top, width, height };
    updateEntrySelectedIds();
  };

  const onMouseUp = () => {
    if (!entrySelecting.value) return;
    entrySelecting.value = false;
    document.removeEventListener("mousemove", onMouseMove);
    document.removeEventListener("mouseup", onMouseUp);
  };

  const onMouseDown = (event: MouseEvent) => {
    // 只有左键才触发
    if (event.button !== 0) return;

    // 忽略特定目标 (比如点击了文件本身，就不应该触发框选)
    const target = event.target as HTMLElement;
    if (target.closest(".drive-entry-item") || target.closest(".drive-entry-parent-item")) {
      return;
    }

    //如果容器不存在则返回
    if (!containerRef.value) {
      return;
    }

    const rect = containerRef.value.getBoundingClientRect();
    startX.value = event.clientX - rect.left + containerRef.value.scrollLeft;
    startY.value = event.clientY - rect.top + containerRef.value.scrollTop;

    entrySelecting.value = true;
    entrySelectedIds.value.clear(); // 开始新框选时清空旧选择

    entrySelectionBox.value = {
      left: startX.value,
      top: startY.value,
      width: 0,
      height: 0,
    };

    document.addEventListener("mousemove", onMouseMove);
    document.addEventListener("mouseup", onMouseUp);
  };

  // 清理副作用
  onUnmounted(() => {
    document.removeEventListener("mousemove", onMouseMove);
    document.removeEventListener("mouseup", onMouseUp);
  });

  return {
    //是否正在框选
    entrySelecting,

    //框选矩形
    entrySelectionBox,

    //选中项ID集合
    entrySelectedIds,

    //鼠标按下事件
    onMouseDown,

    // 如果需要手动清空选择，也可以导出这个方法
    clearSelection: () => entrySelectedIds.value.clear(),
  };
}

/**
 * 拖拽逻辑钩子
 * @param entryData 当前文件列表
 * @param selectedIds 当前已被选中的条目IDS
 * @param emit Vue的emit函数，用于向父组件发送最终的拖拽事件
 */
export function useEntryDrag(
  entryData: Ref<GetEntryListVo[]>,
  selectedIds: Ref<Set<string>>,
  emit: (event: "on-entry-drag", target: GetEntryListVo | null, entries: GetEntryListVo[]) => void
) {
  // --- 状态 ---
  const draggedEntry: Ref<GetEntryListVo> = ref();

  /**
   * 开始拖拽
   */
  const onDragStart = (entry: GetEntryListVo, event: DragEvent) => {
    if (!entry.id) return;
    draggedEntry.value = entry;
    if (event.dataTransfer) {
      event.dataTransfer.effectAllowed = "move";
      // 可以设置拖拽时的预览图，如果需要的话
      // event.dataTransfer.setDragImage(...)
    }
  };

  /**
   * 拖拽经过某条目
   */
  const onDragOver = (entry: GetEntryListVo, event: DragEvent) => {
    // 简单的防抖或检查可以在这里做
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = "move";
    }
  };

  /**
   * 放置在某条目上 (目标是普通文件/文件夹)
   */
  const onDrop = (targetEntry: GetEntryListVo, event: DragEvent) => {
    if (!draggedEntry.value?.id || !targetEntry.id) return;

    // 2. 计算实际被拖拽的文件列表 (处理多选)
    let entriesToDrag: GetEntryListVo[] = [];
    if (selectedIds.value.size > 1 && selectedIds.value.has(draggedEntry.value.id)) {
      // 如果拖拽的是选中集合里的一项，则移动所有选中的项
      entriesToDrag = entryData.value.filter((item) => selectedIds.value.has(item.id as string));
    } else {
      // 否则只移动当前这一项
      entriesToDrag = [draggedEntry.value];
    }

    // 3. 自身不能拖拽到自身
    if (entriesToDrag.some((item) => item.id === targetEntry.id)) return;

    // 4. 发送事件
    emit("on-entry-drag", targetEntry, entriesToDrag);
    draggedEntry.value = null; // 重置
  };

  /**
   * 拖拽经过上级目录
   */
  const onParentDragOver = (targetId: string | null, event: DragEvent) => {
    if (event.dataTransfer) {
      event.dataTransfer.dropEffect = "move";
    }
  };

  /**
   * 放置在上级目录
   */
  const onParentDrop = (targetId: string | null, event: DragEvent) => {
    if (!draggedEntry.value.id) return;

    const draggedEntry = entryData.value.find((item) => item.id === draggedEntry.value.id);
    if (!draggedEntry) return;

    let entriesToDrag: GetEntryListVo[] = [];
    if (selectedIds.value.size > 1 && selectedIds.value.has(draggedEntry.value.id)) {
      entriesToDrag = entryData.value.filter((item) => selectedIds.value.has(item.id as string));
    } else {
      entriesToDrag = [draggedEntry];
    }

    //投递到上级目录
    emit("on-entry-drag", null, entriesToDrag);
    draggedEntry.value = null;
  };

  return {
    draggedEntry,
    onDragStart,
    onDragOver,
    onDrop,
    onParentDragOver,
    onParentDrop,
  };
}

export default {
  useEntrySelection,
  useEntryDrag,
};
