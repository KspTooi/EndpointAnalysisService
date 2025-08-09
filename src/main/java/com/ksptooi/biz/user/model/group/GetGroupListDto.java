package com.ksptooi.biz.user.model.group;


import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetGroupListDto extends PageQuery {

    //模糊匹配 组标识、组名称、组描述
    private String keyword;

    //组状态：0:禁用 1:启用
    private Integer status;

}
