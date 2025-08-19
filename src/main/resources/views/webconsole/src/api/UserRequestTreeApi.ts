import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetUserRequestTreeDto {
  keyword?: string | null;
}

export interface GetUserRequestTreeVo {
  id: string; //数据ID
  parentId: string | null; //父级ID
  type: number; //类型 0:请求组 1:用户请求
  name: string; //名称
  method?: string; //请求方法
  simpleFilterCount?: number; //基本过滤器数量 当type为0时会出现
  linkForOriginalRequest: number | null; //已绑定原始请求 0:未绑定 1:已绑定
  children?: GetUserRequestTreeVo[]; //子节点
}

export interface EditUserRequestTreeDto {
  id: string; //数据ID
  parentId: string | null; //父级ID
  type: number; //类型 0:请求组 1:用户请求
  name: string; //名称
  seq: number; //排序
}

export interface RemoveUserRequestTreeDto {
  id: string | null; //数据ID
  type: number | null; //类型 0:请求组 1:用户请求
}

export default {
  /** 获取用户请求树 */
  getUserRequestTree: async (dto: GetUserRequestTreeDto): Promise<GetUserRequestTreeVo[]> => {
    var result = await Http.postEntity<Result<GetUserRequestTreeVo[]>>("/userRequest/getUserRequestTree", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /** 编辑用户请求树 */
  editUserRequestTree: async (dto: EditUserRequestTreeDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequest/editUserRequestTree", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /** 删除用户请求树 */
  removeUserRequestTree: async (dto: RemoveUserRequestTreeDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequest/removeUserRequestTree", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
