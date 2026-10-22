package com.hollow.dao;

import com.hollow.bean.Post;
import com.hollow.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    /**
     * 查询已发布帖子，支持关键词搜索和分页。
     * keyword 为空时查询全部已发布帖子。
     */
    public List<Post> findPublishedPosts(String keyword, int page, int pageSize) {
        List<Post> posts = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.user_id, p.title, p.content, p.status, " +
                        "p.like_count, p.comment_count, p.created_at, u.nickname " +
                        "FROM post p " +
                        "LEFT JOIN sys_user u ON p.user_id = u.id " +
                        "WHERE p.status = 'PUBLISHED' "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasKeyword) {
            sql.append("AND (p.title LIKE ? OR p.content LIKE ?) ");
        }

        sql.append("ORDER BY p.created_at DESC, p.id DESC LIMIT ? OFFSET ?");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int index = 1;

            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(index++, pattern);
                stmt.setString(index++, pattern);
            }

            int safePage = Math.max(page, 1);
            int safePageSize = Math.max(pageSize, 1);
            int offset = (safePage - 1) * safePageSize;

            stmt.setInt(index++, safePageSize);
            stmt.setInt(index, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();

                    post.setId(rs.getLong("id"));
                    post.setUserId(rs.getLong("user_id"));
                    post.setTitle(rs.getString("title"));
                    post.setContent(rs.getString("content"));
                    post.setStatus(rs.getString("status"));
                    post.setLikeCount(rs.getInt("like_count"));
                    post.setCommentCount(rs.getInt("comment_count"));
                    post.setCreatedAt(rs.getTimestamp("created_at"));
                    post.setNickname(rs.getString("nickname"));

                    posts.add(post);
                }
            }

            return posts;

        } catch (Exception e) {
            throw new RuntimeException("查询首页帖子失败", e);
        }
    }

    /**
     * 查询符合条件的已发布帖子总数，用于分页。
     */
    public int countPublishedPosts(String keyword) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM post WHERE status = 'PUBLISHED' "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasKeyword) {
            sql.append("AND (title LIKE ? OR content LIKE ?)");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            if (hasKeyword) {
                String pattern = "%" + keyword.trim() + "%";
                stmt.setString(1, pattern);
                stmt.setString(2, pattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

            return 0;

        } catch (Exception e) {
            throw new RuntimeException("统计首页帖子数量失败", e);
        }
    }
    /**
     * 根据帖子 ID 查询已发布的帖子详情。
     * 只查询 PUBLISHED 状态的帖子。
     */
    public Post findPublishedPostById(long postId) {
        String sql =
                "SELECT p.id, p.user_id, p.title, p.content, p.status, " +
                        "p.like_count, p.comment_count, p.created_at, u.nickname " +
                        "FROM post p " +
                        "LEFT JOIN sys_user u ON p.user_id = u.id " +
                        "WHERE p.id = ? AND p.status = 'PUBLISHED'";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, postId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post();

                    post.setId(rs.getLong("id"));
                    post.setUserId(rs.getLong("user_id"));
                    post.setTitle(rs.getString("title"));
                    post.setContent(rs.getString("content"));
                    post.setStatus(rs.getString("status"));
                    post.setLikeCount(rs.getInt("like_count"));
                    post.setCommentCount(rs.getInt("comment_count"));
                    post.setCreatedAt(rs.getTimestamp("created_at"));
                    post.setNickname(rs.getString("nickname"));

                    return post;
                }

                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("查询帖子详情失败", e);
        }
    }
}