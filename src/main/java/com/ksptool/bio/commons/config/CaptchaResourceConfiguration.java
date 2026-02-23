package com.ksptool.bio.commons.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.ResourceStore;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaptchaResourceConfiguration {

    /**
     * 配置验证码资源存储器
     *
     * @return ResourceStore
     */
    @Bean
    public ResourceStore resourceStore() {
        // 使用简单的本地内存存储器，实际项目中可以使用数据库等存储
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();

        // 配置背景图
        // arg1: 验证码类型(SLIDER、ROTATE、CONCAT、WORD_IMAGE_CLICK)
        // arg2: Resource对象，包含: 资源类型(calsspath、file、url)、文件路径、tag标签

        //循环添加背景图 从1~100
        for (int i = 1; i <= 100; i++) {

            //格式化三位数
            String bgName = String.format("%03d", i);

            resourceStore.addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "captcha/bg_" + bgName + ".jpg", "default"));
        }

        return resourceStore;
    }

}

