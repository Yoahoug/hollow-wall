package com.hollow.servlet;

import com.hollow.bean.Xhs_Post;
import com.hollow.bean.Yoa_User;
import com.hollow.service.Ewc_ReviewService;
import com.hollow.util.Yoa_AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * 审核后台（ewclpy）。
 *
 * GET  /admin/review   待审核队列 + 已发布列表
 * POST /admin/review   通过 / 驳回
 *
 * /admin/* 已经过管理员过滤器，到这里一定是管理员。
 */
@WebServlet("/admin/review")
public class Ewc_ReviewServlet extends HttpServlet {

    private final Ewc_ReviewService reviewService = new Ewc_ReviewService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Xhs_Post> pendingPosts = reviewService.listPending();
        List<Xhs_Post> publishedPosts = reviewService.listPublished();

        request.setAttribute("pendingPosts", pendingPosts);
        request.setAttribute("pendingCount", pendingPosts.size());
        request.setAttribute("publishedPosts", publishedPosts);

        // 操作失败时用 flash 把提示带回队列页
        HttpSession session = request.getSession();
        Object flashMsg = session.getAttribute("flashMsg");
        if (flashMsg != null) {
            request.setAttribute("error", flashMsg);
            session.removeAttribute("flashMsg");
        }

        request.getRequestDispatcher("/jsp/Ewc_Review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        long postId;
        try {
            postId = Long.parseLong(request.getParameter("postId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "postId 不合法");
            return;
        }

        // 审核人取当前登录的管理员，不从表单传，避免伪造
        Yoa_User reviewer = Yoa_AuthUtil.currentUser(request);
        if (reviewer == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String error;

        if ("approve".equals(action)) {
            error = reviewService.approve(postId, reviewer.getId());
        } else if ("reject".equals(action)) {
            error = reviewService.reject(postId, reviewer.getId(),
                                         request.getParameter("rejectReason"));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "action 不合法");
            return;
        }

        if (error != null) {
            // 驳回缺理由这类校验失败，回队列页让管理员改完再提交
            request.getSession().setAttribute("flashMsg", error);
        }
        response.sendRedirect(request.getContextPath() + "/admin/review");
    }
}
