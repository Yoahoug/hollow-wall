package com.hollow.dao;

import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * post_like 点赞表的数据访问类，整张表归创作侧（xhaosen1）读写。
 *
 * 防重复点赞靠 uk_post_user 唯一约束，代码里不做先查后插：
 *   重复插入时 MySQL 抛 SQLIntegrityConstraintViolationException，
 *   由 Xhs_LikeService 捕获后提示"已经赞过"。
 *
 * 点赞和取消都要连带更新 post 的 like_count，两步在同一事务内，
 * 由 Xhs_LikeService 开事务并把 Connection 传进来。
 */
public class Xhs_LikeDao {

    /**
     * 新增一条点赞记录。
     * 重复点赞会因唯一约束 uk_post_user 抛 SQLIntegrityConstraintViolationException。
     * 传入外部 Connection，由 Service 管理事务。
     */
    public int insert(Connection conn, long postId, long userId) throws SQLException {
        String sql = "INSERT INTO post_like (post_id, user_id, created_at) VALUES (?, ?, NOW())";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            return stmt.executeUpdate();
        }
    }

    /**
     * 删除某个用户对某条帖子的点赞（取消点赞用）。
     * 传入外部 Connection，由 Service 管理事务。
     * 返回影响行数，0 表示本来就没有这条点赞记录。
     */
    public int delete(Connection conn, long postId, long userId) throws SQLException {
        String sql = "DELETE FROM post_like WHERE post_id = ? AND user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            return stmt.executeUpdate();
        }
    }

    /**
     * 删除一条帖子的全部点赞记录。
     * 作者删帖时在同一事务里调用，避免留下指向已删帖子的孤儿数据。
     * 传入外部 Connection，由 Service 管理事务。
     */
    public int deleteByPost(Connection conn, long postId) throws SQLException {
        String sql = "DELETE FROM post_like WHERE post_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            return stmt.executeUpdate();
        }
    }

    /**
     * 查某个用户点赞过的帖子 id 集合。
     * "我的"页面用：标出哪些帖子自己已经赞过，好把按钮显示成"取消点赞"。
     */
    public Set<Long> findLikedPostIds(long userId) {
        String sql = "SELECT post_id FROM post_like WHERE user_id = ?";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            Set<Long> likedIds = new HashSet<>();
            while (rs.next()) {
                likedIds.add(rs.getLong("post_id"));
            }
            return likedIds;

        } catch (Exception e) {
            throw new RuntimeException("查询用户点赞记录失败", e);
        }
    }
}
