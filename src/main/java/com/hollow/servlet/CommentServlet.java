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
 * 提交评论。挂在 /app/comment 下，未登录会被登录过滤器拦到登录页。
 * 提交成功后重定向回详情页，避免刷新时重复提交。
 */
@WebServlet("/app/comment")
public class CommentServlet extends HttpServlet {

    /** 与 post_comment.content 的字段长度一致 */
    private static final int MAX_LENGTH = 500;

    private final PostQueryService postQueryService = new PostQueryService();
    private final CommentService commentService = new CommentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 评论是中文，必须在取参数前指定请求编码
        request.setCharacterEncoding("UTF-8");

        User user = AuthUtil.currentUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        long postId;
        try {
            postId = Long.parseLong(request.getParameter("postId"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 只有已发布的帖子可以评论
        PostView post = postQueryService.findById(postId);
        if (post == null || !"PUBLISHED".equals(post.getStatus())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String content = request.getParameter("content");
        if (content == null) {
            content = "";
        }
        content = content.trim();

        if (content.isEmpty()) {
            redirectBack(response, request, postId, "empty");
            return;
        }
        if (content.length() > MAX_LENGTH) {
            redirectBack(response, request, postId, "toolong");
            return;
        }

        commentService.add(postId, user.getId(), content);
        redirectBack(response, request, postId, null);
    }

    /**
     * 回到详情页的评论区。errorCode 为 null 表示提交成功。
     */
    private void redirectBack(HttpServletResponse response, HttpServletRequest request,
                              long postId, String errorCode) throws IOException {
        String url = request.getContextPath() + "/post?id=" + postId;
        if (errorCode != null) {
            url = url + "&error=" + errorCode;
        }
        response.sendRedirect(url + "#comments");
    }
}
