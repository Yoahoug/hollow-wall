package com.hollow.servlet;

import com.hollow.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注册 Servlet。
 *
 * GET  /register   显示注册页
 * POST /register   处理注册
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserService userService = new UserService();

    /**
     * 显示注册页。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    /**
     * 处理注册表单。
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取表单内容
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");
        String nickname = request.getParameter("nickname");

        // 2. 先检查表单填得对不对
        String error = check(email, password, confirm, nickname);

        // 3. 表单没问题，再交给 Service 去注册
        if (error == null) {
            error = userService.register(email, password, nickname);
        }

        // 4. 有错误就回注册页，把填过的内容带回去
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("email", email);
            request.setAttribute("nickname", nickname);
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        // 5. 注册成功，跳到登录页并提示一下
        request.setAttribute("success", "注册成功，请登录");
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    /**
     * 检查表单内容。
     * 返回 null 表示没问题，返回一句文字表示哪里不对。
     * 只做最基本的检查，不写得太复杂。
     */
    private String check(String email, String password, String confirm, String nickname) {
        if (email == null || email.isBlank()) {
            return "请填写邮箱";
        }
        if (nickname == null || nickname.isBlank()) {
            return "请填写昵称";
        }
        if (password == null || password.length() < 6) {
            return "密码至少 6 位";
        }
        if (!password.equals(confirm)) {
            return "两次输入的密码不一致";
        }
        return null;
    }
}
