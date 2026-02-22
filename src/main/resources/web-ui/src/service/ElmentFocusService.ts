import { onBeforeUnmount, onMounted, ref, type Ref } from "vue";

/**
 * 这个Service提供了元素焦点管理功能
 * 可以用于管理元素的焦点状态
 */
export default {
  /**
   * 使用元素焦点管理功能
   * @param elementRef 目标 DOM 元素的 Ref
   * @param autoTabIndex 是否自动设置 tabindex-1
   */
  useElementFocus(elementRef: Ref<HTMLElement>, autoTabIndex: boolean = true) {
    //响应式状态
    const isFocused = ref(false);

    //内部事件处理函数
    const onFocus = () => {
      isFocused.value = true;
    };

    const onBlur = () => {
      isFocused.value = false;
    };

    //手动控制方法
    const setFocus = () => {
      elementRef.value?.focus();
    };

    const clearFocus = () => {
      elementRef.value?.blur();
    };

    //生命周期管理：绑定与解绑事件
    onMounted(() => {
      const el = elementRef.value;

      if (!el) {
        return;
      }

      el.addEventListener("focus", onFocus);
      el.addEventListener("blur", onBlur);

      if (autoTabIndex) {
        el.setAttribute("tabindex", "-1");
      }
    });

    onBeforeUnmount(() => {
      const el = elementRef.value;
      if (el) {
        el.removeEventListener("focus", onFocus);
        el.removeEventListener("blur", onBlur);
      }
    });

    //返回接口
    return {
      isFocused, //是否具有焦点
      setFocus, //设置焦点
      clearFocus, //清除焦点
    };
  },
};
