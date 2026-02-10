package com.ksptooi.biz.qt.common;

import com.ksptooi.biz.qt.service.QtTaskService;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.formula.functions.Log;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class LocalBeanExecutionJob extends QuartzJobBean {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QtTaskService qtTaskService;

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

            //提供给执行器的参数
            Object paramObj = null;

            //入参不为空 需要处理参数映射
            if (StringUtils.isNotBlank(jsonParams)) {

                try {

                    //参数自动转换黑科技
                    ResolvableType resolvableType = ResolvableType.forClass(AopUtils.getTargetClass(handler)).as(QuickTask.class);
                    Class<?> paramType = resolvableType.getGeneric(0).resolve();

                    //处理特殊情况
                    if (paramType != null) {

                        //用户想直接要 JSON 字符串或未定义泛型
                        if (String.class.isAssignableFrom(paramType)) {
                            paramObj = jsonParams;
                        }

                        //复用Spring Boot 默认配置好的 ObjectMapper
                        if (!String.class.isAssignableFrom(paramType)) {
                            JavaType javaType = objectMapper.getTypeFactory().constructType(resolvableType.getType());
                            paramObj = objectMapper.readValue(jsonParams, javaType);
                        }

                    }

                } catch (Exception e) {
                    //处理参数转换时出现异常,终止任务
                    log.error(e.getMessage(), e);
                    qtTaskService.abortTask(taskId);
                }

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