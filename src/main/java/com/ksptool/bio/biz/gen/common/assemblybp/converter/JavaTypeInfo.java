package com.ksptool.bio.biz.gen.common.assemblybp.converter;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class JavaTypeInfo {

    //简单类型名
    private String simpleName;

    //完整类型名
    private String fullName;

    //要导入的包
    private String importPackage;

    public JavaTypeInfo(String simpleName, String fullName, String importPackage) {
        this.simpleName = simpleName;
        this.fullName = fullName;
        this.importPackage = importPackage;
    }

    /**
     * 静态工厂方法，根据Java类自动创建JavaTypeInfo
     * 会自动处理import和各种名称转换
     *
     * @param clazz Java类
     * @return JavaTypeInfo实例
     */
    public static JavaTypeInfo of(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class不能为null");
        }

        String simpleName = clazz.getSimpleName();
        String fullName = clazz.getName();
        String importPackage = null;

        Package pkg = clazz.getPackage();
        if (pkg != null) {
            String packageName = pkg.getName();
            if (StringUtils.isNotBlank(packageName) && !packageName.equals("java.lang")) {
                importPackage = packageName;
            }
        }

        return new JavaTypeInfo(simpleName, fullName, importPackage);
    }

}

