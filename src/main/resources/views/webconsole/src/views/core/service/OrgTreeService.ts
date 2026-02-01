import { ref } from "vue";
import OrgApi, { type GetOrgTreeVo } from "../api/OrgApi";

export default class OrgTreeService {
  /**
   * 组织架构树逻辑
   */
  public static useOrgTree() {
    const treeData = ref<GetOrgTreeVo[]>([]);
    const loading = ref(false);
    const filterText = ref("");

    const loadTreeData = async () => {
      loading.value = true;
      try {
        const data = await OrgApi.getOrgTree({ name: filterText.value });
        treeData.value = data;
      } catch (error) {
        console.error("加载组织架构树失败", error);
      } finally {
        loading.value = false;
      }
    };

    return {
      treeData,
      loading,
      filterText,
      loadTreeData,
    };
  }
}
