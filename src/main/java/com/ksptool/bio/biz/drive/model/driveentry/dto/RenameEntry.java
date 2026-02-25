package com.ksptool.bio.biz.drive.model.driveentry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RenameEntry {

    @Schema(description = "条目ID")
    @NotNull(message = "条目ID不能为空")
    private Long entryId;

    @Schema(description = "条目名称")
    @NotBlank(message = "条目名称不能为空")
    @Size(min = 1, max = 128, message = "条目名称长度必须在1-128个字符之间")
    private String name;

}

