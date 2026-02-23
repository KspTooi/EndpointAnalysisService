package com.ksptool.bio.biz.relay.model.relayserver;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRelayServerListDto extends PageQuery {

    @Schema(description = "中继服务器名称")
    private String name;

    @Schema(description = "桥接目标URL")
    private String forwardUrl;

}
