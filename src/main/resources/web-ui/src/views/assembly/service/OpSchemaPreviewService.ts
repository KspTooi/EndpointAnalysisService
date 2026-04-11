import { onMounted, ref, type Ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import hljs from "highlight.js";
import "highlight.js/styles/atom-one-dark.css";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext.ts";
import OpSchemaApi from "@/views/assembly/api/OpSchemaApi";
import type { GetOpBluePrintListVo, GetOpSchemaListVo } from "@/views/assembly/api/OpSchemaApi";

export default {
  /**
   * 蓝图列表打包
   */
  useBlueprintList(opSchema: GetOpSchemaListVo, cdrcReturn: () => void) {
    //输出方案ID
    const opSchemaId = ref<string>(opSchema.id);

    //蓝图列表加载状态
    const listLoading = ref(false);

    //蓝图列表
    const blueprintList = ref<GetOpBluePrintListVo[]>([]);

    //是否显示未解析的文件名
    const showRawName = ref(false);

    //已选中的蓝图列表
    const checkedBlueprints = ref<GetOpBluePrintListVo[]>([]);

    const loadBlueprintList = async (): Promise<void> => {
      listLoading.value = true;
      try {
        blueprintList.value = await OpSchemaApi.getOpBluePrintList({ id: opSchemaId.value });

        //成功拉取数据后，自动选中所有蓝图
        checkedBlueprints.value = [...blueprintList.value];
      } catch (error: any) {
        await ElMessageBox.confirm(error.message, "提示", {
          confirmButtonText: "确定",
          showCancelButton: false,
          showClose: false,
          closeOnClickModal: false,
          closeOnPressEscape: false,
          type: "warning",
        });
        cdrcReturn();
      } finally {
        listLoading.value = false;
      }
    };

    onMounted(async () => {
      if (!opSchema) {
        return;
      }
      opSchemaId.value = opSchema.id;
      await loadBlueprintList();
    });

    /**
     * 切换已选中蓝图
     * @param vo 蓝图VO
     */
    const toggleCheckedBlueprint = (vo: GetOpBluePrintListVo): void => {
      //如果已选中，则取消选中
      if (checkedBlueprints.value.some((item) => item.sha256Hex === vo.sha256Hex)) {
        checkedBlueprints.value = checkedBlueprints.value.filter((item) => item.sha256Hex !== vo.sha256Hex);
        return;
      }

      //如果未选中，则选中
      if (!checkedBlueprints.value.some((item) => item.sha256Hex === vo.sha256Hex)) {
        checkedBlueprints.value.push(vo);
      }
    };

    /**
     * 是否已选中蓝图
     * @param vo 蓝图VO
     * @returns 是否已选中
     */
    const isChecked = (vo: GetOpBluePrintListVo): boolean => {
      return checkedBlueprints.value.some((item) => item.sha256Hex === vo.sha256Hex);
    };

    /**
     * 全选
     */
    const checkedAll = (): void => {
      checkedBlueprints.value = [...blueprintList.value];
    };

    /**
     * 清空已选
     */
    const clearSelected = (): void => {
      checkedBlueprints.value = [];
    };

    return {
      //加载状态
      listLoading,

      //蓝图列表
      blueprintList,

      //是否显示未解析的文件名
      showRawName,

      //加载蓝图列表
      loadBlueprintList,

      //已选中的蓝图列表
      checkedBlueprints,

      //切换已选中蓝图
      toggleCheckedBlueprint,

      //是否已选中蓝图
      isChecked,

      //全选
      checkedAll,

      //清空已选
      clearSelected,
    };
  },

  /**
   * 蓝图预览打包
   */
  useBlueprintPreview() {
    const codeContent = ref<string>("");
    const rawHtml = ref<string>("");
    const detectedLanguage = ref<string>("");
    const previewLoading = ref(false);

    //正在预览的文件名
    const previewFileName = ref<string>("");

    //正在预览的蓝图已被删除
    const previewBlueprintDeleted = ref<boolean>(false);

    const getLanguageByExt = (filename: string): string => {
      const ext = filename.split(".").pop()?.toLowerCase();
      const map: Record<string, string> = {
        js: "javascript",
        ts: "typescript",
        vue: "xml",
        html: "xml",
        css: "css",
        scss: "scss",
        json: "json",
        java: "java",
        py: "python",
        go: "go",
        c: "c",
        cpp: "cpp",
        cs: "csharp",
        php: "php",
        sql: "sql",
        md: "markdown",
        sh: "bash",
        yml: "yaml",
        xml: "xml",
      };
      return ext ? map[ext] || "" : "";
    };

    const generateLineNumbers = (html: string): string => {
      const lines = html.split(/\r\n|\r|\n/);
      if (lines[lines.length - 1] === "") {
        lines.pop();
      }
      return lines.map((line) => `<div class="code-line">${line || " "}</div>`).join("");
    };

    /**
     * 渲染代码
     * @param code 代码
     * @param fileName 文件名
     */
    const renderCode = (code: string, fileName: string): void => {
      const lang = getLanguageByExt(fileName);
      let result;

      if (lang && hljs.getLanguage(lang)) {
        result = hljs.highlight(code, { language: lang });
        detectedLanguage.value = lang;
        rawHtml.value = generateLineNumbers(result.value);
        return;
      }

      result = hljs.highlightAuto(code);
      detectedLanguage.value = result.language || "text";
      rawHtml.value = generateLineNumbers(result.value);
    };

    /**
     * 清空预览
     */
    const clearPreview = (): void => {
      codeContent.value = "";
      rawHtml.value = "";
      detectedLanguage.value = "";
    };

    /**
     * 预览蓝图
     * @param vo 蓝图VO
     * @param opSchemaId 输出方案ID
     */
    const previewBlueprint = async (vo: GetOpBluePrintListVo, opSchemaId: string): Promise<void> => {
      try {
        previewBlueprintDeleted.value = false;
        previewLoading.value = true;
        //清空预览
        clearPreview();

        //预览蓝图
        const blueprintOutput = await OpSchemaApi.previewOpBluePrint({ opSchemaId: opSchemaId, sha256Hex: vo.sha256Hex });
        codeContent.value = blueprintOutput;
        renderCode(blueprintOutput, vo.parsedName);

        previewFileName.value = vo.parsedName;
      } catch {
        previewBlueprintDeleted.value = true;
      } finally {
        previewLoading.value = false;
      }
    };

    /**
     * 预览QBE模型
     * @param opSchemaId 输出方案ID
     */
    const previewQbeModel = async (opSchemaId: string): Promise<void> => {
      //清空预览
      clearPreview();
      const qbeModelJson = await OpSchemaApi.previewQbeModel({ id: opSchemaId });
      codeContent.value = qbeModelJson;

      //格式化JSON
      const formattedJson = JSON.stringify(JSON.parse(qbeModelJson), null, 2);
      renderCode(formattedJson, "qbe_model.json");

      previewFileName.value = "QBE模型";
    };

    const onCopy = async (): Promise<void> => {
      if (!codeContent.value) {
        return;
      }
      try {
        await navigator.clipboard.writeText(codeContent.value);
        ElMessage.success("代码已复制到剪贴板");
      } catch {
        ElMessage.error("复制失败");
      }
    };

    return {
      //代码内容
      codeContent,

      //代码高亮后的HTML
      rawHtml,

      //代码语言
      detectedLanguage,

      //加载状态
      previewLoading,

      //正在预览的蓝图已被删除
      previewBlueprintDeleted,

      //正在预览的文件名
      previewFileName,

      //预览蓝图
      previewBlueprint,

      //预览QBE模型
      previewQbeModel,

      //清空预览
      clearPreview,

      //复制代码
      onCopy,
    };
  },

  /**
   * 操作栏打包
   * @param schema 输出方案
   * @param selectedKey 当前选中蓝图的key
   * @param selectedBlueprint 当前选中的蓝图
   * @param loadBlueprintList 加载蓝图列表
   * @param previewBlueprint 预览蓝图
   * @param previewQbeModel 预览QBE模型
   * @param clearPreview 清空预览
   */
  useActionBar(
    schema: GetOpSchemaListVo,
    selectedKey: Ref<string>,
    selectedBlueprint: Ref<GetOpBluePrintListVo | null>,
    loadBlueprintList: () => Promise<void>,
    previewBlueprint: (vo: GetOpBluePrintListVo, opSchemaId: string) => Promise<void>,
    previewQbeModel: (opSchemaId: string) => Promise<void>
  ) {
    /**
     * 刷新蓝图
     */
    const refreshBlueprint = async (): Promise<void> => {
      //重新加载蓝图列表
      await loadBlueprintList();

      //判断当前选的是什么
      if (!selectedKey.value) {
        return;
      }

      //如果是QBE模型，则预览QBE模型
      if (selectedKey.value === "__qbe_model__") {
        previewQbeModel(schema.id);
        return;
      }

      //如果是蓝图，则预览蓝图
      previewBlueprint(selectedBlueprint.value, schema.id);
    };

    /**
     * 执行已选蓝图
     */
    const executeSelectedBlueprint = async (vos: GetOpBluePrintListVo[]): Promise<void> => {
      console.log("执行已选蓝图: " + vos.length);
      console.log(vos);
    };

    return {
      //刷新蓝图
      refreshBlueprint,

      //执行已选蓝图
      executeSelectedBlueprint,
    };
  },
};
