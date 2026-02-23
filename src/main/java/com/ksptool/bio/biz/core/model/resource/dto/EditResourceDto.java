package com.ksptool.bio.biz.core.model.resource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class EditResourceDto {


    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "资源名")
    @NotBlank(message = "资源名不能为空")
    private String name;

    @Schema(description = "资源描述")
    private String description;

    @Schema(description = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @Schema(description = "菜单类型 0:目录 1:菜单 1:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径")
    @NotBlank(message = "菜单路径不能为空")
    private String menuPath;

    @Schema(description = "菜单查询参数")
    private String menuQueryParam;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "接口路径")
    private String path;

    @Schema(description = "所需权限")
    private String permission;

    @Schema(description = "排序")
    private Integer seq;

    /**
     * 验证资源是否合法
     *
     * @return 错误信息 无错误返回null
     */
    public String validate() {

        //如果资源是菜单则不能填写接口相关字段
        if (kind == 0) {
            if (StringUtils.isNotBlank(path)) {
                return "菜单不能填写接口相关字段";
            }
            //资源是目录时不允许填写路径、查询参数
            if (menuKind == 0) {
                if (StringUtils.isNotBlank(menuPath)) {
                    return "目录不能填写路径";
                }
                if (StringUtils.isNotBlank(menuQueryParam)) {
                    return "目录不能填写查询参数";
                }
                if (StringUtils.isNotBlank(permission)) {
                    return "目录不能填写权限";
                }
            }
        }

        //如果资源是接口则不能填写菜单相关字段
        if (kind == 1) {
            if (StringUtils.isNotBlank(menuPath)) {
                return "接口不能填写菜单相关字段";
            }
            if (StringUtils.isNotBlank(menuQueryParam)) {
                return "接口不能填写菜单相关字段";
            }
            if (StringUtils.isNotBlank(menuIcon)) {
                return "接口不能填写菜单相关字段";
            }
            if (StringUtils.isNotBlank(permission)) {
                return "接口不能填写菜单相关字段";
            }
        }

        return null;
    }

}

