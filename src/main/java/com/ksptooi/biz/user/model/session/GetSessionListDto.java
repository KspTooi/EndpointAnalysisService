package com.ksptooi.biz.user.model.session;

import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSessionListDto extends PageQuery {

    //用户名
    private String userName;

}
