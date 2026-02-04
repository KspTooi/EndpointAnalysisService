package com.ksptooi.biz.document.model.epsite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AddEpSiteDto {

    @Schema(description = "站点名称")
    @NotBlank(message = "站点名称不能为空")
    @Length(max = 32, message = "站点名称长度不能超过32个字符")
    private String name;

    @Schema(description = "地址")
    @Length(max = 255, message = "地址长度不能超过255个字符")
    private String address;

    @Schema(description = "账户")
    @Length(max = 500, message = "账户长度不能超过500个字符")
    private String username;

    @Schema(description = "密码")
    @Length(max = 500, message = "密码长度不能超过500个字符")
    private String password;

    @Schema(description = "备注")
    @Length(max = 1000, message = "备注长度不能超过1000个字符")
    private String remark;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

}
