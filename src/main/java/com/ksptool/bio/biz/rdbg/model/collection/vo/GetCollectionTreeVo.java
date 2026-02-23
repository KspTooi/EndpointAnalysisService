package com.ksptool.bio.biz.rdbg.model.collection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetCollectionTreeVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "父级ID NULL顶级")
    private Long parentId;

    @Schema(description = "集合名称")
    private String name;

    @Schema(description = "集合类型 0:请求 1:组")
    private Integer kind;

    @Schema(description = "请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Schema(description = "子节点")
    private List<GetCollectionTreeVo> children;
}
