package com.hollow.servlet;

import com.hollow.bean.PostView;
import com.hollow.bean.User;
import com.hollow.service.PostQueryService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 分享页：只展示单条帖子的正文和分享链接，方便发给同学。
 * 可见性规则与详情页一致。
 */
@WebServlet("/share")
public class ShareServlet extends HttpServlet {

    private final PostQueryService postQueryService = new PostQueryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        PostView post = postQueryService.findById(id);
        User user = AuthUtil.currentUser(request);
        if (post == null || !postQueryService.canView(post, user)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("post", post);
        request.getRequestDispatcher("/jsp/post/share.jsp").forward(request, response);
    }
}
