package com.hollow.dao;

import com.hollow.bean.Yoa_User;
import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员后台：用户管理的数据访问类
 * 负责：查用户列表、改用户状态、删除用户
 */
public class Ewc_AdminUserDao {

    /**
     * 查所有用户，按 ID 排序
     */
    public List<Yoa_User> findAll() {
        String sql = "SELECT id, email, password_hash, nickname, role, status "
                   + "FROM sys_user ORDER BY id";

        List<Yoa_User> list = new ArrayList<>();

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Yoa_User u = new Yoa_User();
                u.setId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setNickname(rs.getString("nickname"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                list.add(u);
            }

        } catch (Exception e) {
            throw new RuntimeException("查用户列表失败", e);
        }

        return list;
    }

    /**
     * 停用/启用用户
     * @param userId 用户 ID
     * @param status  新状态：ACTIVE 正常 / DISABLED 停用
     */
    public boolean updateStatus(long userId, String status) {
        String sql = "UPDATE sys_user SET status = ? WHERE id = ?";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("修改用户状态失败", e);
        }
    }

    /**
     * 删除用户（四步在一个事务里，要么全成功，要么全回滚）
     */
    public boolean delete(long userId) {
        String[] sqls = {
            "DELETE FROM post_like    WHERE user_id = ?",
            "DELETE FROM post_comment WHERE user_id = ?",
            "DELETE FROM post         WHERE user_id = ?",
            "DELETE FROM sys_user     WHERE id = ?"
        };

        Connection conn = null;
        try {
            conn = Yoa_DBUtil.getConnection();
            conn.setAutoCommit(false);  // 开启事务

            for (String sql : sqls) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setLong(1, userId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();  // 全部成功才提交
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException("删除用户失败", e);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignored) {}
        }
    }

    // ==================== 统计 SQL 校验 ====================

    /**
     * 统计：总用户数
     */
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM sys_user";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计用户数失败", e);
        }
    }

    /**
     * 统计：总帖子数
     */
    public int countPosts() {
        String sql = "SELECT COUNT(*) FROM post";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计帖子数失败", e);
        }
    }

    /**
     * 统计：按状态统计帖子数量
     * @return 数组，[待审核数, 已发布数, 已驳回数, 已隐藏数]
     */
    public int[] countPostsByStatus() {
        String sql = "SELECT "
                   + "SUM(CASE WHEN status='PENDING' THEN 1 ELSE 0 END), "
                   + "SUM(CASE WHEN status='PUBLISHED' THEN 1 ELSE 0 END), "
                   + "SUM(CASE WHEN status='REJECTED' THEN 1 ELSE 0 END), "
                   + "SUM(CASE WHEN status='HIDDEN' THEN 1 ELSE 0 END) "
                   + "FROM post";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new int[]{
                    rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4)
                };
            }
            return new int[4];
        } catch (Exception e) {
            throw new RuntimeException("统计帖子状态失败", e);
        }
    }

    /**
     * 统计：总点赞数
     */
    public int countLikes() {
        String sql = "SELECT COUNT(*) FROM post_like";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计点赞数失败", e);
        }
    }

    /**
     * 统计：总评论数
     */
    public int countComments() {
        String sql = "SELECT COUNT(*) FROM post_comment";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计评论数失败", e);
        }
    }

    /**
     * 统计：今日新增用户数
     */
    public int countTodayNewUsers() {
        String sql = "SELECT COUNT(*) FROM sys_user WHERE DATE(created_at) = CURDATE()";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计今日新增用户失败", e);
        }
    }

    /**
     * 统计：今日新增帖子数
     */
    public int countTodayNewPosts() {
        String sql = "SELECT COUNT(*) FROM post WHERE DATE(created_at) = CURDATE()";
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("统计今日新增帖子失败", e);
        }
    }
}
