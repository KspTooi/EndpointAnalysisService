import { defineStore } from "pinia";
import type { UserLoginDto, UserLoginVo } from "../api/AuthApi";
import AuthApi from "../api/AuthApi";

const AuthStore = defineStore("AuthStore", {
  state: () => ({
    userInfo: null as UserLoginVo | null,
    sessionId: null as string | null,
  }),
  getters: {
    getUserInfo: (state) => {
      return state.userInfo;
    },
    getSessionId: (state) => {
      return state.sessionId;
    },
  },
  actions: {
    setUserInfo(userInfo: UserLoginVo) {
      this.userInfo = userInfo;
    },
    setSessionId(sessionId: string) {
      this.sessionId = sessionId;
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
