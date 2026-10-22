
package com.hollow.servlet;

import com.hollow.bean.Post;
import com.hollow.dao.PostDao;
import com.hollow.bean.Comment;
import com.hollow.dao.CommentDao;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private final PostDao postDao = new PostDao();
    private final CommentDao commentDao = new CommentDao();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String idText = request.getParameter("id");

        if (idText == null || !idText.matches("\\d+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确");
            return;
        }

        long postId;

        try {
            postId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确");
            return;
        }

        Post post = postDao.findPublishedPostById(postId);

        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "帖子不存在或暂不可查看");
            return;
        }

        request.setAttribute("post", post);

// 查询当前帖子的评论
        List<Comment> comments = commentDao.findByPostId(postId);
        request.setAttribute("comments", comments);

        request.getRequestDispatcher(
                "/jsp/placeholder/post-detail.jsp"
        ).forward(request, response);
    }
}
