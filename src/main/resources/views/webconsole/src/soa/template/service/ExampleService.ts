import { onMounted, ref } from "vue";
import type {
  GetExampleListDto,
  GetExampleListVo,
  AddExampleDto,
  EditExampleDto,
} from "@/soa/template/api/ExampleApi.ts";
import ExampleApi from "@/soa/template/api/ExampleApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService";

export default {
  /**
   * 示例列表管理
   */
  useExampleList() {
    const listForm = ref<GetExampleListDto>({
      pageNum: 1,
      pageSize: 10,
      name: "",
      status: null,
    });

    const listData = ref<GetExampleListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await ExampleApi.getExampleList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
        QueryPersistService.persistQuery("example-list", listForm.value);
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message);
      }

      listLoading.value = false;
    };

    /**
     * 重置查询
     */
    const resetList = () => {
      listForm.value.pageNum = 1;
      listForm.value.pageSize = 10;
      listForm.value.name = "";
      listForm.value.status = null;
      QueryPersistService.clearQuery("example-list");
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetExampleListVo) => {
      try {
        await ElMessageBox.confirm("确定删除该条记录吗？", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        });
      } catch (error) {
        return;
      }

      try {
        await ExampleApi.removeExample({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
      QueryPersistService.loadQuery("example-list", listForm.value);
      await loadList();
    });

    return {
      listForm,
      listData,
      listTotal,
      listLoading,
      loadList,
      resetList,
      removeList,
    };
  },

  /**
   * 新增示例管理
   */
  useAddExample() {
    const addDialogVisible = ref(false);
    const addForm = ref<AddExampleDto>({
      name: "",
      description: "",
      status: 0,
    });
    const addLoading = ref(false);

    /**
     * 打开新增对话框
     */
    const openAddDialog = () => {
      addForm.value = {
        name: "",
        description: "",
        status: 0,
      };
      addDialogVisible.value = true;
    };

    /**
     * 关闭新增对话框
     */
    const closeAddDialog = () => {
      addDialogVisible.value = false;
    };

    /**
     * 提交新增
     */
    const submitAdd = async (callback?: () => void) => {
      addLoading.value = true;

      try {
        await ExampleApi.addExample(addForm.value);
        ElMessage.success("新增成功");
        closeAddDialog();
        if (callback) {
          callback();
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      }

      addLoading.value = false;
    };

    return {
      addDialogVisible,
      addForm,
      addLoading,
      openAddDialog,
      closeAddDialog,
      submitAdd,
    };
  },

  /**
   * 编辑示例管理
   */
  useEditExample() {
    const editDialogVisible = ref(false);
    const editForm = ref<EditExampleDto>({
      id: "",
      name: "",
      description: "",
      status: 0,
    });
    const editLoading = ref(false);

    /**
     * 打开编辑对话框
     */
    const openEditDialog = async (row: GetExampleListVo) => {
      try {
        const details = await ExampleApi.getExampleDetails({ id: row.id });
        editForm.value = {
          id: details.id,
          name: details.name,
          description: details.description,
          status: details.status,
        };
        editDialogVisible.value = true;
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    /**
     * 关闭编辑对话框
     */
    const closeEditDialog = () => {
      editDialogVisible.value = false;
    };

    /**
     * 提交编辑
     */
    const submitEdit = async (callback?: () => void) => {
      editLoading.value = true;

      try {
        await ExampleApi.editExample(editForm.value);
        ElMessage.success("编辑成功");
        closeEditDialog();
        if (callback) {
          callback();
        }
      } catch (error: any) {
        ElMessage.error(error.message);
      }

      editLoading.value = false;
    };

    return {
      editDialogVisible,
      editForm,
      editLoading,
      openEditDialog,
      closeEditDialog,
      submitEdit,
    };
  },
};
