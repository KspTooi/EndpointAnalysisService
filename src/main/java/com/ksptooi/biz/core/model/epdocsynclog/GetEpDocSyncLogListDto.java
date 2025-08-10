package com.ksptooi.biz.core.model.epdocsynclog;

import jakarta.validation.constraints.NotNull;

import com.ksptooi.commons.utils.page.PageQuery;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetEpDocSyncLogListDto extends PageQuery {

    @NotNull(message = "端点文档ID不能为空")
    private Long epDocId;

}
