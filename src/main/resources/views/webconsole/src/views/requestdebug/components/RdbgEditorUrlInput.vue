<template>
  <div class="url-input">
    <div class="url-input-url">
      <div class="el-input el-input-group el-input-group--prepend">
        <div class="el-input-group__prepend">
          <!-- 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS -->
          <el-select v-model="method" placeholder="请选择请求方法" :filterable="true" style="width: 110px">
            <el-option label="GET" :value="0" />
            <el-option label="POST" :value="1" />
            <el-option label="PUT" :value="2" />
            <el-option label="PATCH" :value="3" />
            <el-option label="DELETE" :value="4" />
            <el-option label="HEAD" :value="5" />
            <el-option label="OPTIONS" :value="6" />
          </el-select>
        </div>
        <div ref="urlInputRef" class="el-input__inner" contenteditable="true" @input="onInput" v-html="highlightedUrl"></div>
      </div>
    </div>
    <div class="url-input-send">
      <el-button type="primary" @click="onRequestSend" :disabled="props.loading">发送</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, nextTick } from "vue";

const urlInputRef = ref<HTMLDivElement | null>(null);

const method = defineModel<number>("method", { required: true });
const url = defineModel<string>("url", { required: true });

const props = defineProps<{
  loading: boolean;
}>();

const emit = defineEmits<{
  (event: "on-request-send"): void;
}>();

const localUrl = ref(url.value);

const highlightedUrl = computed(() => {
  return localUrl.value.replace(/#\{[^}]+}/g, (match) => {
    return `<span class="highlight">${match}</span>`;
  });
});

const setCaretPosition = (el: HTMLElement, pos: number) => {
  const range = document.createRange();
  const sel = window.getSelection();
  if (el.childNodes.length > 0) {
    let charCount = 0;
    let found = false;
    for (const node of Array.from(el.childNodes)) {
      if (found) break;
      if (node.nodeType === 3) {
        // Text node
        const textLength = node.textContent?.length || 0;
        if (charCount + textLength >= pos) {
          range.setStart(node, pos - charCount);
          found = true;
        }
        charCount += textLength;
      } else if (node.nodeType === 1) {
        // Element node
        const textLength = node.textContent?.length || 0;
        if (charCount + textLength >= pos) {
          // This is a simplified handling for elements, might need recursion for complex structures
          const textNode = Array.from(node.childNodes).find((n) => n.nodeType === 3);
          if (textNode) {
            range.setStart(textNode, pos - charCount);
            found = true;
          }
        }
        charCount += textLength;
      }
    }
    if (!found) {
      // Fallback to end of last text node if position is out of bounds
      const lastTextNode = Array.from(el.childNodes)
        .reverse()
        .find((n) => n.nodeType === 3);
      if (lastTextNode) {
        range.setStart(lastTextNode, lastTextNode.textContent?.length || 0);
      }
    }
  }
  range.collapse(true);
  sel?.removeAllRanges();
  sel?.addRange(range);
};

const getCaretPosition = (el: HTMLElement) => {
  let caretOffset = 0;
  const sel = window.getSelection();
  if (sel && sel.rangeCount > 0) {
    const range = sel.getRangeAt(0);
    const preCaretRange = range.cloneRange();
    preCaretRange.selectNodeContents(el);
    preCaretRange.setEnd(range.endContainer, range.endOffset);
    caretOffset = preCaretRange.toString().length;
  }
  return caretOffset;
};

const onInput = (event: Event) => {
  const target = event.target as HTMLDivElement;
  const pos = getCaretPosition(target);
  localUrl.value = target.innerText;

  nextTick(() => {
    if (urlInputRef.value) {
      setCaretPosition(urlInputRef.value, pos);
    }
  });
};

onMounted(() => {
  if (urlInputRef.value) {
    urlInputRef.value.innerText = localUrl.value;
  }
});

const onRequestSend = async () => {
  await emit("on-request-send");
};

//监听外部url变化
watch(
  () => url.value,
  (newVal) => {
    if (localUrl.value !== newVal) {
      localUrl.value = newVal;
      if (urlInputRef.value) {
        urlInputRef.value.innerText = newVal;
      }
    }
  }
);

//监听localUrl变化
watch(localUrl, () => {
  url.value = localUrl.value;
});
</script>

<style scoped>
.url-input {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  gap: 10px;
  background-color: #fff;
}

.url-input-url {
  width: 100%;
}

.url-input-url > .el-input {
  display: flex;
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-radius: var(--el-border-radius-base);
  transition: border-color var(--el-transition-duration);
}
.url-input-url > .el-input:hover,
.url-input-url > .el-input:focus-within {
  border-color: var(--el-color-primary);
}

:deep(.url-input-url .el-input-group__prepend) {
  border-right: 1px solid var(--el-border-color);
  background-color: transparent;
  box-shadow: none !important;
}

:deep(.url-input-url .el-input-group__prepend .el-select .el-input__wrapper) {
  box-shadow: none !important;
  border-right: 1px solid var(--el-border-color);
  border-radius: 0;
}

:deep(.url-input-url .el-input-group__prepend .el-select .el-select__wrapper) {
  box-shadow: none !important;
}

.el-input__inner[contenteditable="true"] {
  min-height: 32px;
  height: auto;
  line-height: 1.5;
  padding-top: 5px;
  padding-bottom: 5px;
  padding-left: 12px;
  flex-grow: 1;
}
:deep(.highlight) {
  color: var(--el-color-info);
}
</style>
