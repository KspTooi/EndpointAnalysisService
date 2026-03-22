import { Delete, Edit, Plus, View } from "@element-plus/icons-vue";
import { markRaw } from "vue";

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
};
