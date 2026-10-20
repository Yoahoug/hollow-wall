package com.hollow.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 发帖页 Servlet（占位）。正式内容由 xhaosen1 实现。
 */
@WebServlet("/app/post/new")
public class PostNewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/placeholder/post-new.jsp").forward(request, response);
    }
}
