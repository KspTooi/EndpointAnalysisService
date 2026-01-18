import { computed, onMounted, onUnmounted, ref, type Ref } from "vue";
import type { GetCollectionTreeVo } from "../api/CollectionApi";
import type { RightMenuEmitter } from "../components/RdbgCollectionTreeRightMenu.vue";

export default {
  /**
   * 右键菜单控制打包
   * @param rightMenuRef 右键菜单引用
   */
  useRightMenuControl(rightMenuRef: Ref<HTMLElement>) {
    const x = ref(0);
    const y = ref(0);
    const visible = ref(false);

    const currentNode = ref<GetCollectionTreeVo | null>(null);

    const openRightMenu = (event: MouseEvent, node: GetCollectionTreeVo | null = null) => {
      currentNode.value = node;
      visible.value = true;

      // 等待下一帧计算菜单位置，确保菜单DOM已更新
      requestAnimationFrame(() => {
        if (!rightMenuRef.value) {
          return;
        }

        const menuWidth = rightMenuRef.value.offsetWidth;
        const menuHeight = rightMenuRef.value.offsetHeight;
        const windowWidth = window.innerWidth;
        const windowHeight = window.innerHeight;

        let menuX = event.clientX;
        let menuY = event.clientY;

        // 检查右边界
        if (menuX + menuWidth > windowWidth) {
          menuX = windowWidth - menuWidth - 5;
        }

        // 检查下边界
        if (menuY + menuHeight > windowHeight) {
          menuY = windowHeight - menuHeight - 5;
        }

        // 确保不超出左边界
        if (menuX < 0) {
          menuX = 5;
        }

        // 确保不超出上边界
        if (menuY < 0) {
          menuY = 5;
        }

        x.value = menuX;
        y.value = menuY;
      });
    };

    const closeRightMenu = () => {
      visible.value = false;
    };

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
      currentNode,
      visible,
      x,
      y,
    };
  },

  /**
   * 集合树右键菜单事件发射器打包
   * @param emit 事件发射器
   * @param closeRightMenu 关闭右键菜单
   * @param currentNode 当前节点
   * @returns 事件发射器
   */
  useRightMenuEmitter(emit: RightMenuEmitter, closeRightMenu: () => void, currentNode: Ref<GetCollectionTreeVo | null>) {
    return {
      onCreateCollection: () => {
        emit("on-create", currentNode.value);
        closeRightMenu();
      },

      onCopyCollection: () => {
        if (!currentNode.value) {
          return;
        }
        emit("on-copy", currentNode.value);
        closeRightMenu();
      },

      onRenameCollection: () => {
        if (!currentNode.value) {
          return;
        }
        emit("on-rename", currentNode.value);
        closeRightMenu();
      },

      onRemoveCollection: () => {
        if (!currentNode.value) {
          return;
        }
        emit("on-remove", [currentNode.value]);
        closeRightMenu();
      },
    };
  },
};
