package com.ksptooi.biz.relay.model.routerule.dto;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class AddRouteRuleDto {

    @Length(max = 32, message = "路由策略名长度不能超过32个字符")
    @NotBlank(message = "路由策略名不能为空")
    @Schema(description = "路由策略名")
    private String name;

    @Range(min = 0, max = 1, message = "匹配类型只能在0或1之间")
    @Schema(description = "匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Schema(description = "匹配键")
    private String matchKey;

    @Range(min = 0, max = 1, message = "匹配操作只能在0或1之间")
    @Schema(description = "匹配操作 0:等于")
    private Integer matchOperator;

    @Schema(description = "匹配值")
    private String matchValue;

    @NotNull(message = "目标服务器ID不能为空")
    @Schema(description = "目标服务器ID")
    private Long routeServerId;

    @Length(max = 5000, message = "策略描述长度不能超过5000个字符")
    @Schema(description = "策略描述")
    private String remark;

    /**
     * 验证参数
     *
     * @return 错误信息 当参数合法时返回null
     */
    public String validate() {

        //匹配类型为0:全部时 不允许填写所有匹配键、匹配操作、匹配值
        if (matchType == 0) {
            if (StringUtils.isNotBlank(matchKey)) {
                return "匹配类型为全部时，不能填写匹配键";
            }
            if (matchOperator != null) {
                return "匹配类型为全部时，不能填写匹配操作";
            }
            if (StringUtils.isNotBlank(matchValue)) {
                return "匹配类型为全部时，不能填写匹配值";
            }
        }

        //匹配类型为1:IP地址时 必须填写匹配操作、匹配值
        if (matchType == 1) {
            if (matchOperator == null) {
                return "匹配类型为IP地址时，必须填写匹配操作";
            }
            if (StringUtils.isBlank(matchValue)) {
                return "匹配类型为IP地址时，必须填写匹配值";
            }
            if (matchOperator != 0) {
                return "匹配类型为IP地址时，匹配操作只能为等于";
            }
        }

        return null;
    }

}

