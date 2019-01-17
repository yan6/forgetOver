package com.example.demo.service.cache;

@FunctionalInterface
public interface MutexProcessWithReturn<T> {
    T process();
}
