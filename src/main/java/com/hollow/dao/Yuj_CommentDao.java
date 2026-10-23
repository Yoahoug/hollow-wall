package com.hollow.dao;

import com.hollow.bean.Yuj_PostComment;
import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论表 post_comment 的数据访问。
 * 按分工约定，评论的读写都归浏览侧。
 */
public class Yuj_CommentDao {

    /**
     * 查询某个帖子下的全部评论，按时间从早到晚。
     */
    public List<Yuj_PostComment> findByPost(long postId) {
        String sql = "SELECT id, post_id, user_id, content, created_at "
                   + "FROM post_comment WHERE post_id = ? "
                   + "ORDER BY created_at ASC, id ASC";

        List<Yuj_PostComment> comments = new ArrayList<>();

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Yuj_PostComment comment = new Yuj_PostComment();
                comment.setId(rs.getLong("id"));
                comment.setPostId(rs.getLong("post_id"));
                comment.setUserId(rs.getLong("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at"));
                comments.add(comment);
            }

        } catch (Exception e) {
            throw new RuntimeException("查询评论失败", e);
        }

        return comments;
    }

    /**
     * 新增一条评论，返回是否插入成功。
     */
    public boolean insert(long postId, long userId, String content) {
        String sql = "INSERT INTO post_comment (post_id, user_id, content) VALUES (?, ?, ?)";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            stmt.setString(3, content);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            throw new RuntimeException("新增评论失败", e);
        }
    }
}
