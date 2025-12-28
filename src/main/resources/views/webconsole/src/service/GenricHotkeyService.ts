import { onMounted, onUnmounted } from "vue";

/**
 * 快捷键动作接口
 * 调用方只需传入需要覆盖的按键
 */
export interface HotkeyActions {
  //按键Ctrl+C
  ctrl_c?: () => void;

  //按键Ctrl+V
  ctrl_v?: () => void;

  //按键Ctrl+X
  ctrl_x?: () => void;

  //按键Ctrl+A
  ctrl_a?: () => void;

  //按键Ctrl+S
  ctrl_s?: () => void;

  //按键Enter
  enter?: () => void;

  //按键Delete
  delete?: () => void;

  //按键F2
  f2?: () => void;

  //按键F5
  f5?: () => void;

  //按键Esc
  esc?: () => void;

  //按键Tab
  tab?: () => void;

  //按键Shift
  shift?: () => void;

  //按键Ctrl
  ctrl?: () => void;
}

export default {
  /**
   * 启用快捷键监听
   * @param actions 动作集合 (只传需要监听的按键)
   */
  useHotkeyFunction(actions: HotkeyActions) {
    /**
     * 辅助执行函数：只有当 action 存在时，才阻止默认事件并执行
     * @param e 键盘事件
     * @param action 对应的回调函数
     * @param preventDefault 是否阻止默认事件 (默认 true)
     */
    const trigger = (e: KeyboardEvent, action: (() => void) | undefined, preventDefault = true) => {
      if (typeof action === "function") {
        if (preventDefault) {
          e.preventDefault();
        }
        action();
        return true; // 标识已处理
      }
      return false; // 标识未处理
    };

    /**
     * 按键按下事件
     */
    const onKeydown = (e: KeyboardEvent) => {
      const { key, ctrlKey, metaKey } = e;
      const isCtrl = ctrlKey || metaKey; // 兼容 Mac Command 键
      const activeTag = document.activeElement?.tagName.toLowerCase();
      const isInput = activeTag === "input" || activeTag === "textarea";

      // --- 全局按键 (即使在输入框中也可能需要触发，如 F5) ---

      // 按键 F5
      if (key === "F5") {
        trigger(e, actions.f5);
        return;
      }

      // 如果当前焦点在输入框或文本域中，通常不触发常规业务快捷键
      // (保留原生复制粘贴等功能)
      if (isInput) {
        return;
      }

      // --- 组合键处理 (Ctrl / Command + Key) ---

      if (isCtrl) {
        const _key = key.toLowerCase();

        if (_key === "a") {
          trigger(e, actions.ctrl_a);
          return;
        }
        if (_key === "c") {
          trigger(e, actions.ctrl_c);
          return;
        }
        if (_key === "v") {
          trigger(e, actions.ctrl_v);
          return;
        }
        if (_key === "x") {
          trigger(e, actions.ctrl_x);
          return;
        }
        if (_key === "s") {
          trigger(e, actions.ctrl_s);
          return;
        }

        //单纯按下 Ctrl 键本身不会进入 isCtrl && key='control' 的组合，
        //这里主要处理 Ctrl 组合键，单独Ctrl在下方处理。
        return;
      }

      // --- 单键处理 ---
      const _key = key.toLowerCase();

      if (_key === "delete") {
        trigger(e, actions.delete);
        return;
      }
      if (_key === "f2") {
        trigger(e, actions.f2);
        return;
      }
      if (_key === "enter") {
        trigger(e, actions.enter);
        return;
      }
      if (_key === "escape") {
        trigger(e, actions.esc);
        return;
      }
      if (_key === "tab") {
        trigger(e, actions.tab);
        return;
      }
      if (_key === "shift") {
        trigger(e, actions.shift, false);
        return;
      }
      if (_key === "control") {
        trigger(e, actions.ctrl, false);
        return;
      }
      if (_key === "meta") {
        trigger(e, actions.ctrl, false);
        return;
      }
    };

    onMounted(() => {
      window.addEventListener("keydown", onKeydown);
    });

    onUnmounted(() => {
      window.removeEventListener("keydown", onKeydown);
    });
  },
};
