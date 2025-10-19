import Http from "@/commons/Http.ts";

export default {
  /**
   * 校验系统内置权限节点
   */
  validateSystemPermissions: async (): Promise<string> => {
    const result = await Http.postRaw<string>("/maintain/validSystemPermission", {});
    return result.message || "权限节点校验完成";
  },

  /**
   * 校验系统内置用户组
   */
  validateSystemGroups: async (): Promise<string> => {
    const result = await Http.postRaw<string>("/maintain/validSystemGroup", {});
    return result.message || "用户组校验完成";
  },

  /**
   * 校验系统内置用户
   */
  validateSystemUsers: async (): Promise<string> => {
    const result = await Http.postRaw<string>("/maintain/validSystemUsers", {});
    return result.message || "系统用户校验完成";
  },

  /**
   * 校验系统全局配置项
   */
  validateSystemConfigs: async (): Promise<string> => {
    const result = await Http.postRaw<string>("/maintain/validSystemConfigs", {});
    return result.message || "全局配置校验完成";
  },

  /**
   * 重置菜单
   */
  resetMenus: async (): Promise<string> => {
    const result = await Http.postRaw<string>("/maintain/resetMenus", {});
    return result.message || "菜单重置完成";
  },
};
