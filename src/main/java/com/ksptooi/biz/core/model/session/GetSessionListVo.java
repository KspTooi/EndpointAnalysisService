package com.ksptooi.biz.core.model.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetSessionListVo {

    public GetSessionListVo(Long id, String username, LocalDateTime createTime, LocalDateTime expiresAt) {
        this.id = id;
        this.username = username;
        this.createTime = createTime;
        this.expiresAt = expiresAt;
    }

    @Schema(description = "会话ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "权限节点数量")
    private Integer permissionCount;

    @Schema(description = "登入时间")
    private LocalDateTime createTime;

    @Schema(description = "过期时间")
    private LocalDateTime expiresAt;

}
