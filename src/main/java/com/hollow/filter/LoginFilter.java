package com.hollow.filter;

import com.hollow.bean.User;
import com.hollow.util.AuthUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录过滤器，只拦 /app/* 。
 *
 * 作用：
 *   已登录 -> 直接放行
 *   没登录 -> 把用户原本想去的地址记进 Session，跳到登录页
 *
 * 登录成功后由 LoginServlet 把这个地址取出来，跳回原页面。
 */
@WebFilter("/app/*")
public class LoginFilter implements Filter {

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

        // 1. 已经登录，直接放行
        User user = AuthUtil.currentUser(req);
        if (user != null) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 没登录，先记下他想去的地址
        String url = req.getRequestURI();
        if (req.getQueryString() != null) {
            // 带参数的地址要把 ? 后面也拼上，比如 /app/post?id=3
            url = url + "?" + req.getQueryString();
        }

        HttpSession session = req.getSession();
        session.setAttribute("redirectUrl", url);

        // 3. 跳到登录页
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    public void destroy() {
        // 没有要释放的资源
    }
}
