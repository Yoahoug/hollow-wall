
package com.hollow.dao;

import com.hollow.bean.Comment;
import com.hollow.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {

    /**
     * 查询指定帖子的评论，按发布时间从早到晚排列。
     */
    public List<Comment> findByPostId(long postId) {
        List<Comment> comments = new ArrayList<>();

        String sql =
                "SELECT c.id, c.post_id, c.user_id, c.content, " +
                        "c.created_at, u.nickname " +
                        "FROM post_comment c " +
                        "LEFT JOIN sys_user u ON c.user_id = u.id " +
                        "WHERE c.post_id = ? " +
                        "ORDER BY c.created_at ASC, c.id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();

                    comment.setId(rs.getLong("id"));
                    comment.setPostId(rs.getLong("post_id"));
                    comment.setUserId(rs.getLong("user_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(
                            rs.getTimestamp("created_at"));
                    comment.setNickname(rs.getString("nickname"));

                    comments.add(comment);
                }
            }

            return comments;

        } catch (Exception e) {
            throw new RuntimeException("查询评论失败", e);
        }
    }

    /**
     * 添加一条评论。
     */
    public boolean addComment(long postId, long userId, String content) {
        String sql =
                "INSERT INTO post_comment " +
                        "(post_id, user_id, content) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            stmt.setString(3, content);

            return stmt.executeUpdate() == 1;

        } catch (Exception e) {
            throw new RuntimeException("提交评论失败", e);
        }
    }
}
