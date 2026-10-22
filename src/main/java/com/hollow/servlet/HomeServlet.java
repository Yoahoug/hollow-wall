
package com.hollow.servlet;

import com.hollow.bean.Post;
import com.hollow.dao.PostDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import java.net.URLEncoder;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final PostDao postDao = new PostDao();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取搜索关键词
        String keyword = request.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        // 获取当前页码
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        if (page < 1) {
            page = 1;
        }

        // 每页显示 5 条帖子
        int pageSize = 5;

        // 查询帖子列表和总数
        List<Post> posts =
                postDao.findPublishedPosts(keyword, page, pageSize);

        int totalPosts = postDao.countPublishedPosts(keyword);
        int totalPages = (int) Math.ceil(totalPosts / (double) pageSize);

        // 页码不能超过最后一页
        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
            posts = postDao.findPublishedPosts(keyword, page, pageSize);
        }

        // 将数据交给 JSP
        request.setAttribute("posts", posts);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute(
                "encodedKeyword",
                URLEncoder.encode(keyword, "UTF-8")
        );
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalPosts", totalPosts);

        request.getRequestDispatcher("/jsp/placeholder/home.jsp")
                .forward(request, response);
    }
}
