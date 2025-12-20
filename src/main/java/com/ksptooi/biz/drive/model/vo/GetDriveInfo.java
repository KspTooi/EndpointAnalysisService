package com.ksptooi.biz.drive.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class GetDriveInfo {
    
    public GetDriveInfo() {
        this.totalCapacity = 0L;
        this.usedCapacity = 0L;
        this.objectCount = 0L;
    }

    public GetDriveInfo(Long totalCapacity, Long usedCapacity, Long objectCount) {
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
        this.objectCount = objectCount;
    }

    //总容量
    private Long totalCapacity;

    //已使用容量
    private Long usedCapacity;

    //对象数量
    private Long objectCount;
    
}
