<template>
  <el-dialog
    v-model="visible"
    title="安全验证"
    width="360px"
    :close-on-click-modal="false"
    :show-close="true"
    destroy-on-close
    append-to-body
    class="com-tac-captcha-dialog"
    @close="handleDialogClose"
  >
    <div :id="captchaBindId" class="captcha-box"></div>
  </el-dialog>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref } from "vue";

declare global {
  interface Window {
    initTAC?: (
      path: string | Record<string, unknown>,
      config: Record<string, unknown>,
      style?: Record<string, unknown>,
    ) => Promise<any>;
  }
}

const emit = defineEmits<{
  (e: "on-success", payload: any): void;
  (e: "on-error", message: string): void;
  (e: "on-close"): void;
}>();

const visible = ref(false);
const tacInstance = ref<any>(null);
const captchaBindId = `com-tac-captcha-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`;

const closeModal = () => {
  if (tacInstance.value?.destroyWindow) {
    tacInstance.value.destroyWindow();
  }

  visible.value = false;
};

const handleDialogClose = () => {
  if (tacInstance.value?.destroyWindow) {
    tacInstance.value.destroyWindow();
  }

  emit("on-close");
};

const buildCaptchaConfig = () => {
  return {
    requestCaptchaDataUrl: "/api/auth/genCaptcha",
    validCaptchaUrl: "/api/auth/check",
    bindEl: `#${captchaBindId}`,
    validSuccess: (res: any, _captcha: any, tac: any) => {
      if (tac?.destroyWindow) {
        tac.destroyWindow();
      }

      visible.value = false;
      emit("on-success", res);
    },
    validFail: (_res: any, _captcha: any, tac: any) => {
      if (!tac?.reloadCaptcha) {
        return;
      }

      tac.reloadCaptcha();
    },
    btnRefreshFun: (_el: any, tac: any) => {
      if (!tac?.reloadCaptcha) {
        return;
      }

      tac.reloadCaptcha();
    },
    btnCloseFun: (_el: any, tac: any) => {
      if (tac?.destroyWindow) {
        tac.destroyWindow();
      }

      visible.value = false;
      emit("on-close");
    },
  };
};

const initCaptcha = async () => {
  if (!window.initTAC) {
    emit("on-error", "验证码脚本未加载");
    return;
  }

  if (tacInstance.value?.init) {
    tacInstance.value.init();
    return;
  }

  try {
    tacInstance.value = await window.initTAC(
      {
        scriptUrls: ["/js/tac.min.js"],
        cssUrls: ["/css/tac.css"],
      },
      buildCaptchaConfig(),
      {
        logoUrl: null,
      },
    );
    tacInstance.value.init();
  } catch (_error) {
    emit("on-error", "验证码初始化失败");
  }
};

const openModal = async () => {
  visible.value = true;
  await nextTick();
  await initCaptcha();
};

onBeforeUnmount(() => {
  if (!tacInstance.value?.destroyWindow) {
    return;
  }

  tacInstance.value.destroyWindow();
});

defineExpose({
  openModal,
  closeModal,
});
</script>

<style scoped>
.captcha-box {
  width: 318px;
  margin: 0 auto;
}

.com-tac-captcha-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
  padding-bottom: 16px;
}
</style>
