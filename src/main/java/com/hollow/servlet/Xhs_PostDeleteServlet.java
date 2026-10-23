package com.hollow.servlet;

import com.hollow.bean.Yoa_User;
import com.hollow.service.Xhs_PostService;
import com.hollow.util.Yoa_AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 删除自己的帖子 Servlet（xhaosen1）。
 *
 * POST /app/post/delete   表单里带 postId
 *
 * 只能删除自己的帖子（DAO 的 WHERE 里带 user_id）。
 * 删帖和清理该帖点赞记录在同一事务内，由 Xhs_PostService 处理。
 *
 * /app/* 已经过登录过滤器，到这里一定已经登录。
 */
@WebServlet("/app/post/delete")
public class Xhs_PostDeleteServlet extends HttpServlet {

    private Xhs_PostService postService = new Xhs_PostService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取帖子 id，解析不了按失败处理
        long postId;
        try {
            postId = Long.parseLong(request.getParameter("postId"));
        } catch (NumberFormatException e) {
            postId = 0;
        }

        // 2. 删除，只有作者本人能删掉
        Yoa_User user = Yoa_AuthUtil.currentUser(request);
        boolean success = postService.deleteOwn(user.getId(), postId);

        // 3. 结果用一次性提示带到"我的"页面显示
        HttpSession session = request.getSession();
        if (success) {
            session.setAttribute("flashMsg", "帖子已删除");
            session.setAttribute("flashType", "success");
        } else {
            session.setAttribute("flashMsg", "删除失败，帖子不存在或不是你的帖子");
            session.setAttribute("flashType", "error");
        }
        response.sendRedirect(request.getContextPath() + "/app/my");
    }
}
