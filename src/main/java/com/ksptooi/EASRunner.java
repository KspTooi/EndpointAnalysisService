package com.ksptooi;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.biz.core.service.GlobalConfigService;
import com.ksptooi.biz.relay.service.RelayServerService;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@EnableAsync
@Slf4j
public class EASRunner {

    // 从配置文件中读取应用版本号
    private static String applicationVersion;

    @Autowired
    private RelayServerService relayServerService;

    @Value("${application.version}")
    public void setApplicationVersion(String version) {
        applicationVersion = version;
    }


    public static void main(String[] args) {
        SpringApplication.run(EASRunner.class, args);
    }


    @PostConstruct
    public void init() {
        relayServerService.initRelayServer();
    }

    /**
     * 应用启动时检查全局配置
     */
    @Bean
    public ApplicationRunner configInitializer(GlobalConfigService globalConfigService, AuthService authService) {
        return args -> {


            // 检查是否存在allow.install.wizard配置
            String allowInstallWizard = globalConfigService.getValue(GlobalConfigEnum.ALLOW_INSTALL_WIZARD.getKey());

            // 检查是否存在更新
            String storeVersion = globalConfigService.get(GlobalConfigEnum.APPLICATION_VERSION.getKey(), "1.0A");

            //检查是否在版本落后时允许执行升级向导
            boolean allowWizardWhenUpgraded = globalConfigService.getBoolean(GlobalConfigEnum.ALLOW_INSTALL_WIZARD_UPGRADED.getKey(), true);

            if (!storeVersion.equals(applicationVersion)) {

                //允许在版本落后时触发向导进行数据升级
                if (allowWizardWhenUpgraded) {
                    log.info("应用程序版本已落后 当前:{} 最新:{},自动运行升级向导。", storeVersion, applicationVersion);
                    globalConfigService.setValue(GlobalConfigEnum.ALLOW_INSTALL_WIZARD.getKey(), null);
                    allowInstallWizard = "true";
                }

                //不允许版本落后时触发向导升级
                if (!allowWizardWhenUpgraded) {
                    log.info("应用程序版本已落后 当前:{} 最新:{},升级向导当前被禁用,请注意数据同步。", storeVersion, applicationVersion);
                    globalConfigService.setValue(GlobalConfigEnum.APPLICATION_VERSION.getKey(), getVersion());
                }

            }

            // 如果配置不存在，则添加默认值true
            if (StringUtils.isBlank(allowInstallWizard) || allowInstallWizard.equals("true")) {
                globalConfigService.setValue(GlobalConfigEnum.ALLOW_INSTALL_WIZARD.getKey(), "true");
                System.out.println("初始化配置: " + GlobalConfigEnum.ALLOW_INSTALL_WIZARD.getKey() + " = true");

                // 清除所有用户会话，确保安装向导启动时没有用户登录
                authService.clearUserSession();
                System.out.println("已清除所有用户会话，准备启动安装向导");
            }

            // 打印应用版本信息
            System.out.println("应用版本: " + getVersion());
        };
    }

    /**
     * 获取应用版本号
     *
     * @return 应用版本号
     */
    public static String getVersion() {
        return applicationVersion;
    }
}
