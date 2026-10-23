package com.hollow.servlet;

import com.hollow.bean.PostView;
import com.hollow.bean.User;
import com.hollow.service.CommentService;
import com.hollow.service.PostQueryService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 帖子详情页：展示单条帖子与评论列表。
 *
 * 已发布的帖子所有人可见；待审核、已驳回、已隐藏的帖子只有作者本人和管理员可见。
 */
@WebServlet("/post")
public class PostServlet extends HttpServlet {

    private final PostQueryService postQueryService = new PostQueryService();
    private final CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PostView post = loadViewablePost(request, response);
        if (post == null) {
            return;
        }

        request.setAttribute("post", post);
        request.setAttribute("comments", commentService.listByPost(post.getId()));

        // 评论提交失败时 CommentServlet 通过 error 参数把原因带回来
        String error = request.getParameter("error");
        if ("empty".equals(error)) {
            request.setAttribute("error", "评论内容不能为空");
        } else if ("toolong".equals(error)) {
            request.setAttribute("error", "评论最多 500 字");
        }

        request.getRequestDispatcher("/jsp/post/detail.jsp").forward(request, response);
    }

    /**
     * 解析 id 并检查可见性，不允许查看时直接返回 404 并返回 null。
     */
    private PostView loadViewablePost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        PostView post = postQueryService.findById(id);
        User user = AuthUtil.currentUser(request);
        if (post == null || !postQueryService.canView(post, user)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return post;
    }
}
