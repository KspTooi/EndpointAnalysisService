
import Http from "@/commons/Http.ts";
import type PageQuery from "@/commons/entity/PageQuery.ts";
import type PageResult from "@/commons/entity/PageResult.ts";
import type CommonIdDto from "@/commons/entity/CommonIdDto.ts";
import type Result from "@/commons/entity/Result.ts";



export interface GetPermissionDefinitionVo {
    id: string;    // 权限节点ID
    code: string;  // 权限节点标识
    name: string;  // 权限节点名称
}

export interface GetPermissionListDto extends PageQuery {
    code?: string | null;        // 权限代码
    name?: string | null;        // 权限名称
}

export interface GetPermissionListVo {
    id: string;           // 权限ID
    code: string;         // 权限代码
    name: string;         // 权限名称
    description: string;  // 权限描述
    isSystem: number;     // 是否为系统权限 1:是 0:否
}

export interface GetPermissionDetailsVo {
    id: string;           // 权限ID
    code: string;         // 权限代码
    name: string;         // 权限名称
    description: string;  // 权限描述
    sortOrder: number;    // 排序顺序
    isSystem: number;     // 是否为系统权限 1:是 0:否
    createTime: string;   // 创建时间
    updateTime: string;   // 修改时间
}

export interface SavePermissionDto {
    id?: string | null;           // 权限ID，创建时为空
    code: string;                 // 权限代码
    name: string;                 // 权限名称
    description?: string | null;  // 权限描述
    sortOrder?: number | null;    // 排序顺序
}

export default {
    /**
     * 获取所有权限定义
     */
    getPermissionDefinition: async (): Promise<GetPermissionDefinitionVo[]> => {
        var result = await Http.postEntity<Result<GetPermissionDefinitionVo[]>>('/permission/getPermissionDefinition', {});
        if (result.code == 0) {
            return result.data;
        }
        throw new Error(result.message);
    },

    /**
     * 获取权限列表（分页）
     */
    getPermissionList: async (dto: GetPermissionListDto): Promise<PageResult<GetPermissionListVo>> => {
        return await Http.postEntity<PageResult<GetPermissionListVo>>('/permission/getPermissionList', dto);
    },

    /**
     * 获取权限详情
     */
    getPermissionDetails: async (dto: CommonIdDto): Promise<GetPermissionDetailsVo> => {
        var result = await Http.postEntity<Result<GetPermissionDetailsVo>>('/permission/getPermissionDetails', dto);
        if (result.code == 0) {
            return result.data;
        }
        throw new Error(result.message);
    },

    /**
     * 保存权限（新增或更新）
     */
    savePermission: async (dto: SavePermissionDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/permission/savePermission', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    },

    /**
     * 删除权限
     */
    removePermission: async (dto: CommonIdDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/permission/removePermission', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    }
} 