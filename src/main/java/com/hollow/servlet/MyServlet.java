package com.hollow.servlet;

import com.hollow.bean.Post;
import com.hollow.bean.User;
import com.hollow.service.LikeService;
import com.hollow.service.PostService;
import com.hollow.util.AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * "我的"页面 Servlet（xhaosen1）。
 *
 * GET /app/my   显示个人信息和自己的全部帖子，含各自的审核状态
 *
 * 未登录访问会被登录过滤器拦下，到这里一定已经登录。
 */
@WebServlet("/app/my")
public class MyServlet extends HttpServlet {

    private PostService postService = new PostService();
    private LikeService likeService = new LikeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 取当前登录用户，过滤器保证不为 null
        User user = AuthUtil.currentUser(request);

        // 2. 查自己的全部帖子，待审核、已发布、已驳回、已隐藏都在里面
        List<Post> posts = postService.findByUser(user.getId());

        // 3. 查自己点赞过的帖子，页面上好把按钮显示成"取消点赞"
        Set<Long> likedIds = likeService.findLikedPostIds(user.getId());

        // 4. 交给页面渲染
        request.setAttribute("posts", posts);
        request.setAttribute("likedIds", likedIds);
        request.getRequestDispatcher("/jsp/post/my.jsp").forward(request, response);
    }
}
