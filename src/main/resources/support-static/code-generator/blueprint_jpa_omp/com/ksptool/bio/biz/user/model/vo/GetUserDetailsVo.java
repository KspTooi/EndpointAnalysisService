package com.ksptool.bio.biz.user.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetUserDetailsVo{


    @Schema(description="用户ID")
    private String id;

    @Schema(description="用户名")
    private String username;

    @Schema(description="密码")
    private String password;

    @Schema(description="昵称")
    private String nickname;

    @Schema(description="性别 0:男 1:女 2:不愿透露")
    private String gender;

    @Schema(description="手机号码")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="登录次数")
    private String loginCount;

    @Schema(description="用户状态 0:正常 1:封禁")
    private String status;

    @Schema(description="最后登录时间")
    private String lastLoginTime;

    @Schema(description="所属企业ID")
    private String rootId;

    @Schema(description="所属企业名称")
    private String rootName;

    @Schema(description="部门ID")
    private String deptId;

    @Schema(description="部门名称")
    private String deptName;

    @Schema(description="已激活的公司ID(兼容字段)")
    private String activeCompanyId;

    @Schema(description="已激活的环境ID(兼容字段)")
    private String activeEnvId;

    @Schema(description="用户头像附件ID")
    private String avatarAttachId;

    @Schema(description="内置用户 0:否 1:是")
    private String isSystem;

    @Schema(description="数据版本号")
    private String dataVersion;

    @Schema(description="创建时间")
    private String createTime;

    @Schema(description="创建人ID")
    private String creatorId;

    @Schema(description="修改时间")
    private String updateTime;

    @Schema(description="更新人ID")
    private String updaterId;

    @Schema(description="删除时间 为NULL未删")
    private String deleteTime;

}

