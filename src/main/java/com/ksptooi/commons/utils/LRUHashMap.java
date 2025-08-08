package com.ksptooi.commons.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUHashMap(int capacity) {
        // 初始化 LinkedHashMap 并指定访问顺序
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    // 重写 removeEldestEntry 方法，当缓存的大小超过容量时，移除最久未使用的元素
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
