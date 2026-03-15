package com.ksptool.bio.biz.assembly.common.quickbuildengine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QbeBlueprint {

    //蓝图文件名
    private String fileName;

    //绝对路径
    private String absoluteFilePath;

    //相对路径(相对于基准路径)
    private String relativeFilePath;

    //基准路径
    private String basePath;

    //模板内容(从.vm文件读取)
    private String templateContent;

    //包含占位符的相对路径(相对于基准路径)
    private String relativePathWithPlaceholder;

    //包含占位符的文件名
    private String fileNameWithPlaceholder;

    //最终输出文件名(占位符已解析)
    private String outputFileName;

    //最终输出路径(包含文件名,占位符已解析)
    private String outputFilePath;

}
