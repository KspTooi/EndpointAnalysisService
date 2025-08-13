import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface AddUserRequestGroupDto {
    parentId: number | null; //父级ID 为空表示根节点
    name: string; //请求组名称
}

export interface EditUserRequestGroupDto {
    id: number; //请求组ID
    parentId: number | null; //父级ID 为空表示根节点
    name: string; //请求组名称
    description: string | null; //请求组描述
    seq: number; //排序
}

export default {
    
    addUserRequestGroup: async (dto: AddUserRequestGroupDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/userRequestGroup/addUserRequestGroup', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    },

    editUserRequestGroup: async (dto: EditUserRequestGroupDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/userRequestGroup/editUserRequestGroup', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    },
    
}
