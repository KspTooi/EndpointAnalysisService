import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

export interface UserLoginDto {
    username: string; // 用户名
    password: string; // 密码
}

export interface UserLoginVo {
    id: string; // 用户ID
    username: string; // 用户名
    nickname: string; // 用户昵称
    gender: number; // 用户性别
    phone: string; // 用户手机号
    email: string; // 用户邮箱
    status: number; // 用户状态
    lastLoginTime: string; // 最后登录时间
    rootId: string; // 所属企业ID
    deptId: string; // 所属部门ID
    rootName: string; // 所属企业名称
    deptName: string; // 所属部门名称
    avatarAttachId: string; // 用户头像附件ID
    isSystem: number; // 是否为系统内置用户 0:否 1:是
    createTime: string; // 创建时间
    sessionId: string; // 用户会话ID
    authorities: string[]; // 权限码
}

export default {
    /**
     * 用户登录
     */
    userLogin: async (dto: UserLoginDto): Promise<Result<UserLoginVo>> => {
        return await Http.postEntity<Result<UserLoginVo>>("/auth/userLogin", dto);
    },
};
