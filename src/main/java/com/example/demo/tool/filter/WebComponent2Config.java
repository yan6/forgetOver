package com.example.demo.tool.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 拦截器相关
 */
@Configuration
public class WebComponent2Config {
    @Bean
    public FilterRegistrationBean someFilterLoginOrRegister() {
        //新建过滤器注册类
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 添加我们写好的过滤器
        registration.setFilter(new SessionFilter());
        // 设置过滤器的URL模式
        registration.addUrlPatterns("/xx");
        //设置过滤器顺序,越小执行优先级越高
        registration.setOrder(1);
        return registration;
    }


    @Bean
    public FilterRegistrationBean someFilterMonitor() {
        //新建过滤器注册类
        FilterRegistrationBean registration = new FilterRegistrationBean();
        // 添加我们写好的过滤器
        registration.setFilter(new MonitorFilter());
        // 设置过滤器的URL模式
        registration.addUrlPatterns("/xx");
        //设置过滤器顺序,越小执行优先级越高
        registration.setOrder(2);
        return registration;
    }
}
