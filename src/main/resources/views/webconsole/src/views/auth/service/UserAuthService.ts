import { defineStore } from "pinia";
import type { UserLoginDto, UserLoginVo } from "../api/AuthApi";
import AuthApi from "../api/AuthApi";

const AuthStore = defineStore("AuthStore", {
  state: () => {
    const userInfo = localStorage.getItem("userInfo");
    const sessionId = localStorage.getItem("sessionId");

    return {
      userInfo: userInfo ? (JSON.parse(userInfo) as UserLoginVo) : null,
      sessionId: sessionId,
    };
  },
  getters: {
    getUserInfo: (state) => {
      return state.userInfo;
    },
    getSessionId: (state) => {
      return state.sessionId;
    },
  },
  actions: {
    setUserInfo(userInfo: UserLoginVo | null) {
      this.userInfo = userInfo;

      if (!userInfo) {
        localStorage.removeItem("userInfo");
        return;
      }

      localStorage.setItem("userInfo", JSON.stringify(userInfo));
    },
    setSessionId(sessionId: string | null) {
      this.sessionId = sessionId;

      if (!sessionId) {
        localStorage.removeItem("sessionId");
        return;
      }

      localStorage.setItem("sessionId", sessionId);
    },
  },
});

export default {
  AuthStore,
  /**
   * 用户认证服务
   */
  useUserAuth() {
    const login = async (username: string, password: string) => {
      var dto = {
        username,
        password,
      } as UserLoginDto;

      const result = await AuthApi.userLogin(dto);

      if (result.code === 0 && result.data) {
        AuthStore().setUserInfo(result.data);
        AuthStore().setSessionId(result.data.sessionId);
        return result.data;
      }

      throw new Error(result.message);
    };

    return {
      login,
    };
  },
};
