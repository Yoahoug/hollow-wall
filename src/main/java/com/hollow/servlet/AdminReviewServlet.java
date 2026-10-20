package com.hollow.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 审核页 Servlet（占位）。正式内容由 ewclpy 实现。
 */
@WebServlet("/admin/review")
public class AdminReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/placeholder/admin-review.jsp").forward(request, response);
    }
}
