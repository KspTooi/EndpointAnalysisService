
import DriveApi from "@/views/drive/api/DriveApi.ts";

import { ElMessage } from "element-plus";
import { ref, onUnmounted, type Ref, reactive, computed } from "vue";
import type { EntryPo, GetEntryListDto } from "@/views/drive/api/DriveTypes.ts"

/**
 * DriveEntryGrid 服务模块
 * 包含框选和拖拽逻辑的钩子函数
 */
export default {
  /**
   * 条目列表打包
   * @param emit
   */
  useEntryList(emit: (event: "on-entries-loaded", data: EntryPo[], total: number) => void) {
    const listQuery = reactive<GetEntryListDto>({
      directoryId: null,
      keyword: null,
      pageNum: 1,
      pageSize: 50000,
    });
    const listData = ref<EntryPo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    const dirId = ref<string>(null); //当前目录ID
    const dirName = ref<string>(null); //当前目录名称
    const dirParentId = ref<string>(null); //当前目录父级ID

    const hasParentDir = computed(() => {
      if (dirId.value == null && dirParentId.value == null && dirName.value == null) {
        return false;
      }
      return true;
    });

    const listLoad = async () => {
      listLoading.value = true;
      try {
        const res = await DriveApi.getEntryList(listQuery);

        dirId.value = res.data.dirId;
        dirName.value = res.data.dirName;
        dirParentId.value = res.data.dirParentId;

        //后端VO转换为前端PO
        const items: EntryPo[] = res.data.items.map((item) => {
          return {
            id: item.id,
            parentId: res.data.dirParentId,
            name: item.name,
            kind: item.kind,
            attachId: item.attachId,
            attachSize: item.attachSize,
            attachSuffix: item.attachSuffix,
            createTime: item.createTime,
            updateTime: null,
          };
        });

        listData.value = items;
        listTotal.value = res.data.total;
        emit("on-entries-loaded", items, listTotal.value);
        return res;
      } catch (error: any) {
        listLoading.value = false;
        ElMessage.error(error.message || "加载列表失败");
        throw error;
      } finally {
        listLoading.value = false;
      }
    };

    return {
      hasParentDir,
      dirId,
      dirName,
      dirParentId,
      listQuery,
      listData,
      listTotal,
      listLoading,
      listLoad,
    };
  },

  /**
   * 鼠标框选打包
   * @param gridRef 容器的 DOM 引用 (gridRef)
   * @param entryRefs 条目 DOM 引用集合 (entryRefs) ID->DOM元素
   */
  useEntrySelection(gridRef: Ref<HTMLElement>, entryRefs: Ref<Map<string, HTMLElement>>) {
    //是否正在框选
    const hasSelecting = ref(false);

    //框选矩形
    const selectBox = ref({
      left: 0,
      top: 0,
      width: 0,
      height: 0,
    });

    //选中项ID集合
    const selectedIds = ref<Set<string>>(new Set());

    //框选开始坐标
    const startX = ref(0);
    const startY = ref(0);

    // 更新选中项 (核心碰撞检测算法)
    const updateSelectedIds = () => {
      //如果容器不存在则返回
      if (!gridRef.value) {
        return;
      }

      const newSelectedIds = new Set<string>();
      const boxRect = {
        left: selectBox.value.left,
        top: selectBox.value.top,
        right: selectBox.value.left + selectBox.value.width,
        bottom: selectBox.value.top + selectBox.value.height,
      };

      const gridRect = gridRef.value.getBoundingClientRect();
      const scrollLeft = gridRef.value.scrollLeft;
      const scrollTop = gridRef.value.scrollTop;

      //遍历所有条目
      entryRefs.value.forEach((entryEl, id) => {
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

      selectedIds.value = newSelectedIds;
    };

    /**
     * 鼠标移动事件
     * @param event 鼠标事件
     */
    const onMouseMove = (event: MouseEvent) => {
      if (!hasSelecting.value || !gridRef.value) {
        return;
      }

      const rect = gridRef.value.getBoundingClientRect();
      const currentX = event.clientX - rect.left + gridRef.value.scrollLeft;
      const currentY = event.clientY - rect.top + gridRef.value.scrollTop;

      const left = Math.min(startX.value, currentX);
      const top = Math.min(startY.value, currentY);
      const width = Math.abs(currentX - startX.value);
      const height = Math.abs(currentY - startY.value);

      selectBox.value = { left, top, width, height };
      updateSelectedIds();
    };

    /**
     * 鼠标抬起事件
     */
    const onMouseUp = () => {
      //如果不在框选状态则返回
      if (!hasSelecting.value) {
        return;
      }

      hasSelecting.value = false;
      document.removeEventListener("mousemove", onMouseMove);
      document.removeEventListener("mouseup", onMouseUp);
    };

    /**
     * 鼠标按下事件
     * @param event 鼠标事件
     */
    const onMouseDown = (event: MouseEvent) => {
      //如果容器不存在则返回
      if (!gridRef.value) {
        return;
      }

      //如果不是左键按下则返回
      if (event.button !== 0) {
        return;
      }

      //如果点击到文件本身则返回
      const target = event.target as HTMLElement;
      if (target.closest(".drive-entry-item")) {
        return;
      }

      //记录框选开始坐标
      const rect = gridRef.value.getBoundingClientRect();
      startX.value = event.clientX - rect.left + gridRef.value.scrollLeft;
      startY.value = event.clientY - rect.top + gridRef.value.scrollTop;

      //开始新框选时清空旧选择
      hasSelecting.value = true;
      selectedIds.value.clear();

      selectBox.value = {
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
      hasSelecting,

      //框选矩形
      selectBox,

      //选中项ID集合
      selectedIds,

      //鼠标按下事件
      onMouseDown,

      // 如果需要手动清空选择,也可以导出这个方法
      clearSelection: () => selectedIds.value.clear(),
    };
  },

  /**
   * 拖拽逻辑钩子
   * @param entryData 当前文件列表
   * @param selectedIds 当前已被选中的条目IDS
   * @param emit Vue的emit函数,用于向父组件发送最终的拖拽事件
   */
  useEntryDrag(entryData: Ref<EntryPo[]>, selectedIds: Ref<Set<string>>, emit: (event: "on-entry-drag", target: EntryPo, entries: EntryPo[]) => void) {
    //当前正在拖拽的条目
    const draggedEntry: Ref<EntryPo> = ref();

    /**
     * 开始拖拽
     * @param entry 拖拽的条目
     * @param event 拖拽事件
     */
    const onDragStart = (entry: EntryPo, event: DragEvent) => {
      if (!entry.id) {
        return;
      }

      draggedEntry.value = entry;
      if (event.dataTransfer) {
        event.dataTransfer.effectAllowed = "move";
        //可设置拖拽时的预览图
        //event.dataTransfer.setDragImage(...)
      }
    };

    /**
     * 拖拽经过某条目
     * @param entry 拖拽的条目
     * @param event 拖拽事件
     */
    const onDragOver = (entry: EntryPo, event: DragEvent) => {
      // 简单的防抖或检查可以在这里做
      if (event.dataTransfer) {
        event.dataTransfer.dropEffect = "move";
      }
    };

    /**
     * 放置在某条目上 (目标是普通文件/文件夹)
     * @param targetEntry 目标条目
     * @param event 拖拽事件
     */
    const onDrop = (targetEntry: EntryPo, event: DragEvent) => {
      //计算实际被拖拽的文件列表 (处理多选)
      let dragEntries: EntryPo[] = [];

      let hasMultiDrag = selectedIds.value.size > 1 && selectedIds.value.has(draggedEntry.value.id);

      //如果拖拽的是选中集合里的某一项,则移动所有选中的项
      if (hasMultiDrag) {
        // 如果拖拽的是选中集合里的一项,则移动所有选中的项
        dragEntries = entryData.value.filter((item) => selectedIds.value.has(item.id));
      }
      if (!hasMultiDrag) {
        dragEntries = [draggedEntry.value];
      }

      //如果拖拽集合中的某一项与拖拽的目标相同 则判定为自身拖拽
      if (dragEntries.some((item) => item.id === targetEntry.id)) {
        return;
      }

      //发送事件
      emit("on-entry-drag", targetEntry, dragEntries);
      draggedEntry.value = null;
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
      let entriesToDrag: EntryPo[] = [];

      //如果选了多个且当前拖拽的也在其中,保持多选状态
      if (selectedIds.value.size > 1 && selectedIds.value.has(draggedEntry.value.id)) {
        entriesToDrag = entryData.value.filter((item) => selectedIds.value.has(item.id));
      } else {
        entriesToDrag = [draggedEntry.value];
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
  },
};
