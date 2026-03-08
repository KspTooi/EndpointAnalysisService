package com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@Setter
public class RawBlueprint {

    //蓝图文件名
    private String fileName;

    //绝对路径
    private String absoluteFilePath;

    //相对路径(相对于基准路径)
    private String relativeFilePath;

    //基准路径
    private String basePath;


    /**
     * 获取蓝图文件路径
     *
     * @return 蓝图文件路径
     */
    public Path getFilePath() {
        return getBasePath().resolve(relativeFilePath);
    }

    /**
     * 获取基准路径
     *
     * @return 基准路径
     */
    public Path getBasePath() {
        return Paths.get(basePath);
    }

}
