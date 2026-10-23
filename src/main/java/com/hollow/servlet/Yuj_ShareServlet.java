package com.hollow.servlet;

import com.hollow.bean.Yuj_PostView;
import com.hollow.bean.Yoa_User;
import com.hollow.service.Yuj_PostQueryService;
import com.hollow.util.Yoa_AuthUtil;

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
public class Yuj_ShareServlet extends HttpServlet {

    private final Yuj_PostQueryService postQueryService = new Yuj_PostQueryService();

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

        Yuj_PostView post = postQueryService.findById(id);
        Yoa_User user = Yoa_AuthUtil.currentUser(request);
        if (post == null || !postQueryService.canView(post, user)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("post", post);
        request.getRequestDispatcher("/jsp/post/Yuj_Share.jsp").forward(request, response);
    }
}
