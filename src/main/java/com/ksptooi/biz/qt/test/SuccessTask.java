package com.ksptooi.biz.qt.test;

import com.ksptooi.biz.qt.common.QuickTask;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.stereotype.Component;

@Component
public class SuccessTask implements QuickTask<String> {

    @Override
    public Result<String> execute(String params) throws Exception {
        return Result.success("SuccessTask 一切正常");
    }
}
