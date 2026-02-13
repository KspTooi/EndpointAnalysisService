import { ref } from "vue";
import { useTabStore } from "@/store/TabHolder.ts";
import GenricHotkeyService from "@/service/GenricHotkeyService.ts";

export default {
    /**
     * 框架快捷键服务
     */
    useComTabHotkey() {
        const hotKeyActive = ref(true);
        const { activeOf } = useTabStore();

        /**
         * 初始化快捷键
         */
        const initHotkeys = () => {
            GenricHotkeyService.useHotkeyFunction(
                {
                    ctrl_1: () => activeOf(1),
                    ctrl_2: () => activeOf(2),
                    ctrl_3: () => activeOf(3),
                    ctrl_4: () => activeOf(4),
                    ctrl_5: () => activeOf(5),
                    ctrl_6: () => activeOf(6),
                    ctrl_7: () => activeOf(7),
                    ctrl_8: () => activeOf(8),
                    ctrl_9: () => activeOf(9),
                },
                hotKeyActive
            );
        };

        return {
            initHotkeys,
        };
    },

    /**
     * 框架布局服务
     */
    useComFramework() {
        /**
         * 菜单折叠状态
         */
        const isMenuCollapse = ref(localStorage.getItem("isMenuCollapse") !== "false");

        /**
         * 切换菜单状态
         */
        const toggleMenu = () => {
            isMenuCollapse.value = !isMenuCollapse.value;
            localStorage.setItem("isMenuCollapse", isMenuCollapse.value.toString());
        };

        return {
            isMenuCollapse,
            toggleMenu,
        };
    },
};
