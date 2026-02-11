package com.ksptooi.biz.auth.model.session.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserSessionVo {

    @Schema(description = "用户凭据SessionID")
    private String sessionId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "所属企业ID")
    private Long rootId;

    @Schema(description = "所属企业名称")
    private String rootName;

    @Schema(description = "所属部门ID")
    private Long deptId;

    @Schema(description = "所属部门名称")
    private String deptName;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "用户权限代码集合")
    private Set<String> permissionCodes;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;


} 