import { onBeforeUnmount, onMounted, ref, type Ref } from "vue";

/**
 * 这个Service提供了元素焦点管理功能
 * 可以用于管理元素的焦点状态
 */
export default {
  /**
   * 使用元素焦点管理功能
   * @param elementRef 目标 DOM 元素的 Ref
   */
  useElementFocus(elementRef: Ref<HTMLElement>) {
    //响应式状态
    const isFocused = ref(false);

    //内部事件处理函数
    const handleFocus = () => {
      isFocused.value = true;
    };

    const handleBlur = () => {
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
      if (el) {
        el.addEventListener("focus", handleFocus);
        el.addEventListener("blur", handleBlur);
      }
    });

    onBeforeUnmount(() => {
      const el = elementRef.value;
      if (el) {
        el.removeEventListener("focus", handleFocus);
        el.removeEventListener("blur", handleBlur);
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
