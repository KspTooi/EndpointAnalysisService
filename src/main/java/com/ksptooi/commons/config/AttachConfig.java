package com.ksptooi.commons.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class AttachConfig {

    //默认存储类型
    @Value("${file-attach.default}")
    private String defaultAttachType;

    /**
     * 本地文件配置
     */
    //本地文件存储路径LINUX
    @Value("${file-attach.local.linux}")
    private String localLinuxPath;

    //本地文件存储路径WINDOWS
    @Value("${file-attach.local.windows}")
    private String localWindowsPath;

    //是否自动创建文件夹
    @Value("${file-attach.local.auto-create}")
    private Boolean localAutoCreate;

    //MINIO存储端点
    //@Value("${file-attach.minio.endpoint}")
    //private String minioEndpoint;

    //MINIO存储桶名称
    //@Value("${file-attach.minio.bucket-name}")
    //private String minioBucketName;

    //MINIO存储访问密钥
    //@Value("${file-attach.minio.access-key}")
    //private String minioAccessKey;

    //MINIO存储密钥
    //@Value("${file-attach.minio.secret-key}")
    //private String minioSecretKey;

}
