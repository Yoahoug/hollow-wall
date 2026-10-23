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
 * 治理侧对 post 表审核状态的数据访问（ewclpy）。
 *
 * 按分工约定，治理侧对 post 只允许 UPDATE 审核相关列
 * （status / reject_reason / reviewer_id / reviewed_at），
 * 不改标题、正文、点赞数，也不删除帖子。
 * 审核时展示内容所需的读取，走创作侧的 Xhs_PostWriteDao。
 */
public class Ewc_ReviewDao {

    /**
     * 查出待审核的帖子，按提交时间从早到晚。
     * 先提交的先处理，避免旧帖一直压在队尾。
     */
    public List<Xhs_Post> findPending() {
        String sql = "SELECT id, user_id, title, content, status, reject_reason, "
                   + "reviewer_id, reviewed_at, like_count, created_at, "
                   + "(SELECT COUNT(*) FROM post_comment c WHERE c.post_id = post.id) AS comment_count "
                   + "FROM post WHERE status = 'PENDING' "
                   + "ORDER BY created_at ASC, id ASC";

        List<Xhs_Post> posts = new ArrayList<>();

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(toPost(rs));
            }
            return posts;

        } catch (Exception e) {
            throw new RuntimeException("查询待审核帖子失败", e);
        }
    }

    /**
     * 已发布的帖子，按发布时间倒序。后台列表展示用。
     */
    public List<Xhs_Post> findPublished() {
        String sql = "SELECT id, user_id, title, content, status, reject_reason, "
                   + "reviewer_id, reviewed_at, like_count, created_at, "
                   + "(SELECT COUNT(*) FROM post_comment c WHERE c.post_id = post.id) AS comment_count "
                   + "FROM post WHERE status = 'PUBLISHED' "
                   + "ORDER BY created_at DESC, id DESC";

        List<Xhs_Post> posts = new ArrayList<>();

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                posts.add(toPost(rs));
            }
            return posts;

        } catch (Exception e) {
            throw new RuntimeException("查询已发布帖子失败", e);
        }
    }

    /**
     * 通过审核。只有当前仍是 PENDING 的帖子会被更新，
     * 影响行数为 0 表示已被别人处理过，不做重复审核。
     */
    public boolean approve(long postId, long reviewerId) {
        String sql = "UPDATE post SET status = 'PUBLISHED', reject_reason = NULL, "
                   + "reviewer_id = ?, reviewed_at = NOW() "
                   + "WHERE id = ? AND status = 'PENDING'";
        return updateWithStatusGuard(sql, postId, reviewerId, null);
    }

    /**
     * 驳回。理由会展示给作者，所以由 Servlet 保证非空。
     * 同样带 status = 'PENDING' 条件，防止重复审核。
     */
    public boolean reject(long postId, long reviewerId, String rejectReason) {
        String sql = "UPDATE post SET status = 'REJECTED', reject_reason = ?, "
                   + "reviewer_id = ?, reviewed_at = NOW() "
                   + "WHERE id = ? AND status = 'PENDING'";
        return updateWithStatusGuard(sql, postId, reviewerId, rejectReason);
    }

    /**
     * 两个操作共用的执行逻辑：按需要设置驳回理由，返回是否真的改动了行。
     */
    private boolean updateWithStatusGuard(String sql, long postId, long reviewerId,
                                          String rejectReason) {
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            if (rejectReason != null) {
                stmt.setString(index++, rejectReason);
            }
            stmt.setLong(index++, reviewerId);
            stmt.setLong(index, postId);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("更新帖子审核状态失败", e);
        }
    }

    /**
     * 待审核数量，后台首页用来显示红点。
     */
    public int countPending() {
        String sql = "SELECT COUNT(*) FROM post WHERE status = 'PENDING'";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (Exception e) {
            throw new RuntimeException("统计待审核数量失败", e);
        }
    }

    private Xhs_Post toPost(ResultSet rs) throws SQLException {
        Xhs_Post post = new Xhs_Post();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setStatus(rs.getString("status"));
        post.setRejectReason(rs.getString("reject_reason"));
        post.setReviewerId(rs.getLong("reviewer_id"));
        post.setReviewedAt(rs.getTimestamp("reviewed_at"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        return post;
    }
}
