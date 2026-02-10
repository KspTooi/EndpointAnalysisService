package com.ksptooi.biz.qt.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
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


        for(Map.Entry<String, QuickTask> entry : beans.entrySet()){

            var name = entry.getKey();
            var bean = entry.getValue();
            var simpleName = bean.getClass().getSimpleName();

            //如果出现冲突则抛出异常
            if (QT_BEAN_MAP.containsKey(simpleName)) {
                throw new RuntimeException("QuickTask初始化时产生了一个冲突: " + simpleName + "请检查是否存在重复的本地任务Bean");
            }

            QT_BEAN_MAP.put(simpleName, bean);
            log.info("QuickTask 加载本地任务: {}", simpleName);
            
        }



    }

    /**
     * 提供给 Quartz Job 调用
     */
    public static QuickTask<?> getBean(String name) {
        return QT_BEAN_MAP.get(name);
    }

    /**
     * 判断是否包含本地任务Bean
     *
     * @param name Bean名称
     * @return 是否包含
     */
    public static boolean contains(String name) {
        return QT_BEAN_MAP.containsKey(name);
    }

    /**
     * 获取所有本地任务Bean
     *
     * @return 本地任务Bean列表
     */
    public static ArrayList<QuickTask> getBeans() {
        Collection<QuickTask> values = QT_BEAN_MAP.values();
        return new ArrayList<>(values);
    }
}
