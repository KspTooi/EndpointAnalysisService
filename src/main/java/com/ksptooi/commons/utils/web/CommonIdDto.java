package com.ksptooi.commons.utils.web;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CommonIdDto {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "主键ID列表")
    private List<Long> ids;

    /**
     * 查询当前是否为批量删除模式
     */
    public boolean isBatch(){
        return ids != null && !ids.isEmpty();
    }

    public boolean isValid(){
        return id != null || (ids != null && !ids.isEmpty());
    }

}
