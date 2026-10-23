package com.hollow.servlet;

import com.hollow.bean.Yoa_User;
import com.hollow.service.Xhs_LikeService;
import com.hollow.service.Yuj_PostQueryService;
import com.hollow.util.Yoa_AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 首页：已发布帖子的列表、分页与关键词搜索。
 */
@WebServlet("/home")
public class Yuj_HomeServlet extends HttpServlet {

    private final Yuj_PostQueryService postQueryService = new Yuj_PostQueryService();
    private final Xhs_LikeService likeService = new Xhs_LikeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 没有关键词时按空字符串处理，DAO 会跳过搜索条件
        String keyword = request.getParameter("q");
        if (keyword == null) {
            keyword = "";
        }
        keyword = keyword.trim();

        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (Exception e) {
            // 没传 page 或传了非数字，都按第一页处理
            page = 1;
        }

        Yuj_PostQueryService.PostPage postPage = postQueryService.listPublished(keyword, page);

        request.setAttribute("keyword", keyword);
        request.setAttribute("postPage", postPage);

        // 登录用户附带已赞帖子集合，页面据此显示"点赞 / 已赞"按钮
        Yoa_User user = Yoa_AuthUtil.currentUser(request);
        if (user != null) {
            request.setAttribute("likedIds", likeService.findLikedPostIds(user.getId()));
        }

        request.getRequestDispatcher("/jsp/Yuj_Home.jsp").forward(request, response);
    }
}
