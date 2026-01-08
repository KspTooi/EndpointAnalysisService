package com.ksptooi.biz.rdbg.model.userrequestenv.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class AddUserRequestEnvDto {

    @Length(max = 32, message = "环境名长度不能超过32个字符")
    @NotBlank(message = "环境名不能为空")
    @Schema(description = "环境名")
    private String name;

    @Length(max = 5000, message = "描述长度不能超过5000个字符")
    @Schema(description = "描述")
    private String remark;

}

