import type { EntryPo } from "@/views/drive/api/DriveApi.ts";
import { DriveHolder } from "@/store/DriveHolder";
import { computed, onMounted, onUnmounted, ref, type Ref } from "vue";

export default {
  /**
   * 右键菜单控制打包
   * @param rightMenuRef 右键菜单引用
   */
  useRightMenuControl(rightMenuRef: Ref<HTMLElement>) {
    const x = ref(0);
    const y = ref(0);
    const visible = ref(false);

    //当前选中的条目列表
    const currentEntries = ref<EntryPo[]>([]);

    //是否有粘贴板内容
    const hasClipboard = computed(() => {
      return DriveHolder().getClipBoardEntry.length > 0;
    });

    //是否多选
    const isMultiSelect = computed(() => {
      return currentEntries.value.length > 1;
    });

    //当前选中的条目
    const currentEntry = computed(() => {
      return currentEntries.value[0] || null;
    });

    /**
     * 打开右键菜单
     * @param event 鼠标事件
     * @param entries 当前条目 为null表示没有选中条目
     */
    const openRightMenu = (event: MouseEvent, entries: EntryPo[] | null = null) => {
      x.value = event.clientX;
      y.value = event.clientY;
      currentEntries.value = entries || [];
      visible.value = true;
    };

    /**
     * 关闭右键菜单
     */
    const closeRightMenu = () => {
      visible.value = false;
    };

    /**
     * 处理点击到外部区域关闭菜单
     * @param event 鼠标事件
     */
    const onClickOutside = (event: MouseEvent) => {
      if (rightMenuRef.value && !rightMenuRef.value.contains(event.target as Node)) {
        closeRightMenu();
      }
    };

    onMounted(() => {
      document.addEventListener("click", onClickOutside);
    });

    onUnmounted(() => {
      document.removeEventListener("click", onClickOutside);
    });

    return {
      openRightMenu,
      closeRightMenu,
      isMultiSelect,
      currentEntry,
      currentEntries,
      hasClipboard,
      visible,
      x,
      y,
    };
  },

  /**
   * 右键菜单事件提交打包
   */
  useRightMenuEmitter() {},
};
