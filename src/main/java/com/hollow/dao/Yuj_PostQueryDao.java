package com.hollow.dao;

import com.hollow.bean.Yuj_PostView;
import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览侧的帖子查询。
 *
 * 按分工约定，post 表的查询只归浏览侧，这里只出现 SELECT，不写任何
 * INSERT / UPDATE / DELETE，写入和审核分别由创作侧、治理侧负责。
 */
public class Yuj_PostQueryDao {

    /**
     * 评论数从 post_comment 实时统计，不依赖 post.comment_count 列，
     * 因为浏览侧没有更新 post 表的权限。
     */
    private static final String BASE_SELECT =
            "SELECT p.id, p.user_id, p.title, p.content, p.status, p.reject_reason, "
          + "p.like_count, p.created_at, "
          + "(SELECT COUNT(*) FROM post_comment c WHERE c.post_id = p.id) AS comment_count "
          + "FROM post p ";

    /**
     * 统计已发布帖子总数，搜索时只统计标题或正文包含关键词的。
     */
    public int countPublished(String keyword) {
        boolean hasKeyword = keyword != null && !keyword.isEmpty();

        String sql = "SELECT COUNT(*) FROM post "
                   + "WHERE status = 'PUBLISHED' "
                   + (hasKeyword ? "AND (title LIKE ? OR content LIKE ?)" : "");

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (hasKeyword) {
                String pattern = likePattern(keyword);
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
            }

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (Exception e) {
            throw new RuntimeException("统计帖子数量失败", e);
        }
    }

    /**
     * 分页查询已发布帖子，按发布时间从新到旧。
     */
    public List<Yuj_PostView> findPublished(String keyword, int offset, int limit) {
        boolean hasKeyword = keyword != null && !keyword.isEmpty();

        String sql = BASE_SELECT
                   + "WHERE p.status = 'PUBLISHED' "
                   + (hasKeyword ? "AND (p.title LIKE ? OR p.content LIKE ?) " : "")
                   + "ORDER BY p.created_at DESC, p.id DESC LIMIT ? OFFSET ?";

        List<Yuj_PostView> posts = new ArrayList<>();

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            if (hasKeyword) {
                String pattern = likePattern(keyword);
                stmt.setString(index++, pattern);
                stmt.setString(index++, pattern);
            }
            stmt.setInt(index++, limit);
            stmt.setInt(index, offset);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(toPostView(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException("查询帖子列表失败", e);
        }

        return posts;
    }

    /**
     * 按 id 查询帖子，任何状态都返回，是否允许查看由 Service 判断。
     */
    public Yuj_PostView findById(long id) {
        String sql = BASE_SELECT + "WHERE p.id = ?";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return toPostView(rs);
            }
            return null;

        } catch (Exception e) {
            throw new RuntimeException("按 id 查询帖子失败", e);
        }
    }

    /**
     * 把关键词中的 LIKE 通配符转义掉，避免用户输入 % 或 _ 时匹配到全部帖子。
     */
    private String likePattern(String keyword) {
        String escaped = keyword.replace("\\", "\\\\")
                                .replace("%", "\\%")
                                .replace("_", "\\_");
        return "%" + escaped + "%";
    }

    private Yuj_PostView toPostView(ResultSet rs) throws Exception {
        Yuj_PostView post = new Yuj_PostView();
        post.setId(rs.getLong("id"));
        post.setUserId(rs.getLong("user_id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setStatus(rs.getString("status"));
        post.setRejectReason(rs.getString("reject_reason"));
        post.setLikeCount(rs.getInt("like_count"));
        post.setCommentCount(rs.getInt("comment_count"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        return post;
    }
}
