package com.example.demo.service.cache;

import java.util.concurrent.TimeUnit;

public interface RedisService {

    boolean put(String key, Object value);

    boolean put(String key, Object value, long timeout, TimeUnit unit);

    <T> T get(String key, Class<T> type);

    <T> T get(String key, Class<T> type, CacheCallable<T> callable);

    <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue);

    <T> T get(String key, Class<T> type, CacheCallable<T> callable, T defaultValue, long timeout, TimeUnit unit);
}
