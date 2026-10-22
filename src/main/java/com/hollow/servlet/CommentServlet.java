
package com.hollow.servlet;

import com.hollow.bean.User;
import com.hollow.dao.CommentDao;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/app/comment")
public class CommentServlet extends HttpServlet {

    private final CommentDao commentDao = new CommentDao();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 检查登录状态
        if (!AuthUtil.isLogin(request)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        // 获取登录用户
        User user = AuthUtil.currentUser(request);

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        // 获取并校验帖子 ID
        String idText = request.getParameter("postId");

        if (idText == null || !idText.matches("\\d+")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确");
            return;
        }

        long postId;

        try {
            postId = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "帖子 ID 不正确");
            return;
        }

        // 获取并校验评论内容
        String content = request.getParameter("content");

        if (content == null || content.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "评论内容不能为空");
            return;
        }

        content = content.trim();

        if (content.length() > 500) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "评论不能超过500个字符");
            return;
        }

        // 保存评论
        try {
            boolean success = commentDao.addComment(
                    postId, user.getId(), content);

            if (!success) {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "评论提交失败");
                return;
            }
        } catch (RuntimeException e) {
            throw new ServletException("保存评论失败", e);
        }

        // 保存成功后返回帖子详情页
        response.sendRedirect(
                request.getContextPath() + "/post?id=" + postId);
    }
}
