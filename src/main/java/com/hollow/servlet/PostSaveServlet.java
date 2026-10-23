package com.hollow.servlet;

import com.hollow.bean.User;
import com.hollow.service.PostService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 保存新帖 Servlet（xhaosen1）。
 *
 * POST /app/post/save   处理发帖表单
 *
 * 新帖一律进入待审核（PENDING），发布成功后跳到"我的"页面，
 * 页面上能看到"审核中"状态。失败时回发帖页，把填过的内容带回去。
 *
 * /app/* 已经过登录过滤器，到这里一定已经登录。
 */
@WebServlet("/app/post/save")
public class PostSaveServlet extends HttpServlet {

    private PostService postService = new PostService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取表单内容，去掉首尾空白
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        if (title != null) {
            title = title.trim();
        }
        if (content != null) {
            content = content.trim();
        }

        // 2. 先检查表单填得对不对
        String error = check(title, content);

        // 3. 表单没问题，交给 Service 发布，状态固定 PENDING
        if (error == null) {
            User user = AuthUtil.currentUser(request);
            error = postService.publish(user.getId(), title, content);
        }

        // 4. 有错误就回发帖页，把填过的内容带回去
        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("title", title);
            request.setAttribute("content", content);
            request.getRequestDispatcher("/jsp/post/post-new.jsp").forward(request, response);
            return;
        }

        // 5. 发布成功，跳到"我的"页面看审核状态。
        //    重定向后再显示提示，避免刷新时重复提交表单
        HttpSession session = request.getSession();
        session.setAttribute("flashMsg", "发布成功，帖子进入待审核");
        session.setAttribute("flashType", "success");
        response.sendRedirect(request.getContextPath() + "/app/my");
    }

    /**
     * 检查表单内容。
     * 返回 null 表示没问题，返回一句文字表示哪里不对。
     * 标题长度限制对应 post.title 的 VARCHAR(100)。
     */
    private String check(String title, String content) {
        if (title == null || title.isBlank()) {
            return "请填写标题";
        }
        if (title.length() > 100) {
            return "标题不能超过 100 字";
        }
        if (content == null || content.isBlank()) {
            return "请填写正文";
        }
        if (content.length() > 10000) {
            return "正文不能超过 10000 字";
        }
        return null;
    }
}
