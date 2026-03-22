import { Delete, Edit, Plus, View } from "@element-plus/icons-vue";
import { Icon } from "@iconify/vue";
import { h, markRaw, type Component } from "vue";

/**
 * 全局图标缓存
 */
const globalIconCache = new Map<string, Component>();

//加入默认图标
const defaultIconName = "icon-service-default";

const defaultIcon = markRaw(() => h(Icon, { icon: "ep:magic-stick", width: "1em", height: "1em" }));
globalIconCache.set(defaultIconName, defaultIcon);

export default {
  /**
   * 使用简单图标服务
   * 这里提供了增删改查的基本图标,并打包成对象返回
   * @returns 简单图标
   */
  useSimpleIconService() {
    const addIcon = markRaw(Plus);
    const editIcon = markRaw(Edit);
    const removeIcon = markRaw(Delete);
    const viewIcon = markRaw(View);
    return {
      addIcon,
      editIcon,
      removeIcon,
      viewIcon,
    };
  },

  /**
   * 使用标准动态图标服务，可以动态获取图标组件
   * A:这个图标服务同时兼容Element Plus和Iconify的图标，根据传入的图标名称，动态获取图标组件
   * B:这个图标服务具有缓存功能，避免重复创建导致重渲染
   * @returns 图标服务
   */
  useIconService() {
    const resolveIcon = (iconName: string): Component => {
      //如果图标名称为空，则返回默认图标
      if (!iconName) {
        return globalIconCache.get(defaultIconName);
      }

      //查字典 如果缓存池里有这个组件，直接拿走，拒绝重复捏造
      if (globalIconCache.has(iconName)) {
        return globalIconCache.get(iconName);
      }

      let finalIconString = iconName;

      //历史包袱转换：兼容没有加前缀的 Element Plus 图标（比如 "Setting" 或 "EditPen"）
      if (!iconName.includes(":")) {
        // 将大驼峰 (CamelCase) 转换为短横线 (kebab-case)
        // 例如: "EditPen" -> "edit-pen"，并拼上 "ep:" 前缀
        const kebabName = iconName
          .replace(/([A-Z])/g, "-$1")
          .toLowerCase()
          .replace(/^-/, "");
        finalIconString = `ep:${kebabName}`;
      }

      //现场捏造 直接用Vue的h函数捏一个组件出来 先设成 1em 继承父级字体大小，方便外部用 CSS 控制尺寸
      const dynamicComponent = markRaw(() => h(Icon, { icon: finalIconString, width: "1em", height: "1em" }));

      //加进缓存
      globalIconCache.set(iconName, dynamicComponent);
      return dynamicComponent;
    };
  },
};
