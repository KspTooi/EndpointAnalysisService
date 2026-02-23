package com.ksptool.bio.biz.core.model.maintain.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintainUpdateVo {

    @Schema(description = "已存在的数量")
    private Integer existCount;

    @Schema(description = "新增的数量")
    private int addedCount;

    @Schema(description = "删除的数量")
    private int removedCount;

    @Schema(description = "新增列表")
    private List<String> addedList = new ArrayList<>();

    @Schema(description = "删除列表")
    private List<String> removedList = new ArrayList<>();

    @Schema(description = "结果消息")
    private String message;

}
