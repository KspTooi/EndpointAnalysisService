package com.ksptool.bio.biz.qf.model.qfmodel.dto;


import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GetQfModelListDto extends PageQuery {

    @Schema(description="模型分组")
    private String groupName;

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="模型状态 0:草稿 1:已部署 2:历史")
    private List<Integer> status;

}
