package com.hollow.servlet;

import com.hollow.dao.Ewc_AdminUserDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 后台统计页 Servlet。
 *
 * GET /admin/stats   展示总数统计，数据交给 admin/Ewc_Home.jsp 渲染。
 *
 * 分工文档第 6 节登记了这个路由，此前没有对应的 Servlet，访问一律 404。
 * 统计能力已经写在 Ewc_AdminUserDao 里，这里只负责取数并转发页面。
 */
@WebServlet("/admin/stats")
public class Ewc_StatsServlet extends HttpServlet {

    private final Ewc_AdminUserDao adminUserDao = new Ewc_AdminUserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("userCount", adminUserDao.countUsers());
        request.setAttribute("postCount", adminUserDao.countPosts());
        request.setAttribute("likeCount", adminUserDao.countLikes());
        request.setAttribute("commentCount", adminUserDao.countComments());

        request.getRequestDispatcher("/jsp/admin/Ewc_Home.jsp").forward(request, response);
    }
}
