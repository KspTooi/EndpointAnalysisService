package com.ksptooi.biz.userrequest.model.userrequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetUserRequestTreeVo {

    @Schema(description = "对象ID")
    private Long id;

    @Schema(description = "父级ID null为根节点")
    private Long parentId;

    @Schema(description = "对象类型 0:请求组 1:用户请求")
    private Integer type;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "基本过滤器数量")
    private Integer simpleFilterCount;

    @Schema(description = "已绑定原始请求 0:未绑定 1:已绑定")
    private Integer linkForOriginalRequest;

    @Schema(description = "子节点")
    private List<GetUserRequestTreeVo> children;
}

