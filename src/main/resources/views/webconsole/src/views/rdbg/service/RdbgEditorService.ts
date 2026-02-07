import { computed, reactive, ref, watch } from "vue";
import type { GetCollectionDetailsVo } from "@/views/rdbg/api/CollectionApi";
import type { RdbgEditorProps } from "@/views/rdbg/components/RdbgEditor.vue";
import { useRdbgStore } from "@/views/rdbg/service/RdbgStore";

const rdbgStore = useRdbgStore();

const getDefaultEditor = (): GetCollectionDetailsVo => ({
  id: "",
  parentId: "",
  name: "",
  kind: 0,
  reqUrl: "",
  requestParams: [],
  reqMethod: 0,
  requestHeaders: [],
  reqBodyKind: 0,
  reqBody: {
    kind: 0,
    formData: [],
    formDataUrlEncoded: [],
    rawData: "",
    binaryData: "",
  },
  seq: 0,
});

export default {
  /**
   * 编辑器功能打包
   * @param props 属性
   * @param emits 事件发射器
   * @param debounceTime 防抖时间
   * @returns 编辑器
   */
  useEditor: (
    props: RdbgEditorProps,
    emits: (e: "on-details-change", details: GetCollectionDetailsVo) => void,
    debounceTime: number = 300
  ) => {
    //编辑器数据
    const editor = reactive<GetCollectionDetailsVo>(getDefaultEditor());

    //编辑器是否有未提交的更改(这是在编辑器内部更新时但外部未提交时会置为true)
    const hasUncommittedChanges = computed(() => {
      return rdbgStore.getUncommittedCollections.length > 0;
    });

    //防抖定时器
    let debounceTimer: ReturnType<typeof setTimeout> | null = null;
    let propsUpdating = false;

    /**
     * 外部更新时将值同步到编辑器
     * @param value 值
     */
    const update = (value: GetCollectionDetailsVo) => {
      //正在进行props更新
      propsUpdating = true;

      //初始化为默认
      Object.assign(editor, getDefaultEditor());

      editor.id = value.id;
      editor.parentId = value.parentId;

      if (value.name) {
        editor.name = value.name;
      }

      editor.kind = value.kind;

      if (value.reqUrl) {
        editor.reqUrl = value.reqUrl;
      }

      if (value.requestParams) {
        editor.requestParams = value.requestParams;
      }

      if (value.reqMethod) {
        editor.reqMethod = value.reqMethod;
      }

      if (value.requestHeaders) {
        editor.requestHeaders = value.requestHeaders;
      }

      if (value.reqBodyKind) {
        editor.reqBodyKind = value.reqBodyKind;
      }

      if (value.reqBody) {
        editor.reqBody = value.reqBody;
      }

      if (value.seq) {
        editor.seq = value.seq;
      }

      //更新完成
      setTimeout(() => {
        propsUpdating = false;
      }, 1);
    };

    watch(
      () => props.details,
      (newVal) => {
        if (newVal != null) {
          update(newVal);
        }
      }
    );

    /**
     * 内部更新时将值同步到外部（带防抖）
     * @param value 值
     */
    const updateEmitter = (value: GetCollectionDetailsVo) => {
      if (debounceTimer) {
        clearTimeout(debounceTimer);
      }

      debounceTimer = setTimeout(() => {
        //如果当前没有未提交的更改，则不再提交
        if (rdbgStore.getUncommittedCollections.length === 0) {
          return;
        }

        //提交成功后重置为false
        rdbgStore.clearUncommittedCollections();

        //提交到外部
        emits("on-details-change", value);
      }, debounceTime);
    };

    watch(
      () => editor,
      (newVal) => {
        if (propsUpdating) {
          return;
        }

        updateEmitter(newVal);
        //有未提交的更改
        rdbgStore.addUncommittedCollection({
          id: newVal.id,
          data: newVal,
          commitImmediately: () => {
            emits("on-details-change", newVal);
          },
        });
      },
      { deep: true }
    );

    return {
      hasUncommittedChanges,
      editor,
      update,
    };
  },
};
