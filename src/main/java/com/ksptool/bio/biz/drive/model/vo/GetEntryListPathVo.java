package com.ksptool.bio.biz.drive.model.vo;

import com.ksptool.bio.biz.drive.model.EntryPo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEntryListPathVo {

    @Schema(description = "条目ID")
    private Long id;

    @Schema(description = "条目名称")
    private String name;

    /**
     * 从EntryPo创建
     *
     * @param entryPo 条目
     * @return 目录路径
     */
    public static GetEntryListPathVo of(EntryPo entryPo) {
        var vo = new GetEntryListPathVo();
        vo.setId(entryPo.getId());
        vo.setName(entryPo.getName());
        return vo;
    }

    /**
     * 创建根目录
     *
     * @return 根目录
     */
    public static GetEntryListPathVo ofRoot() {
        var vo = new GetEntryListPathVo();
        vo.setId(null);
        vo.setName("根目录");
        return vo;
    }

}
