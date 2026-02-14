<template>
  <div class="login-page">
    <!-- 亮色系数字化蓝图背景 -->
    <div class="bg-grid"></div>
    <div class="bg-dots"></div>

    <div class="login-wrapper">
      <!-- 动态扫描线 (青色光束) -->
      <div class="scan-line"></div>

      <div class="login-panel">
        <header class="panel-header">
          <div class="brand-box">
            <div class="brand-square"></div>
          </div>
          <h1 class="system-title">ENDPOINT ANALYSIS</h1>
          <p class="system-desc">EAS 系统登录</p>
        </header>

        <main class="panel-body">
          <div class="form-item">
            <div class="item-header">ACCOUNT ID</div>
            <el-input
              v-model="loginForm.username"
              placeholder="输入您的账户 ID"
              :prefix-icon="User"
              clearable
              @keyup.enter="onLogin"
            />
          </div>

          <div class="form-item">
            <div class="item-header">密钥</div>
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="输入您的安全密钥"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="onLogin"
            />
          </div>

          <transition name="slide-up">
            <div v-if="errorMessage" class="error-notification">
              <span class="err-tag">ERR_CODE_01:</span>
              {{ errorMessage }}
            </div>
          </transition>

          <div id="captcha-box"></div>

          <div class="button-container">
            <el-button type="primary" class="auth-button" :loading="isLoading" @click="onLogin">
              {{ isLoading ? "正在处理" : "登录" }}
            </el-button>
          </div>
        </main>

        <footer class="panel-footer">
          <span class="version-tag">CORE v2.5.0-LITE</span>
          <a class="nav-link" @click="onRegister">申请系统访问权限</a>
        </footer>
      </div>
    </div>

    <!-- 装饰性脚注 -->
    <aside class="side-info left">127.0.0.1 / SECURED</aside>
    <aside class="side-info right">UTC+8:00 / ACTIVE</aside>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import UserAuthService from "@/views/auth/service/UserAuthService";
import type { UserLoginDto } from "./api/AuthApi";
import Http from "@/commons/Http";

declare global {
  interface Window {
    initTAC?: (path: string | Record<string, unknown>, config: Record<string, unknown>, style?: Record<string, unknown>) => Promise<any>;
  }
}

const router = useRouter();
const { login } = UserAuthService.useUserAuth();

// 表单数据
const loginForm = ref<UserLoginDto>({
  username: "",
  password: "",
});

// 错误信息
const errorMessage = ref<string>("");

// 加载状态
const isLoading = ref<boolean>(false);

// 验证码实例
const tacInstance = ref<any>(null);

/**
 * 执行登录
 */
const doLogin = async () => {
  if (isLoading.value) {
    return;
  }

  isLoading.value = true;

  try {
    await login(loginForm.value.username, loginForm.value.password);
    ElMessage.success("用户验证通过");
    await router.push({ path: "/" });
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "网络同步异常，同步失败";
  } finally {
    isLoading.value = false;
  }
};

/**
 * 初始化并展示验证码
 */
const openCaptcha = async () => {
  if (!window.initTAC) {
    errorMessage.value = "验证码组件未加载，请刷新页面重试";
    return;
  }

  if (tacInstance.value) {
    tacInstance.value.init();
    return;
  }

  const config = {
    // 生成接口 (必选项,必须配置, 要符合tianai-captcha默认验证码生成接口规范)
    requestCaptchaDataUrl: Http.resolve("/auth/genCaptcha"),
    // 验证接口 (必选项,必须配置, 要符合tianai-captcha默认验证码校验接口规范)
    validCaptchaUrl: Http.resolve("/auth/check"),
    // 验证码绑定的div块 (必选项,必须配置)
    bindEl: "#captcha-box",
    // 验证成功回调函数(必选项,必须配置)
    validSuccess: async (_res, _c, tac) => {
      // 销毁验证码服务
      tac.destroyWindow();
      await doLogin();
    },
    // 验证失败的回调函数(可忽略，如果不自定义 validFail 方法时，会使用默认的)
    validFail: (_res, _c, tac) => {
      // 验证失败后重新拉取验证码
      tac.reloadCaptcha();
    },
    // 刷新按钮回调事件
    btnRefreshFun: (_el, tac) => {
      tac.reloadCaptcha();
    },
    // 关闭按钮回调事件
    btnCloseFun: (_el, tac) => {
      tac.destroyWindow();
    },
  };
  // 一些样式配置， 可不传
  let style = {
    logoUrl: null, // 去除logo
  };
  // 参数1 为 tac文件是目录地址， 目录里包含 tac的js和css等文件
  // 参数2 为 tac验证码相关配置
  // 参数3 为 tac窗口一些样式配置
  try {
    tacInstance.value = await window.initTAC(
      {
        scriptUrls: ["/js/tac.min.js"],
        cssUrls: ["/css/tac.css"],
      },
      config,
      style,
    );
    tacInstance.value.init(); // 调用init则显示验证码
  } catch (_error) {
    errorMessage.value = "初始化验证码失败，请稍后重试";
  }
};

/**
 * 处理登录逻辑
 */
const onLogin = async () => {
  errorMessage.value = "";

  if (!loginForm.value.username) {
    errorMessage.value = "请提供账户标识";
    return;
  }

  if (!loginForm.value.password) {
    errorMessage.value = "请提供安全密钥";
    return;
  }

  await openCaptcha();
};

/**
 * 跳转注册
 */
const onRegister = () => {
  router.push({ name: "register" });
};
</script>

<style scoped>
.login-page {
  /* 定义亮色设计变量 */
  --p-main: #009688;
  --p-bg: #f8fafc;
  --p-panel: #ffffff;
  --p-border: #e2e8f0;
  --p-text: #1e293b;
  --p-text-light: #64748b;
  --p-input: #fcfcfc;
}

.login-page {
  width: 100vw;
  height: 100vh;
  background-color: var(--p-bg);
  color: var(--p-text);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  font-family: "PingFang SC", "Segoe UI", "Consolas", sans-serif;
}

/* 数字化蓝图背景 */
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(to right, rgba(0, 150, 136, 0.05) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(0, 150, 136, 0.05) 1px, transparent 1px);
  background-size: 40px 40px;
  z-index: 1;
}

.bg-dots {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(0, 150, 136, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  z-index: 2;
}

/* 扫描线效果 */
.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(0, 150, 136, 0.4), transparent);
  box-shadow: 0 0 10px rgba(0, 150, 136, 0.2);
  animation: scan 5s infinite ease-in-out;
  z-index: 11;
  opacity: 0.8;
}

@keyframes scan {
  0% {
    transform: translateY(0);
    opacity: 0;
  }
  20% {
    opacity: 1;
  }
  80% {
    opacity: 1;
  }
  100% {
    transform: translateY(400px);
    opacity: 0;
  }
}

/* 核心面板 (硬朗直角风格) */
.login-wrapper {
  position: relative;
  z-index: 10;
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-panel {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border: 1px solid var(--p-border);
  box-shadow: 0 40px 80px -20px rgba(0, 0, 0, 0.1);
  padding: 48px;
  position: relative;
  z-index: 12;
}

/* 顶部品牌设计 */
.panel-header {
  text-align: center;
  margin-bottom: 40px;
}

.brand-box {
  width: 44px;
  height: 44px;
  border: 1.5px solid var(--p-main);
  margin: 0 auto 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-square {
  width: 18px;
  height: 18px;
  background: var(--p-main);
}

.system-title {
  font-size: 1.25rem;
  font-weight: 600;
  letter-spacing: 3px;
  margin: 0;
  color: var(--p-text);
}

.system-desc {
  font-size: 0.7rem;
  color: var(--p-text-light);
  margin-top: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  font-weight: 500;
}

/* 状态栏 */
.auth-infobar {
  border-top: 1px solid var(--p-border);
  border-bottom: 1px solid var(--p-border);
  padding: 10px 0;
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 12px;
  font-size: 0.65rem;
  font-weight: 600;
  letter-spacing: 1px;
}

.info-label {
  color: var(--p-text-light);
}

.info-value {
  color: var(--p-main);
}

.blink {
  animation: blink-anim 0.8s infinite;
}

@keyframes blink-anim {
  50% {
    opacity: 0.2;
  }
}

/* 表单逻辑 */
.form-item {
  margin-bottom: 24px;
}

.item-header {
  font-size: 0.7rem;
  color: var(--p-text-light);
  margin-bottom: 10px;
  font-weight: bold;
  letter-spacing: 0.5px;
}

/* Element Plus 重写为硬核直角 */
:deep(.el-input__wrapper) {
  background-color: var(--p-input) !important;
  box-shadow: none !important;
  border: 1px solid var(--p-border) !important;
  border-radius: 0 !important; /* 强制直角 */
  padding: 6px 14px !important;
  transition: all 0.3s;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: var(--p-main) !important;
  background-color: #fff !important;
  box-shadow: 0 4px 12px rgba(0, 150, 136, 0.1) !important;
}

:deep(.el-input__inner) {
  color: var(--p-text) !important;
  font-weight: 500;
}

/* 错误通知栏 */
.error-notification {
  margin-bottom: 20px;
  font-size: 0.75rem;
  color: #ef4444;
  background: #fef2f2;
  padding: 10px 15px;
  border-left: 3px solid #ef4444;
}

.err-tag {
  font-weight: bold;
  margin-right: 8px;
}

/* 极简直角按钮 */
.auth-button {
  width: 100%;
  height: 52px;
  border-radius: 0 !important; /* 彻底直角 */
  background-color: var(--p-main) !important;
  border: none !important;
  font-size: 0.85rem;
  font-weight: bold;
  letter-spacing: 1px;
  text-transform: uppercase;
  transition: all 0.3s;
  box-shadow: 0 8px 16px -4px rgba(0, 150, 136, 0.3);
}

.auth-button:hover {
  filter: brightness(1.05);
  box-shadow: 0 12px 24px -6px rgba(0, 150, 136, 0.4);
  transform: translateY(-1px);
}

.auth-button:active {
  transform: translateY(1px);
}

/* 底部操作 */
.panel-footer {
  margin-top: 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.65rem;
}

.version-tag {
  color: var(--p-text-light);
  font-family: "Consolas", monospace;
  letter-spacing: 1px;
}

.nav-link {
  color: var(--p-main);
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}

.nav-link:hover {
  text-decoration: underline;
}

/* 侧边装饰文本 */
.side-info {
  position: absolute;
  bottom: 24px;
  font-family: "Consolas", monospace;
  font-size: 0.6rem;
  font-weight: 600;
  color: var(--p-text-light);
  letter-spacing: 2px;
  opacity: 0.5;
}

.side-info.left {
  left: 40px;
}
.side-info.right {
  right: 40px;
}

/* 动画效果 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
