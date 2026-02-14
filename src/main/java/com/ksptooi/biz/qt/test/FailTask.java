package com.ksptooi.biz.qt.test;

import com.ksptooi.biz.qt.common.QuickTask;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.stereotype.Component;

@Component
public class FailTask implements QuickTask<String> {

    @Override
    public Result<String> execute(String params) throws BizException {
        throw new BizException("FailTask 业务异常,这是一条测试异常信息");
    }
}
