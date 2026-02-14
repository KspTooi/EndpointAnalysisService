package com.ksptooi.biz.qt;

import com.ksptooi.biz.qt.common.QuickTask;
import com.ksptool.assembly.entity.exception.BizException;
import com.ksptool.assembly.entity.web.PageQuery;
import com.ksptool.assembly.entity.web.Result;
import org.springframework.stereotype.Component;

@Component
public class TestJobExceptionBean implements QuickTask<PageQuery> {


    @Override
    public Result<String> execute(PageQuery pq) throws Exception {

        System.out.println("🔥🔥🔥 任务执行了！参数：" + pq);

        throw new BizException("任务异常了！");
    }
}
