package com.ksptooi.biz.qt.model.qttask.vo;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.ksptooi.commons.dataprocess.converter.IntegerRow;
import com.ksptooi.commons.dataprocess.converter.IntegerRowConverter;
import com.ksptooi.commons.dataprocess.converter.StringRow;
import com.ksptooi.commons.dataprocess.converter.StringRowConverter;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExportQtTaskVo {

    @ExcelProperty(value = "任务分组名", converter = StringRowConverter.class)
    @StringRow(required = false, max = 80, message = "任务分组名长度不能超过80")
    private String groupName;

    @ExcelProperty(value = "*任务名", converter = StringRowConverter.class)
    @StringRow(required = true, max = 80, message = "任务名长度不能超过80")
    private String name;

    @ExcelProperty(value = "*任务类型", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=本地BEAN;1=远程HTTP", message = "任务类型只能为 本地BEAN 或 远程HTTP")
    private Integer kind;

    @ExcelProperty(value = "*CRON表达式", converter = StringRowConverter.class)
    @StringRow(required = true, max = 64, message = "CRON表达式长度不能超过64")
    private String cron;

    @ExcelProperty(value = "*调用目标", converter = StringRowConverter.class)
    @StringRow(required = true, max = 1000, message = "调用目标长度不能超过1000")
    private String target;

    @ExcelProperty(value = "调用参数JSON", converter = StringRowConverter.class)
    @StringRow(required = false, message = "调用参数JSON长度不能超过1000")
    private String targetParam;

    @ExcelProperty(value = "*并发执行", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=允许;1=禁止", message = "并发执行只能为 允许 或 禁止")
    private Integer concurrent;

    @ExcelProperty(value = "*过期策略", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=放弃执行;1=立即执行;2=全部执行", message = "过期策略只能为 放弃执行 或 立即执行 或 全部执行")
    private Integer policyMisfire;

    @ExcelProperty(value = "*失败策略", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=默认;1=自动暂停", message = "失败策略只能为 默认 或 自动暂停")
    private Integer policyError;

    @ExcelProperty(value = "*日志策略", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=全部;1=仅异常;2=不记录", message = "日志策略只能为 全部 或 仅异常 或 不记录")
    private Integer policyRcd;

    @ExcelProperty(value = "任务有效期截止")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @ExcelProperty(value = "*状态", converter = IntegerRowConverter.class)
    @IntegerRow(required = true, format = "0=正常;1=暂停", message = "状态只能为 正常 或 暂停)")
    private Integer status;

}
