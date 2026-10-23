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
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录过滤器，只拦 /app/* 。
 *
 * 作用：
 *   已登录 -> 直接放行
 *   没登录 -> 把用户原本想去的地址记进 Session，跳到登录页
 *
 * 登录成功后由 Yoa_LoginServlet 把这个地址取出来，跳回原页面。
 */
@WebFilter("/app/*")
public class Yoa_LoginFilter implements Filter {

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
        Yoa_User user = Yoa_AuthUtil.currentUser(req);
        if (user != null) {
            chain.doFilter(request, response);
            return;
        }

        // 2. 没登录，先记下他想去的地址。
        //    只记 GET：POST 的提交内容（比如刚写好的评论）在跳转后拿不回来，
        //    登录后按 GET 回跳只会得到 405，不如让用户回到首页重新操作
        HttpSession session = req.getSession();
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String url = req.getRequestURI();
            if (req.getQueryString() != null) {
                // 带参数的地址要把 ? 后面也拼上，比如 /app/post?id=3
                url = url + "?" + req.getQueryString();
            }
            session.setAttribute("redirectUrl", url);
        } else {
            // POST 不记录，同时清掉之前可能残留的地址，避免登录后跳到旧页面
            session.removeAttribute("redirectUrl");
        }

        // 3. 跳到登录页
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    public void destroy() {
        // 没有要释放的资源
    }
}
