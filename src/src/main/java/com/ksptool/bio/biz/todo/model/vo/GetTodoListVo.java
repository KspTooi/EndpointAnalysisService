package com.ksptool.bio.biz.todo.model.vo;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetTodoListVo {

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="当前节点名称 (如: 财务总监审批)")
    private String nodeName;

    @Schema(description="摘要(如：张三提交的 5000 元报销)")
    private String summary;

    @Schema(description="办理成员类型 0:办理人, 1:候选组")
    private Integer memberType;

    @Schema(description="办理成员ID (用户ID或用户组标识)")
    private Long memberId;

    @Schema(description="发起人ID")
    private Long initiatorId;

    @Schema(description="发起人名")
    private String initiatorName;

    @Schema(description="发起时间")
    private LocalDateTime initiatorTime;

    @Schema(description="任务到达时间")
    private LocalDateTime createTime;

}
