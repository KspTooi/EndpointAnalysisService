package com.ksptooi.biz.core.service;


import com.ksptooi.commons.enums.GlobalConfigEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 安装向导
 */
@Service
public class PanelInstallWizardService {

    @Autowired
    private GlobalConfigService globalConfigService;

    //判断当前是否为安装向导模式
    public boolean hasInstallWizardMode() {
        return globalConfigService.getBoolean(GlobalConfigEnum.ALLOW_INSTALL_WIZARD.getKey(), false);
    }

}
