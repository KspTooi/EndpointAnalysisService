import { onMounted, ref } from "vue";
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
    const opSchemaId = ref<string>(opSchema.id);
    const listLoading = ref(false);
    const blueprintList = ref<GetOpBluePrintListVo[]>([]);

    //是否显示未解析的文件名
    const showRawName = ref(false);

    const loadBlueprintList = async (): Promise<void> => {
      listLoading.value = true;
      try {
        blueprintList.value = await OpSchemaApi.getOpBluePrintList({ id: opSchemaId.value });
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

    return {
      //加载状态
      listLoading,

      //蓝图列表
      blueprintList,

      //是否显示未解析的文件名
      showRawName,

      //加载蓝图列表
      loadBlueprintList,
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
      //预览蓝图
      const blueprintOutput = await OpSchemaApi.previewOpBluePrint({ opSchemaId: opSchemaId, sha256Hex: vo.sha256Hex });
      codeContent.value = blueprintOutput;
      renderCode(blueprintOutput, vo.parsedName);

      previewFileName.value = vo.parsedName;
    };

    /**
     * 预览QBE模型
     * @param opSchemaId 输出方案ID
     */
    const previewQbeModel = async (opSchemaId: string): Promise<void> => {
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
};
