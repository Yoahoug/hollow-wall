package com.hollow.dao;

import com.hollow.bean.Xhs_Post;
import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 创作侧对 post 表的数据访问（xhaosen1）。
 *
 * 按分工约定，创作侧对 post 只做三件事：
 *   1. INSERT 新帖（状态一律 PENDING）
 *   2. 作者删除自己的帖子
 *   3. 维护 like_count（点赞功能的计数更新）
 * 另外包含"我的页面"查自己帖子和"按 id 查帖子"（供审核模块调用）。
 *
 * 首页列表、详情页等公开查询由浏览侧（yujie8791-ai）自己实现，
 * 两边不共用同一份代码，避免互相改到对方的文件。
 */
public class Xhs_PostWriteDao {

    /** 查询用的列清单，两个查询方法共用 */
    private static final String COLUMNS =
            "id, user_id, title, content, status, reject_reason, reviewer_id, "
          + "reviewed_at, like_count, comment_count, created_at";

    /**
     * 新增一条帖子。状态固定 PENDING，等待审核。
     */
    public boolean insert(Xhs_Post post) {
        String sql = "INSERT INTO post (user_id, title, content, status) "
                   + "VALUES (?, ?, ?, 'PENDING')";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, post.getUserId());
            stmt.setString(2, post.getTitle());
            stmt.setString(3, post.getContent());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            throw new RuntimeException("保存帖子失败", e);
        }
    }

    /**
     * 作者删除自己的帖子。
     * WHERE 里带上 user_id，不是自己的帖子删不掉。
     * 传入外部 Connection，与清理该帖点赞记录放在同一事务里，由 Service 管理。
     */
    public int deleteOwn(Connection conn, long postId, long userId) throws SQLException {
        String sql = "DELETE FROM post WHERE id = ? AND user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, postId);
            stmt.setLong(2, userId);
            return stmt.executeUpdate();
        }
    }

    /**
     * 更新帖子的点赞数，delta 为 1 表示加一，-1 表示减一。
     * GREATEST 保证计数不会减成负数。
     * 传入外部 Connection，与点赞记录的插入/删除放在同一事务里，由 Service 管理。
     */
    public int changeLikeCount(Connection conn, long postId, int delta) throws SQLException {
        String sql = "UPDATE post SET like_count = GREATEST(like_count + ?, 0) WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delta);
            stmt.setLong(2, postId);
            return stmt.executeUpdate();
        }
    }

    /**
     * 按 id 查一条帖子，什么状态都查得出来。
     * 审核模块展示内容时也用这个方法（跨模块调用见分工文档第 5 节）。
     * 查不到返回 null。
     */
    public Xhs_Post findById(long postId) {
        String sql = "SELECT " + COLUMNS + " FROM post WHERE id = ?";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return toPost(rs);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("按 id 查询帖子失败", e);
        }
    }

    /**
     * 查某个用户的全部帖子，按时间倒序。"我的"页面用。
     * 待审核、已发布、已驳回、已隐藏都查出来，页面上各自标注状态。
     */
    public List<Xhs_Post> findByUser(long userId) {
        String sql = "SELECT " + COLUMNS + " FROM post WHERE user_id = ? "
                   + "ORDER BY created_at DESC, id DESC";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Xhs_Post> posts = new ArrayList<>();
            while (rs.next()) {
                posts.add(toPost(rs));
            }
            return posts;

        } catch (Exception e) {
            throw new RuntimeException("查询用户帖子失败", e);
        }
    }

    /**
     * 把结果集当前行转成 Xhs_Post 对象。
     */
    private Xhs_Post toPost(ResultSet rs) throws SQLException {
        Xhs_Post post = new Xhs_Post();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setStatus(rs.getString("status"));
        post.setRejectReason(rs.getString("reject_reason"));
        // getLong 遇到 NULL 返回 0，reviewer_id 只在已审核时有值，0 正好表示没审核过
        post.setReviewerId(rs.getLong("reviewer_id"));
        post.setReviewedAt(rs.getTimestamp("reviewed_at"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        return post;
    }
}
