package com.ksptool.bio.biz.core.model.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.ksptool.bio.commons.dataprocess.AbstractImportDto;
import com.ksptool.bio.commons.dataprocess.Str;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class ImportUserDto extends AbstractImportDto {

    @ExcelProperty(value = "*用户名/登录账号")
    private String username;

    @ExcelProperty(value = "*密码")
    private String password;

    @ExcelProperty(value = "昵称/姓名")
    private String nickname;

    @ExcelProperty(value = "性别")// 0:男 1:女 2:不愿透露
    private String gender;

    @ExcelProperty(value = "手机号")
    private String phone;

    @ExcelProperty(value = "邮箱")
    private String email;

    @ExcelProperty(value = "所属企业")
    private String rootName;

    @ExcelProperty(value = "所属部门")
    private String deptName;

    @Override
    public String validate() {

        //校验必填项
        if (StringUtils.isBlank(username)) {
            return "用户名不能为空";
        }
        if (StringUtils.isBlank(password)) {
            return "密码不能为空";
        }

        //校验长度
        if (StringUtils.isNotBlank(username) && username.length() > 50) {
            return "用户名长度不能超过50个字符";
        }
        if (StringUtils.isNotBlank(password) && password.length() > 1280) {
            return "密码长度不能超过1280个字符";
        }
        if (StringUtils.isNotBlank(nickname) && nickname.length() > 50) {
            return "昵称长度不能超过50个字符";
        }
        if (StringUtils.isNotBlank(phone) && phone.length() > 64) {
            return "手机号长度不能超过64个字符";
        }
        if (StringUtils.isNotBlank(email) && email.length() > 64) {
            return "邮箱长度不能超过64个字符";
        }
        if (StringUtils.isNotBlank(rootName) && rootName.length() > 32) {
            return "所属企业名长度不能超过32个字符";
        }
        if (StringUtils.isNotBlank(deptName) && deptName.length() > 255) {
            return "所属部门名长度不能超过255个字符";
        }

        //校验性别
        if (StringUtils.isNotBlank(gender) && Str.isNotIn(gender, "男", "女", "不愿透露")) {
            return "性别只能为男、女、不愿透露";
        }

        //如果填写了部门则必须填写所属企业
        if (StringUtils.isNotBlank(deptName) && StringUtils.isBlank(rootName)) {
            return "填写部门时必须填写所属企业";
        }

        return null;
    }

}
