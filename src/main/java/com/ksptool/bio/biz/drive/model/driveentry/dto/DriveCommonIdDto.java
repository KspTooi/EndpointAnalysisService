package com.ksptool.bio.biz.drive.model.driveentry.dto;

import com.ksptool.assembly.entity.web.CommonIdDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriveCommonIdDto extends CommonIdDto {

    @Schema(description = "云盘空间ID")
    @NotNull(message = "云盘空间ID不能为空")
    private Long driveSpaceId;

}
