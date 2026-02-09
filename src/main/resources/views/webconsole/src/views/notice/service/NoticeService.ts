import { onMounted, reactive, ref, type Ref } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import type {
  GetNoticeListDto,
  GetNoticeListVo,
  GetNoticeDetailsVo,
  AddNoticeDto,
  EditNoticeDto,
} from "@/views/notice/api/NoticeApi.ts";
import NoticeApi from "@/views/notice/api/NoticeApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import QueryPersistService from "@/service/QueryPersistService";

/**
 * 模态框模式类型
 */
type ModalMode = "add" | "edit";

export default {
  /**
   * 消息表列表管理
   */
  useNoticeList() {
    const listForm = ref<GetNoticeListDto>({
      pageNum: 1,
      pageSize: 10,
      title: "",
      kind: null,
      content: "",
      priority: null,
      category: "",
      senderId: "",
      senderName: "",
      forward: "",
      params: "",
    });

    const listData = ref<GetNoticeListVo[]>([]);
    const listTotal = ref(0);
    const listLoading = ref(false);

    /**
     * 加载列表
     */
    const loadList = async () => {
      listLoading.value = true;
      const result = await NoticeApi.getNoticeList(listForm.value);

      if (Result.isSuccess(result)) {
        listData.value = result.data;
        listTotal.value = result.total;
        QueryPersistService.persistQuery("notice-list", listForm.value);
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
      listForm.value.title = "";
      listForm.value.kind = null;
      listForm.value.content = "";
      listForm.value.priority = null;
      listForm.value.category = "";
      listForm.value.senderId = "";
      listForm.value.senderName = "";
      listForm.value.forward = "";
      listForm.value.params = "";
      QueryPersistService.clearQuery("notice-list");
      loadList();
    };

    /**
     * 删除记录
     */
    const removeList = async (row: GetNoticeListVo) => {
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
        await NoticeApi.removeNotice({ id: row.id });
        ElMessage.success("删除成功");
        await loadList();
      } catch (error: any) {
        ElMessage.error(error.message);
      }
    };

    onMounted(async () => {
      QueryPersistService.loadQuery("notice-list", listForm.value);
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
   * 模态框管理（统一处理新增和编辑）
   */
  useNoticeModal(modalFormRef: Ref<FormInstance | undefined>, reloadCallback: () => void) {
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const modalMode = ref<ModalMode>("add");
    const modalForm = reactive<GetNoticeDetailsVo>({
      id: "",
      title: "",
      kind: 0,
      content: "",
      priority: 0,
      category: "",
      senderId: "",
      senderName: "",
      forward: "",
      params: "",
      createTime: "",
    });

    /**
     * 表单验证规则
     */
    const modalRules: FormRules = {
      title: [{ required: true, message: "请输入标题", trigger: "blur" }],
      kind: [{ required: true, message: "请输入种类: 0公告, 1业务提醒, 2私信", trigger: "blur" }],
      priority: [{ required: true, message: "请输入优先级: 0:低 1:中 2:高", trigger: "blur" }],
    };

    /**
     * 打开模态框
     * @param mode 模式: 'add' | 'edit'
     * @param row 编辑时传入的行数据
     */
    const openModal = async (mode: ModalMode, row: GetNoticeListVo | null) => {
      modalMode.value = mode;

      if (mode === "add") {
        modalForm.id = "";
        modalForm.title = "";
        modalForm.kind = 0;
        modalForm.content = "";
        modalForm.priority = 0;
        modalForm.category = "";
        modalForm.senderId = "";
        modalForm.senderName = "";
        modalForm.forward = "";
        modalForm.params = "";
        modalForm.createTime = "";
        modalVisible.value = true;
        return;
      }

      if (mode === "edit") {
        if (!row) {
          ElMessage.error("未选择要编辑的数据");
          return;
        }

        try {
          const details = await NoticeApi.getNoticeDetails({ id: row.id });
          modalForm.id = details.id;
          modalForm.title = details.title;
          modalForm.kind = details.kind;
          modalForm.content = details.content;
          modalForm.priority = details.priority;
          modalForm.category = details.category;
          modalForm.senderId = details.senderId;
          modalForm.senderName = details.senderName;
          modalForm.forward = details.forward;
          modalForm.params = details.params;
          modalForm.createTime = details.createTime;
          modalVisible.value = true;
        } catch (error: any) {
          ElMessage.error(error.message);
        }
      }
    };

    /**
     * 重置模态框
     */
    const resetModal = () => {
      if (!modalFormRef.value) {
        return;
      }
      modalFormRef.value.resetFields();
      modalForm.id = "";
      modalForm.title = "";
      modalForm.kind = 0;
      modalForm.content = "";
      modalForm.priority = 0;
      modalForm.category = "";
      modalForm.senderId = "";
      modalForm.senderName = "";
      modalForm.forward = "";
      modalForm.params = "";
      modalForm.createTime = "";
    };

    /**
     * 提交模态框
     */
    const submitModal = async () => {
      if (!modalFormRef.value) {
        return;
      }

      try {
        await modalFormRef.value.validate();
      } catch (error) {
        return;
      }

      modalLoading.value = true;

      if (modalMode.value === "add") {
        try {
          const addDto: AddNoticeDto = {
            title: modalForm.title,
            kind: modalForm.kind,
            content: modalForm.content,
            priority: modalForm.priority,
            category: modalForm.category,
            senderId: modalForm.senderId,
            senderName: modalForm.senderName,
            forward: modalForm.forward,
            params: modalForm.params,
          };
          await NoticeApi.addNotice(addDto);
          ElMessage.success("新增成功");
          modalVisible.value = false;
          resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
        return;
      }

      if (modalMode.value === "edit") {
        if (!modalForm.id) {
          ElMessage.error("缺少ID参数");
          modalLoading.value = false;
          return;
        }

        try {
          const editDto: EditNoticeDto = {
            id: modalForm.id,
            title: modalForm.title,
            kind: modalForm.kind,
            content: modalForm.content,
            priority: modalForm.priority,
            category: modalForm.category,
            senderId: modalForm.senderId,
            senderName: modalForm.senderName,
            forward: modalForm.forward,
            params: modalForm.params,
          };
          await NoticeApi.editNotice(editDto);
          ElMessage.success("编辑成功");
          modalVisible.value = false;
          resetModal();
          reloadCallback();
        } catch (error: any) {
          ElMessage.error(error.message);
        }
        modalLoading.value = false;
      }
    };

    return {
      modalVisible,
      modalLoading,
      modalMode,
      modalForm,
      modalRules,
      openModal,
      resetModal,
      submitModal,
    };
  },
};
