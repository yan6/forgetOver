package com.example.demo.service.common.aop;

import com.example.demo.tool.annotation.MethodRunTime;
import org.springframework.stereotype.Service;

@Service
public class MethodDataLog {

    @MethodRunTime(currentTime = 1)
    public String test() {
        return "1111111";
    }
}
