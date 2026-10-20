package com.hollow.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 退出登录。
 * POST /logout
 *
 * 把整个 Session 作废，然后回首页。
 */
@WebServlet("/logout")
public class Yoa_LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 拿旧的 Session，没有就算了（不要新建一个空的）
        HttpSession session = request.getSession(false);

        if (session != null) {
            // 整作废，不只是删掉用户属性
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}
