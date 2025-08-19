package com.ksptooi.biz.core.model.filter.dto;

import com.ksptooi.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditSimpleFilterOperationDto {

    private Long id;

    @Schema(description = "操作名称")
    private String name;

    @NotNull(message = "类型不能为空")
    @Range(min = 0, max = 60, message = "类型只能为0、1、2、3、4、50、60")
    @Schema(description = "类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL 50:标记请求状态 60:获取请求ID")
    private Integer kind;

    @NotNull(message = "目标不能为空")
    @Range(min = 0, max = 53, message = "目标只能为0、1、2、50、51、52、53")
    @Schema(description = "目标 0:标头 1:JSON载荷 2:URL(仅限kind=4) [50:正常 51:HTTP失败 52:业务失败 53:连接超时(仅限kind=50)]")
    private Integer target;

    @Schema(description = "原始键")
    private String f;

    @Schema(description = "目标键")
    private String t;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, message = "排序不能小于0")
    @Schema(description = "排序")
    private Integer seq;

    /**
     * 验证参数逻辑
     *
     * @return 错误信息 当没有错误时返回null
     */
    public String validate() {

        //持久化 缓存 注入缓存 注入持久化 时需提供原始键与目标键
        if (kind == 0 || kind == 1 || kind == 2 || kind == 3) {
            if (Str.isBlank(f)) {
                return "原始键不能为空";
            }
            if (Str.isBlank(t)) {
                return "目标键不能为空";
            }
        }

        //覆写URL时只能提供目标键
        if (kind == 4) {
            if (Str.isBlank(t)) {
                return "目标键不能为空";
            }
            if (Str.isNotBlank(f)) {
                return "当类型为4时，不能提供原始键";
            }
        }

        //目标为URL时 kind 只能为4
        if (target == 2) {
            if (kind != 4) {
                return "当目标为URL时，类型只能为覆写URL";
            }
        }

        //标记请求状态时kind = 50 时target 只能为50、51、52、53
        if (kind == 50) {
            
            if (target != 50 && target != 51 && target != 52 && target != 53) {
                return "当类型为50时，目标只能为50、51、52、53";
            }
            //不允许提供原始键
            if (Str.isNotBlank(f)) {
                return "当类型为50时，不能提供原始键";
            }
            //不允许提供目标键
            if (Str.isNotBlank(t)) {
                return "当类型为50时，不能提供目标键";
            }

        }

        //获取请求ID时kind = 60 时target 只能为0 1
        if (kind == 60) {
            if (target != 0 && target != 1) {
                return "当类型为60时，目标只能为0、1";
            }
            //必须提供原始键
            if (Str.isBlank(f)) {
                return "当类型为60时，必须提供原始键";
            }
            //不允许提供目标键
            if (Str.isNotBlank(t)) {
                return "当类型为60时，不能提供目标键";
            }
        }

        return null;
    }


}

