package com.hollow.filter;

import com.hollow.bean.Yoa_User;
import com.hollow.util.Yoa_AuthUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理员过滤器，只拦 /admin/* 。
 *
 * 三种情况：
 *   没登录           -> 跳到登录页
 *   登录了但不是管理员 -> 跳回首页
 *   是管理员          -> 放行
 */
@WebFilter("/admin/*")
public class Yoa_AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 不需要初始化参数
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 过滤器拿到的参数是父类型，先转成 Http 的
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 1. 先看有没有登录
        Yoa_User user = Yoa_AuthUtil.currentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 2. 再看是不是管理员
        if (!user.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        // 3. 是管理员，放行
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 没有要释放的资源
    }
}
