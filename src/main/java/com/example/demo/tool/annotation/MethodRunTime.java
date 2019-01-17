package com.example.demo.tool.annotation;

import java.lang.annotation.*;

/**
 * @Documented 在默认的情况下javadoc命令不会将我们的annotation生成再doc中去的，所以使用该标记就是告诉jdk让它也将annotation生成到doc中去
 * @Inherited 比如有一个类A，在他上面有一个标记annotation，那么A的子类B是否不用再次标记annotation就可以继承得到呢，答案是肯定的
 * Annotation属性值 有以下三种： 基本类型、数组类型、枚举类型
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MethodRunTime {
    int currentTime() default 0;
}
