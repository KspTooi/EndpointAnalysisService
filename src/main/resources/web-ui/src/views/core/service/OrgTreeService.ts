import { ref } from "vue";
import OrgApi, { type GetOrgTreeVo } from "@/views/core/api/OrgApi";

export default class OrgTreeService {
  /**
   * 组织架构树逻辑
   */
  public static useOrgTree() {
    const treeData = ref<GetOrgTreeVo[]>([]);
    const loading = ref(false);
    const filterText = ref("");
    const selectedOrg = ref<GetOrgTreeVo | null>(null);

    const onSelectOrg = (org: GetOrgTreeVo | null) => {
      selectedOrg.value = org;
    };

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
      selectedOrg,
      onSelectOrg,
    };
  }
}
