package com.ksptool.bio.biz.assembly.common.assemblybp.entity.blueprint;


import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Setter
@Getter
public class PolyBlueprint {

    //原始蓝图
    private RawBlueprint rawBlueprint;

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

    /**
     * 获取最终输出文件路径
     *
     * @return 最终输出文件路径
     */
    public Path getOutputFilePath() {
        if (outputFilePath == null) {
            return null;
        }
        return Paths.get(outputFilePath);
    }

}
