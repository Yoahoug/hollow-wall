package com.hollow.servlet;

import com.hollow.bean.User;
import com.hollow.service.UserService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录 Servlet。
 *
 * GET  /login   显示登录页
 * POST /login   处理登录
 *
 * 登录成功后的去向：
 *   如果之前被过滤器拦下来过，Session 里有 redirectUrl，就回到原来那个页面；
 *   否则回首页。
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService = new UserService();

    /**
     * 显示登录页。已经登录的人不用再看了，直接回首页。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (AuthUtil.isLogin(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    /**
     * 处理登录表单。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取表单里的邮箱和密码
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 2. 交给 Service 判断能不能登录
        User user = userService.login(email, password);

        // 3. 登录失败：回登录页，显示错误，保留填过的邮箱
        if (user == null) {
            request.setAttribute("error", "邮箱或密码错误");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }

        // 4. 登录成功：把用户对象放进 Session，之后各页面都从这里取
        HttpSession session = request.getSession();
        session.setAttribute(AuthUtil.SESSION_USER, user);

        // 5. 如果之前被过滤器拦过，就跳回原来想去的页面
        String redirectUrl = (String) session.getAttribute("redirectUrl");
        if (redirectUrl != null) {
            // 取完要删掉，不然下次登录还会跳过去
            session.removeAttribute("redirectUrl");
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
