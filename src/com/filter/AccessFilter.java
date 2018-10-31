package com.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.utils.UserSessionUtil;

/**
 *
 *
 */
public class AccessFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.setContentType("text/html;charset=UTF-8");

        String ip = request.getRemoteAddr();
        resp.setHeader("Access-Control-Allow-Origin", "*");

        if (noFilterUrl(req)) {
            chain.doFilter(request, response);
        } else {
            boolean isLogin = isLogin(req, resp);//处理自动登录
            if (isLogin) {
                chain.doFilter(request, response);
            }
        }

    }

    private static final String[] STATIC_NO_FILTER_URL = {"/index.jsp", "/static/"};

    private boolean noFilterUrl(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        for (String s : STATIC_NO_FILTER_URL) {
            if (servletPath.startsWith(s)) {
                return true;
            }
        }
        return true;
    }

    /**
     * 当访问html、jsp时，读取cookie，自动登录
     *
     * @param req
     */
    private boolean isLogin(HttpServletRequest req, HttpServletResponse resp) {
        UserSessionUtil usu = new UserSessionUtil(req);
        if (usu.getUser() != null) {//访问后台页面
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

}
