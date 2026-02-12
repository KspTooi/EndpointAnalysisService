<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import AuthApi from "./api/AuthApi";
import type { UserLoginDto } from "./api/AuthApi";

const router = useRouter();

// 表单数据
const loginForm = ref<UserLoginDto>({
  username: "",
  password: "",
});

// 错误信息
const errorMessage = ref<string>("");

// 加载状态
const isLoading = ref<boolean>(false);

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 清空错误信息
  errorMessage.value = "";

  // 表单验证
  if (!loginForm.value.username) {
    errorMessage.value = "请输入用户名";
    return;
  }

  if (!loginForm.value.password) {
    errorMessage.value = "请输入密码";
    return;
  }

  // 开始加载
  isLoading.value = true;

  try {
    const result = await AuthApi.userLogin(loginForm.value);

    // 检查响应
    if (result.code === 0 && result.data) {
      // 保存sessionId到localStorage
      if (result.data.sessionId) {
        localStorage.setItem("sessionId", result.data.sessionId);
      }

      // 保存用户信息
      localStorage.setItem("userInfo", JSON.stringify(result.data));

      ElMessage.success("登录成功");

      // 跳转到首页
      await router.push({ name: "home" });
      return;
    }

    // 登录失败
    if (result.message) {
      errorMessage.value = result.message;
      return;
    }

    errorMessage.value = "登录失败，请重试";
  } catch (error) {
    if (error instanceof Error) {
      errorMessage.value = error.message;
      return;
    }
    errorMessage.value = "登录失败，请检查网络连接";
  } finally {
    isLoading.value = false;
  }
};

/**
 * 处理注册跳转
 */
const handleRegister = () => {
  router.push({ name: "register" });
};
</script>

<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="login-background"></div>

    <!-- 登录卡片 -->
    <div class="login-wrapper">
      <div class="login-card">
        <!-- 品牌标题 -->
        <div class="brand-header">
          <h1 class="brand-title">端点分析服务</h1>
          <div class="brand-subtitle">安全接入网关</div>
        </div>

        <!-- 登录表单 -->
        <form @submit.prevent="handleLogin">
          <!-- 用户名输入 -->
          <div class="form-group">
            <label class="form-label" for="username">账户 ID</label>
            <div class="form-input-wrapper">
              <input
                id="username"
                v-model="loginForm.username"
                autocomplete="username"
                class="form-input"
                placeholder="请输入您的账户"
                required
                type="text"
              />
            </div>
          </div>

          <!-- 密码输入 -->
          <div class="form-group">
            <label class="form-label" for="password">密钥</label>
            <div class="form-input-wrapper">
              <input
                id="password"
                v-model="loginForm.password"
                autocomplete="current-password"
                class="form-input"
                placeholder="请输入您的密码"
                required
                type="password"
              />
            </div>
          </div>

          <!-- 错误提示 -->
          <div v-if="errorMessage" class="error-alert">
            <span>{{ errorMessage }}</span>
          </div>

          <!-- 登录按钮 -->
          <button :disabled="isLoading" class="btn-submit" type="submit">
            {{ isLoading ? "登录中..." : "立即登录" }}
          </button>

          <!-- 注册链接 -->
          <div class="register-link">
            还没有账号？ <a @click.prevent="handleRegister">立即注册</a>
          </div>
        </form>
      </div>
    </div>

    <!-- 页脚 -->
    <div class="footer-copy">&copy; 2026 KspTool. All Rights Reserved.</div>
  </div>
</template>

<style scoped>
:root {
  /* 设计系统变量 */
  --primary-color: #00d2ff;
  --primary-hover: #3a7bd5;
  --bg-dark: #0f172a;
  --bg-light: #1e293b;
  --text-main: #f8fafc;
  --text-muted: #94a3b8;
  --error-color: #ef4444;
  --glass-bg: rgba(30, 41, 59, 0.7);
  --glass-border: rgba(255, 255, 255, 0.1);
  --input-bg: rgba(15, 23, 42, 0.6);
  --transition-speed: 0.3s;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.login-container {
  font-family: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    Helvetica, Arial, sans-serif;
  background-color: var(--bg-dark);
  color: var(--text-main);
  height: 100vh;
  width: 100vw;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

/* 动态背景效果 */
.login-background::before,
.login-background::after {
  content: "";
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.4;
  z-index: -1;
  animation: float 10s infinite alternate ease-in-out;
}

.login-background::before {
  background: radial-gradient(circle, var(--primary-color), transparent);
  top: -10%;
  left: -10%;
}

.login-background::after {
  background: radial-gradient(circle, #7928ca, transparent);
  bottom: -10%;
  right: -10%;
  animation-delay: -5s;
}

@keyframes float {
  0% {
    transform: translate(0, 0) scale(1);
  }
  100% {
    transform: translate(30px, 50px) scale(1.1);
  }
}

/* 主容器 */
.login-wrapper {
  width: 100%;
  max-width: 420px;
  padding: 20px;
  perspective: 1000px;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 磨砂卡片 */
.login-card {
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid var(--glass-border);
  padding: 3rem 2.5rem;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
  position: relative;
  overflow: hidden;
}

/* 顶部装饰条 */
.login-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), #7928ca);
}

/* 品牌标题 */
.brand-header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.brand-title {
  font-family: "Chakra Petch", sans-serif;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: 1px;
  background: linear-gradient(135deg, #fff 0%, #cbd5e1 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
}

.brand-subtitle {
  font-size: 0.875rem;
  color: var(--text-muted);
  letter-spacing: 0.5px;
}

/* 表单样式 */
.form-group {
  margin-bottom: 1.5rem;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-muted);
  transition: var(--transition-speed);
}

.form-input-wrapper {
  position: relative;
}

.form-input {
  width: 100%;
  padding: 0.875rem 1rem;
  background: var(--input-bg);
  border: 1px solid var(--glass-border);
  border-radius: 4px;
  color: var(--text-main);
  font-size: 1rem;
  font-family: "Inter", sans-serif;
  transition: all var(--transition-speed);
  outline: none;
}

.form-input:focus {
  border-color: var(--primary-color);
  background: rgba(15, 23, 42, 0.8);
  box-shadow: 0 0 0 4px rgba(0, 210, 255, 0.15);
}

.form-input::placeholder {
  color: rgba(148, 163, 184, 0.4);
}

/* 聚焦时标签高亮 */
.form-group:focus-within .form-label {
  color: var(--primary-color);
}

/* 错误提示 */
.error-alert {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #fca5a5;
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 8px;
  animation: shake 0.4s ease-in-out;
}

@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-4px);
  }
  75% {
    transform: translateX(4px);
  }
}

/* 登录按钮 */
.btn-submit {
  width: 100%;
  padding: 1rem;
  background: linear-gradient(135deg, var(--primary-color) 0%, #3a7bd5 100%);
  border: none;
  border-radius: 4px;
  color: #fff;
  font-family: "Chakra Petch", sans-serif;
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all var(--transition-speed);
  position: relative;
  overflow: hidden;
  text-transform: uppercase;
}

.btn-submit:hover {
  box-shadow: 0 10px 20px -5px rgba(0, 210, 255, 0.4);
}

.btn-submit:active {
  transform: translateY(0);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 注册链接 */
.register-link {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.9rem;
  color: var(--text-muted);
}

.register-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: color 0.2s;
  cursor: pointer;
}

.register-link a:hover {
  color: #60a5fa;
  text-decoration: underline;
}

/* 页脚 */
.footer-copy {
  position: absolute;
  bottom: 20px;
  text-align: center;
  width: 100%;
  font-size: 0.75rem;
  color: rgba(148, 163, 184, 0.4);
  font-family: "Chakra Petch", sans-serif;
}
</style>
