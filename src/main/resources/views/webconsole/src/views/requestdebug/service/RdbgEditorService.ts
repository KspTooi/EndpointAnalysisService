import { reactive, ref, watch } from "vue";
import type { GetCollectionDetailsVo } from "../api/CollectionApi";
import type { RdbgEditorProps } from "../components/RdbgEditor.vue";

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
  reqBodyJson: "",
  seq: 0,
});

export default {
  useEditor: (props: RdbgEditorProps, emits: (e: "on-details-change", details: GetCollectionDetailsVo) => void) => {
    const editor = reactive<GetCollectionDetailsVo>(getDefaultEditor());

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

      if (value.reqBodyJson) {
        editor.reqBodyJson = value.reqBodyJson;
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
        emits("on-details-change", value);
        console.log("updateEmitter", value);
      }, 300);
    };

    watch(
      () => editor,
      (newVal) => {
        if (propsUpdating) {
          return;
        }

        updateEmitter(newVal);
      },
      { deep: true }
    );

    return {
      editor,
      update,
    };
  },
};
