package com.hollow.service;

import com.hollow.bean.Yuj_PostView;
import com.hollow.bean.Yoa_User;
import com.hollow.dao.Yuj_PostQueryDao;

import java.util.List;

/**
 * 浏览侧的帖子业务逻辑：列表分页、详情查询、可见性判断。
 */
public class Yuj_PostQueryService {

    /**
     * 每页条数。初始化数据只有 3 条已发布帖子，用 2 条一页便于验证翻页。
     */
    public static final int PAGE_SIZE = 2;

    private final Yuj_PostQueryDao postQueryDao = new Yuj_PostQueryDao();

    /**
     * 分页查询已发布帖子。总数和列表是两条查询，先在 DAO 里分别执行。
     */
    public PostPage listPublished(String keyword, int page) {
        int total = postQueryDao.countPublished(keyword);

        int totalPages = total == 0 ? 1 : (total + PAGE_SIZE - 1) / PAGE_SIZE;
        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        int offset = (page - 1) * PAGE_SIZE;
        List<Yuj_PostView> posts = postQueryDao.findPublished(keyword, offset, PAGE_SIZE);

        return new PostPage(posts, page, PAGE_SIZE, total, totalPages);
    }

    /**
     * 按 id 查询帖子，查不到返回 null。
     */
    public Yuj_PostView findById(long id) {
        return postQueryDao.findById(id);
    }

    /**
     * 帖子是否允许当前访问者查看。
     * 已发布：所有人可见；其它状态：仅作者本人和管理员可见。
     */
    public boolean canView(Yuj_PostView post, Yoa_User user) {
        if ("PUBLISHED".equals(post.getStatus())) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (user.isAdmin()) {
            return true;
        }
        return post.getUserId() == user.getId();
    }

    /**
     * 一页帖子的查询结果，页面直接取这些属性。
     */
    public static class PostPage {

        private final List<Yuj_PostView> posts;
        private final int page;
        private final int pageSize;
        private final int total;
        private final int totalPages;

        public PostPage(List<Yuj_PostView> posts, int page, int pageSize, int total, int totalPages) {
            this.posts = posts;
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.totalPages = totalPages;
        }

        public List<Yuj_PostView> getPosts() {
            return posts;
        }

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotal() {
            return total;
        }

        public int getTotalPages() {
            return totalPages;
        }
    }
}
