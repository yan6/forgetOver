package com.example.demo.tool.filter;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MonitorFilter implements Filter {

    private static final String[] vaildUdid = {"123456", "111111"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();

        System.out.println("filter url:" + uri);
        if (StringUtils.isNotBlank(uri) && uri.contains("monitor")) {
            // session中包含user对象,则是登录状态
            String udid = request.getParameter("udid");
            if (StringUtils.isNotBlank(udid) && !isVaild(udid)) {
                // System.out.println("user:"+session.getAttribute("user"));
                filterChain.doFilter(request, response);
            } else {
                String requestType = request.getHeader("X-Requested-With");
                //判断是否是ajax请求
                if (requestType != null && "XMLHttpRequest".equals(requestType)) {
                    response.getWriter().write("非法请求");
                } else {
                    //重定向到登录页(需要在static文件夹下建立此html文件)
                    //response.sendRedirect(request.getContextPath()+"/user/login.html");
                    System.out.print("非法请求");
                }
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isVaild(String udid) {
        for (String _udid : vaildUdid) {
            if (StringUtils.equals(udid, _udid))
                return true;
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
