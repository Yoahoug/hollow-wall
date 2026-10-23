package com.hollow.service;

import com.hollow.bean.Yuj_PostComment;
import com.hollow.dao.Yuj_CommentDao;

import java.util.List;

/**
 * 评论业务逻辑。
 *
 * 只写 post_comment 表，不更新 post.comment_count：
 * 按分工约定，浏览侧对 post 表只有 SELECT 权限，帖子上的评论数改为实时统计。
 */
public class Yuj_CommentService {

    private final Yuj_CommentDao commentDao = new Yuj_CommentDao();

    public List<Yuj_PostComment> listByPost(long postId) {
        return commentDao.findByPost(postId);
    }

    public boolean add(long postId, long userId, String content) {
        return commentDao.insert(postId, userId, content);
    }
}
