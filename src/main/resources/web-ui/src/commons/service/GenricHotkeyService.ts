import { onMounted, onUnmounted, type Ref } from "vue";

//Ctrl 键对应的按键映射
const ctrlKeyMap: Record<string, keyof HotkeyActions> = {
  a: "ctrl_a",
  c: "ctrl_c",
  v: "ctrl_v",
  x: "ctrl_x",
  s: "ctrl_s",
  "1": "ctrl_1",
  "2": "ctrl_2",
  "3": "ctrl_3",
  "4": "ctrl_4",
  "5": "ctrl_5",
  "6": "ctrl_6",
  "7": "ctrl_7",
  "8": "ctrl_8",
  "9": "ctrl_9",
  "0": "ctrl_0",
};

//单键对应的按键映射
const singleKeyMap: Record<string, { action: keyof HotkeyActions; preventDefault?: boolean }> = {
  delete: { action: "delete" },
  f2: { action: "f2" },
  enter: { action: "enter" },
  escape: { action: "esc" },
  tab: { action: "tab" },
  shift: { action: "shift", preventDefault: false },
  control: { action: "ctrl", preventDefault: false },
  meta: { action: "ctrl", preventDefault: false },
  backspace: { action: "backspace" },
};

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

  //按键Backspace
  backspace?: () => void;

  ctrl_1?: () => void;
  ctrl_2?: () => void;
  ctrl_3?: () => void;
  ctrl_4?: () => void;
  ctrl_5?: () => void;
  ctrl_6?: () => void;
  ctrl_7?: () => void;
  ctrl_8?: () => void;
  ctrl_9?: () => void;
  ctrl_0?: () => void;
}

export default {
  /**
   * 启用快捷键监听
   * @param actions 动作集合 (只传需要监听的按键)
   * @param active 是否激活快捷键
   * @param triggerInInput 是否在输入框中触发快捷键
   */
  useHotkeyFunction(actions: HotkeyActions, active: Ref<boolean>, triggerInInput?: boolean) {
    /**
     * 辅助执行函数：只有当 action 存在时，才阻止默认事件并执行
     * @param e 键盘事件
     * @param action 对应的回调函数
     * @param preventDefault 是否阻止默认事件 (默认 true)
     */
    const trigger = (e: KeyboardEvent, action: (() => void) | undefined, preventDefault = true): boolean => {
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
    const onKeydown = (e: KeyboardEvent): void => {
      if (!active.value) {
        return;
      }

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
      if (isInput && !triggerInInput) {
        return;
      }

      // --- 组合键处理 (Ctrl / Command + Key) ---

      if (isCtrl) {
        //先把按下的按键转换为小写
        const _key = key.toLowerCase();

        //查找在组合映射表中是否存在
        const action = ctrlKeyMap[_key];

        //执行对应的回调函数
        if (action) {
          trigger(e, actions[action]);
          return;
        }

        //单纯按下 Ctrl 键本身不会进入 isCtrl && key='control' 的组合，
        //这里主要处理 Ctrl 组合键，单独Ctrl在下方处理。
        return;
      }

      // --- 单键处理 ---
      const _key = key.toLowerCase();

      //查找在单键映射表中是否存在
      const singleAction = singleKeyMap[_key];

      if (singleAction) {
        trigger(e, actions[singleAction.action], singleAction.preventDefault);
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
