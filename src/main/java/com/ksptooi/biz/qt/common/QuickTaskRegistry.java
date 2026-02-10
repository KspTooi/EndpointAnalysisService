package com.ksptooi.biz.qt.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QuickTaskRegistry implements ApplicationListener<ApplicationReadyEvent> {

    // 核心容器：BeanName -> Bean实例
    private static final Map<String, QuickTask> QT_BEAN_MAP = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        Map<String, QuickTask> beans = event.getApplicationContext().getBeansOfType(QuickTask.class);

        beans.forEach((name, bean) -> {
            QT_BEAN_MAP.put(name, bean);
            log.info("QuickTask 加载本地任务: {}", name);
        });
    }

    /**
     * 提供给 Quartz Job 调用
     */
    public static QuickTask<?> getBean(String name) {
        return QT_BEAN_MAP.get(name);
    }

    public static boolean contains(String name) {
        return QT_BEAN_MAP.containsKey(name);
    }
}
