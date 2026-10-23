package com.hollow.servlet;

import com.hollow.bean.User;
import com.hollow.service.LikeService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 点赞 Servlet（xhaosen1）。
 *
 * POST /app/like   表单里带 postId，可选 action
 *
 *   action 不传或传 like    点赞
 *   action 传 cancel        取消点赞
 *
 * 防重复点赞靠唯一约束，不做先查后插；点赞记录和计数更新在同一事务内，
 * 都由 LikeService 处理。处理完带着一次性提示跳回来源页面。
 *
 * /app/* 已经过登录过滤器，到这里一定已经登录。
 */
@WebServlet("/app/like")
public class LikeServlet extends HttpServlet {

    private LikeService likeService = new LikeService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取帖子 id，解析不了直接回"我的"页面
        long postId;
        try {
            postId = Long.parseLong(request.getParameter("postId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/app/my");
            return;
        }

        // 2. 按参数决定是点赞还是取消
        boolean cancel = "cancel".equals(request.getParameter("action"));
        User user = AuthUtil.currentUser(request);
        String error;
        if (cancel) {
            error = likeService.cancel(user.getId(), postId);
        } else {
            error = likeService.like(user.getId(), postId);
        }

        // 3. 失败原因用一次性提示带到下一个页面显示
        if (error != null) {
            HttpSession session = request.getSession();
            session.setAttribute("flashMsg", error);
            session.setAttribute("flashType", "error");
        }

        // 4. 跳回来源页面（点赞按钮可能在首页、详情页或"我的"页面），没有就回"我的"
        String back = request.getHeader("Referer");
        if (back == null || back.isBlank()) {
            back = request.getContextPath() + "/app/my";
        }
        response.sendRedirect(back);
    }
}
