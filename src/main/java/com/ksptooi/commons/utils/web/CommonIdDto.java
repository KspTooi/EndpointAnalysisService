package com.ksptooi.commons.utils.web;

import lombok.Data;

import java.util.List;

@Data
public class CommonIdDto {

    private Long id;
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
