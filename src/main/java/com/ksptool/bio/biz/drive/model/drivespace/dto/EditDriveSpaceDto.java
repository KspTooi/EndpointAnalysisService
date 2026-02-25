package com.ksptool.bio.biz.drive.model.drivespace.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import com.ksptool.bio.biz.drive.model.drivespacemember.dto.AddDriveSpaceMemberDto;

@Getter
@Setter
public class EditDriveSpaceDto {

    @NotNull(message = "空间ID不能为空")
    @Schema(description = "空间ID")
    private Long id;

    @NotBlank(message = "空间名称不能为空")
    @Size(max = 80, min = 1, message = "空间名称长度必须在1-80个字符之间")
    @Schema(description = "空间名称")
    private String name;

    @Size(max = 65535, message = "空间描述长度不能超过65535个字符")
    @Schema(description = "空间描述")
    private String remark;

    @NotNull(message = "配额限制不能为空")
    @Range(min = 0, message = "配额限制不能小于0")
    @Schema(description = "配额限制(bytes)")
    private Long quotaLimit;

    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态只能在0-1之间")
    @Schema(description = "状态 0:正常 1:归档")
    private Integer status;

    @NotNull(message = "成员列表不能为空")
    @Schema(description = "成员列表")
    private List<AddDriveSpaceMemberDto> members;

}
