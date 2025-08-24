import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

export interface GetUserRequestTreeDto {
  keyword?: string | null;
}

export interface GetUserRequestTreeVo {
  id: string; //数据ID
  requestId: string; //请求ID
  groupId: string; //组ID
  parentId: string | null; //父级ID
  type: number; //类型 0:请求组 1:用户请求
  name: string; //名称
  method?: string; //请求方法
  simpleFilterCount?: number; //基本过滤器数量 当type为0时会出现
  linkForOriginalRequest: number | null; //已绑定原始请求 0:未绑定 1:已绑定
  children?: GetUserRequestTreeVo[]; //子节点
}

export interface AddUserRequestTreeDto {
  parentId: string | null; //父级ID
  kind: number; //类型 0:请求组 1:用户请求
  name: string; //名称
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

export interface MoveUserRequestTreeDto {
  keyword?: string | null; //关键字查询
  nodeId: string; //对象ID
  targetId: string | null; //目标ID
  kind: number; //移动方式 0:顶部 1:底部 2:内部
}

export default {
  /** 获取用户请求树 */
  getUserRequestTree: async (dto: GetUserRequestTreeDto): Promise<GetUserRequestTreeVo[]> => {
    var result = await Http.postEntity<Result<GetUserRequestTreeVo[]>>("/userRequestTree/getUserRequestTree", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /** 新增用户请求树 */
  addUserRequestTree: async (dto: AddUserRequestTreeDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequestTree/addUserRequestTree", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /** 移动用户请求树 */
  moveUserRequestTree: async (dto: MoveUserRequestTreeDto): Promise<GetUserRequestTreeVo[]> => {
    var result = await Http.postEntity<Result<GetUserRequestTreeVo[]>>("/userRequestTree/moveUserRequestTree", dto);
    if (result.code == 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /** 编辑用户请求树 */
  editUserRequestTree: async (dto: EditUserRequestTreeDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequestTree/editUserRequestTree", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },

  /** 删除用户请求树 */
  removeUserRequestTree: async (dto: RemoveUserRequestTreeDto): Promise<string> => {
    var result = await Http.postEntity<Result<string>>("/userRequestTree/removeUserRequestTree", dto);
    if (result.code == 0) {
      return result.message;
    }
    throw new Error(result.message);
  },
};
