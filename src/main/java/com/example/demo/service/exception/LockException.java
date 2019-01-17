package com.example.demo.service.exception;

public class LockException extends Exception {

    public LockException(String lockKey) {
        super(lockKey);
    }
}
