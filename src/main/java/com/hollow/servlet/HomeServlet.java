package com.hollow.servlet;

import com.hollow.bean.User;
import com.hollow.service.LikeService;
import com.hollow.service.PostQueryService;
import com.hollow.util.AuthUtil;

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
public class HomeServlet extends HttpServlet {

    private final PostQueryService postQueryService = new PostQueryService();
    private final LikeService likeService = new LikeService();

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

        PostQueryService.PostPage postPage = postQueryService.listPublished(keyword, page);

        request.setAttribute("keyword", keyword);
        request.setAttribute("postPage", postPage);

        // 登录用户附带已赞帖子集合，页面据此显示"点赞 / 已赞"按钮
        User user = AuthUtil.currentUser(request);
        if (user != null) {
            request.setAttribute("likedIds", likeService.findLikedPostIds(user.getId()));
        }

        request.getRequestDispatcher("/jsp/placeholder/home.jsp").forward(request, response);
    }
}
