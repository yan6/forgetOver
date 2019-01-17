package com.example.demo.service.cache;

public interface CacheCallable<T> {

    T call(String key) throws Exception;
}
