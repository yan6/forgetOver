package com.example.demo.tool.filter;

import org.springframework.util.DigestUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SessionFilter implements Filter {

    //不需要登录就可以访问的路径(比如:注册登录等)
    private static final String[] includeUrls = new String[]{
            "/api/user/getUser", "register"};

    //标示符：表示当前用户未登录(可根据自己项目需要改为json样式)
    private static final String NO_LOGIN = "您还未登录";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        generateSign(request);

        //是否需要过滤
        boolean needFilter = isNeedFilter(uri);

        if (!needFilter) { //不需要过滤直接传给下一个过滤器
            filterChain.doFilter(servletRequest, servletResponse);
        } else { //需要过滤器
            // session中包含user对象,则是登录状态
            if (session != null && session.getAttribute("user") != null) {
                // System.out.println("user:"+session.getAttribute("user"));
                filterChain.doFilter(request, response);
            } else {
                String requestType = request.getHeader("X-Requested-With");
                //判断是否是ajax请求
                if (requestType != null && "XMLHttpRequest".equals(requestType)) {
                    response.getWriter().write(NO_LOGIN);
                } else {
                    //重定向到登录页(需要在static文件夹下建立此html文件)
                    //response.sendRedirect(request.getContextPath()+"/user/login.html");
                    System.out.print("到登录页...");
                }
            }
        }


    }

    private void generateSign(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String _newSign = test2(parameterMap);
        String newSign = request.getParameter("newSign");
        System.out.println("generateSign:newSign:" + newSign + ",_newSign:" + _newSign);
    }

    private String test2(Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        // 拼接url中的参数
        if (params != null) {
            ArrayList<String> keyList = new ArrayList<>(params.keySet());
            Collections.sort(keyList); //key正序
            for (int i = 0; i < keyList.size(); i++) {
                String k = keyList.get(i);
                if ("newSign".equals(k)) {
                    // 此参数不加加密
                    continue;
                }
                String[] v = params.get(k);
                //空值不传递，不参与签名组串
                if (null != v && !"".equals(v[0])) {
                    sb.append(v[0]);
                }
            }
        }

        // 拼接KEY
        sb = sb.append("0jdiu5k7");
        String sign = null;
        try {
            //加密,结果转换为小写字符
            sign = DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toLowerCase();
        } catch (Exception e) {
        }
        return sign;
    }

    @Override
    public void destroy() {

    }

    private boolean isNeedFilter(String uri) {
        for (String includeUrl : includeUrls) {
            if (includeUrl.equals(uri)) {
                return false;
            }
        }
        return true;
    }
}
