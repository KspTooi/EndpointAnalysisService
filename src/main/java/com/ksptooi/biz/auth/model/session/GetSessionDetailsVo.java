package com.ksptooi.biz.auth.model.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class GetSessionDetailsVo {

    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登入时间")
    private LocalDateTime createTime;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;

    @Schema(description = "权限节点")
    private Set<String> permissions;

}
