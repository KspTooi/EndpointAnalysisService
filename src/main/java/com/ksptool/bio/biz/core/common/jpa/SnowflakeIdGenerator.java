package com.ksptool.bio.biz.core.common.jpa;

import com.ksptool.bio.commons.utils.IdWorker;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

import java.util.EnumSet;

public class SnowflakeIdGenerator implements BeforeExecutionGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        // 如果外部已经手动 set 了 ID，则保留手动设置的值，否则生成新的
        if (currentValue != null) {
            return currentValue;
        }
        return IdWorker.nextId();
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        // 仅在插入（INSERT）时触发
        return EventTypeSets.INSERT_ONLY;
    }
}