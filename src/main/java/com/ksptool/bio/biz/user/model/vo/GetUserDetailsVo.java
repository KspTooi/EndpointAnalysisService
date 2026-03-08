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
    private Long id;

    @Schema(description="用户名")
    private String username;

    @Schema(description="密码")
    private String password;

    @Schema(description="昵称")
    private String nickname;

    @Schema(description="性别 0:男 1:女 2:不愿透露")
    private Integer gender;

    @Schema(description="手机号码")
    private String phone;

    @Schema(description="邮箱")
    private String email;

    @Schema(description="登录次数")
    private Integer loginCount;

    @Schema(description="用户状态 0:正常 1:封禁")
    private Integer status;

    @Schema(description="最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description="所属企业ID")
    private Long rootId;

    @Schema(description="所属企业名称")
    private String rootName;

    @Schema(description="部门ID")
    private Long deptId;

    @Schema(description="部门名称")
    private String deptName;

    @Schema(description="已激活的公司ID(兼容字段)")
    private Long activeCompanyId;

    @Schema(description="已激活的环境ID(兼容字段)")
    private Long activeEnvId;

    @Schema(description="用户头像附件ID")
    private Long avatarAttachId;

    @Schema(description="内置用户 0:否 1:是")
    private Integer isSystem;

    @Schema(description="数据版本号")
    private Long dataVersion;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="修改时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人ID")
    private Long updaterId;

    @Schema(description="删除时间 为NULL未删")
    private LocalDateTime deleteTime;

}

