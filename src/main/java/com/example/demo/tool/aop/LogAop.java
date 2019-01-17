package com.example.demo.tool.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

/**
 * AOP,前切，后切，监控类的调用
 * 打日志用
 */
@Aspect
@Configuration
public class LogAop {
    /**
     * com.example.demo.service包及子包下所有类中的所有方法
     */
    @Pointcut("execution(* com.example.demo.service.impl..*.*(..))")
    public void excudeService() {
    }

    @Pointcut("execution(* com.example.demo.service.common.aop..*.*(..))")
    public void excude() {
    }

    @Before("excudeService()||excude()")
    public void before(JoinPoint joinPoint) {
        //通过JoinPoint 获取通知的签名信息，如目标方法名，目标方法参数信息等
        System.err.println("切面before执行了。。。");
    }

    @AfterReturning(value = "excudeService()||excude()", returning = "obj")
    public void after(JoinPoint joinPoint, Object obj) {
        System.out.println("after return user" + obj);
    }
}
