package com.ksptooi.biz.qt.common;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class LocalExecutionJob extends QuartzJobBean {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        //获取任务快照数据 (这些数据是调度时塞进去的)
        JobDataMap dataMap = context.getMergedJobDataMap();
        String beanName = dataMap.getString("target");
        String jsonParams = dataMap.getString("params");
        Long taskId = dataMap.getLong("taskId");

        long startTime = System.currentTimeMillis();
        int status = 0; // 0成功 1失败
        String result = "";

        try {
            //查找 Bean (安全检查)
            QuickTask<?> handler = QuickTaskRegistry.getBean(beanName);
            if (handler == null) {
                throw new RuntimeException("本地任务Bean不存在: " + beanName);
            }

            //参数自动转换 (黑科技：获取接口泛型 T 的类型)
            Class<?> paramType = GenericTypeResolver.resolveTypeArgument(handler.getClass(), QuickTask.class);
            Object paramObj = null;
            if (jsonParams != null && paramType != null) {
                paramObj = objectMapper.readValue(jsonParams, paramType);
            }

            //执行业务
            //这里的强转是安全的，因为我们已经处理了泛型
            ((QuickTask<Object>) handler).execute(paramObj);

            result = "执行成功";

        } catch (Exception e) {
            status = 1; // 失败
            //截取异常堆栈前2000字符存入数据库
            result = ExceptionUtils.getStackTrace(e);
            e.printStackTrace(); // 建议用 log.error
        } finally {
            //记录日志 (写 qt_task_rcd 表)
            long cost = System.currentTimeMillis() - startTime;
            //logService.recordLog(taskId, status, result, cost);
        }
    }


}