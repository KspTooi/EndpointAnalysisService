import type CommonIdDto from "@/commons/entity/CommonIdDto";
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

export interface GetUserRequestDetailsVo {
    id: string;
    name: string | null;
    method: string | null;
    url: string | null;
    requestHeaders: Map<string, string> | null;
    requestBodyType: string | null;
    requestBody: string | null;
    seq: number | null;
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

    /** 获取用户请求详情 */
    getUserRequestDetails: async (dto: CommonIdDto): Promise<GetUserRequestDetailsVo> => {
        var result = await Http.postEntity<Result<GetUserRequestDetailsVo>>('/userRequest/getUserRequestDetails', dto);
        if (result.code == 0) {
            return result.data;
        }
        throw new Error(result.message);
    },


}

