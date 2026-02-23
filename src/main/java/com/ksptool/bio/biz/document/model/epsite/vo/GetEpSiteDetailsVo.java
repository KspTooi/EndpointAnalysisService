package com.ksptool.bio.biz.document.model.epsite.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEpSiteDetailsVo {

    @Schema(description = "站点ID")
    private Long id;

    @Schema(description = "站点名称")
    private String name;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "账户")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    private Integer seq;

}
