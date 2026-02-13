import { computed, onMounted, ref, watch } from "vue";
import { useTabStore } from "@/store/TabHolder.ts";
import GenricHotkeyService from "@/service/GenricHotkeyService.ts";
import { useRoute, useRouter } from "vue-router";
import MenuApi, { type GetUserMenuTreeVo } from "@/views/core/api/MenuApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage } from "element-plus";
import { EventHolder } from "@/store/EventHolder.ts";

export default {
    /**
     * 框架快捷键服务
     */
    useComTabHotkey() {
        const { activeOf } = useTabStore();

        /**
         * 初始化快捷键
         */
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
            ref(true)
        );
    },

    /**
     * 框架布局服务
     */
    useComFramework() {
        const router = useRouter();
        const route = useRoute();

        /**
         * 自动生成面包屑导航
         */
        const autoBreadcrumbs = computed(() => {
            const revisedBreadcrumbs: Array<{ text: string; to?: string | object }> = [];

            // 尝试添加首页/根路径面板屑
            let homeTitle = "控制台";
            const homeRouteConfig = router.options.routes.find((r) => r.path === "/");
            const homeBreadcrumbMeta = homeRouteConfig?.meta?.breadcrumb as any;

            if (homeBreadcrumbMeta) {
                if (typeof homeBreadcrumbMeta === "string") {
                    homeTitle = homeBreadcrumbMeta;
                }
                if (typeof homeBreadcrumbMeta === "object" && homeBreadcrumbMeta.title) {
                    homeTitle = homeBreadcrumbMeta.title;
                }
            }

            // 如果当前不是根路径，则添加控制台首页
            if (route.path !== "/") {
                revisedBreadcrumbs.push({
                    text: homeTitle,
                    to: "/",
                });
            }

            // 遍历匹配的路由记录
            route.matched.forEach((record, index) => {
                // 如果已经添加了首页，并且当前记录是根路径，则跳过
                if (record.path === "/" && revisedBreadcrumbs.length > 0 && revisedBreadcrumbs[0].to === "/") return;

                const meta = record.meta;
                let title = "";
                let hidden = false;
                const path = record.path; // 使用匹配路由的路径

                // 处理 meta.breadcrumb (支持字符串和对象)
                const breadcrumbMeta = meta?.breadcrumb as any;

                if (typeof breadcrumbMeta === "string") {
                    title = breadcrumbMeta;
                }

                if (breadcrumbMeta && typeof breadcrumbMeta === "object") {
                    if (breadcrumbMeta.title) {
                        title = breadcrumbMeta.title;
                    }
                    hidden = breadcrumbMeta.hidden === true;
                }

                // 如果 breadcrumb 中没有 title，尝试使用 meta.title
                if (!title && meta?.title && typeof meta.title === "string") {
                    title = meta.title;
                }

                // 如果有标题且不隐藏，则添加到面包屑数组中
                if (title && !hidden) {
                    // 检查是否已存在（基于路径）
                    const alreadyExists = revisedBreadcrumbs.some((b) => b.to === path);
                    if (!alreadyExists) {
                        revisedBreadcrumbs.push({
                            text: title,
                            // 最后一个面包屑（当前页面）不设置链接
                            to: index === route.matched.length - 1 ? undefined : path,
                        });
                    }
                }
            });

            return revisedBreadcrumbs;
        });

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

        /**
         * 菜单数据
         */
        const menuTree = ref<GetUserMenuTreeVo[]>([]);

        /**
         * 加载菜单树
         */
        const loadMenuTree = async () => {
            try {
                const result = await MenuApi.getUserMenuTree();
                if (!Result.isSuccess(result)) {
                    ElMessage.error(result.message);
                    return;
                }
                menuTree.value = result.data;
            } catch (error: any) {
                ElMessage.error(error.message);
            }
        };

        /**
         * 根据路由路径计算当前活动菜单ID
         */
        const findMenuIdByPath = (items: GetUserMenuTreeVo[], path: string): any => {
            for (const item of items) {
                if (item.menuPath === path) {
                    return item.id;
                }
                if (item.children?.length) {
                    const foundId = findMenuIdByPath(item.children, path);
                    if (foundId) return foundId;
                }
            }
            return "";
        };

        /**
         * 当前活动菜单ID
         */
        const activeMenuId = computed(() => {
            return findMenuIdByPath(menuTree.value, route.path);
        });

        /**
         * 监听左侧菜单重新加载事件
         */
        watch(
            () => EventHolder().isNeedReloadLeftMenu,
            (newVal) => {
                if (newVal) {
                    loadMenuTree();
                }
            },
            { immediate: true }
        );

        // 初始加载
        onMounted(() => {
            loadMenuTree();
        });

        return {
            autoBreadcrumbs,
            isMenuCollapse,
            toggleMenu,
            menuTree,
            activeMenuId,
        };
    },
};
