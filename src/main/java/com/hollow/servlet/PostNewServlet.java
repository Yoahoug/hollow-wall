package com.hollow.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 发帖页 Servlet（xhaosen1）。
 *
 * GET /app/post/new   显示发帖表单
 *
 * 表单提交到 POST /app/post/save，由 PostSaveServlet 处理。
 * 未登录访问会被登录过滤器拦下并记录原始地址，登录后回到本页。
 */
@WebServlet("/app/post/new")
public class PostNewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/post/post-new.jsp").forward(request, response);
    }
}
