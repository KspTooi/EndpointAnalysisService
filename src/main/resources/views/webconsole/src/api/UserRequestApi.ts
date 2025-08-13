import type Result from "@/commons/entity/Result";
import Http from "@/commons/Http";

export interface SaveAsUserRequestDto {
    requestId: string;
    name: string | null;
}



export interface EditUserRequestDto {
    id: string;
    name: string | null;
    method: string | null;
    url: string | null;
}


export default {

    /** 保存原始请求为用户请求 */
    saveAsUserRequest: async (dto: SaveAsUserRequestDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/userRequest/saveAsUserRequest', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    },

    /** 编辑用户请求 */
    editUserRequest: async (dto: EditUserRequestDto): Promise<string> => {
        var result = await Http.postEntity<Result<string>>('/userRequest/editUserRequest', dto);
        if (result.code == 0) {
            return result.message;
        }
        throw new Error(result.message);
    },


}

