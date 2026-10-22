
package com.hollow.servlet;

import com.hollow.bean.Post;
import com.hollow.dao.PostDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/share")
public class ShareServlet extends HttpServlet {

    private final PostDao postDao = new PostDao();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取帖子 ID
        String idText = request.getParameter("id");

        // 检查 ID 是否为空或格式错误
        if (idText == null || !idText.matches("\\d+")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确"
            );
            return;
        }

        long postId;

        try {
            postId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确"
            );
            return;
        }

        // 查询已发布的帖子
        Post post = postDao.findPublishedPostById(postId);

        if (post == null) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "帖子不存在或暂不可分享"
            );
            return;
        }

        // 将帖子传递给分享页面
        request.setAttribute("post", post);

        request.getRequestDispatcher(
                "/jsp/placeholder/share.jsp"
        ).forward(request, response);
    }
}
