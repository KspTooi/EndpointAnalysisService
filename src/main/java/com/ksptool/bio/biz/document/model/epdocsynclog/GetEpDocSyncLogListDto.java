package com.ksptool.bio.biz.document.model.epdocsynclog;

import com.ksptool.assembly.entity.web.PageQuery;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEpDocSyncLogListDto extends PageQuery {

    @NotNull(message = "端点文档ID不能为空")
    private Long epDocId;

}
