package com.ksptool.bio.biz.drive.model.driveentry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddEntryDto {

    @Schema(description = "云盘空间ID")
    @NotNull(message = "云盘空间ID不能为空")
    private Long driveSpaceId;

    @Schema(description = "父级ID 为NULL顶级")
    private Long parentId;

    @Schema(description = "条目名称")
    @NotBlank(message = "条目名称不能为空")
    @Length(min = 1, max = 128, message = "条目名称长度必须在1-128个字符之间")
    private String name;

    @Schema(description = "条目类型 0:文件 1:文件夹")
    @NotNull(message = "条目类型不能为空")
    @Range(min = 0, max = 1, message = "条目类型只能在0-1之间")
    private Integer kind;

    @Schema(description = "文件附件ID 文件夹为NULL")
    private Long attachId;


    public String validate() {

        //文件夹不能有文件附件
        if (kind == 1) {
            if (attachId != null) {
                return "文件夹不能有文件附件";
            }
        }

        //文件附件ID不能为空
        if (kind == 0) {
            if (attachId == null) {
                return "文件附件ID不能为空";
            }
        }

        return null;
    }

}

