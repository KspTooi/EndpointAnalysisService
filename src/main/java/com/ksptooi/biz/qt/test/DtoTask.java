package com.ksptooi.biz.qt.test;

import com.ksptooi.biz.qt.common.QuickTask;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageQuery;
import com.ksptool.assembly.entity.web.Result;

import static com.ksptool.entities.Entities.toJson;

import org.springframework.stereotype.Component;

@Component
public class DtoTask implements QuickTask<PageQuery> {

    @Override
    public Result<String> execute(PageQuery params) throws Exception {

        //页码不能大于500
        if (params.getPageNum() > 500) {
            throw new BizException("页码不能大于500");
        }

        return Result.success("DtoTask 一切正常,参数: " + toJson(params));
    }

}
