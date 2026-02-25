package com.ksptool.bio.biz.drive.model.driveentry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CheckEntryMoveVo {

    @Schema(description = "是否可以移动 0:是 1:名称冲突 2:不可移动")
    private Integer canMove;

    @Schema(description = "提示信息")
    private String message;

    @Schema(description = "存在冲突的条目列表")
    private Set<String> conflictNames;

}
