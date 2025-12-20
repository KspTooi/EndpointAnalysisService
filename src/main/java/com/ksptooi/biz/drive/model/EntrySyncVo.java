package com.ksptooi.biz.drive.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntrySyncVo {

    //文件附件ID
    private Long attachId;

    //云盘条目ID
    private Long entryId;

    //检查次数
    private Integer checkCount;

}
