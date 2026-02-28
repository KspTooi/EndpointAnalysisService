package com.ksptool.bio.biz.core.model.maintain.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteInstallWizardVo {

    @Schema(description = "旧版本")
    private String oldVersion;

    @Schema(description = "新版本")
    private String newVersion;

    @Schema(description = "变更项内容")
    private List<String> changesContent;
    
}
