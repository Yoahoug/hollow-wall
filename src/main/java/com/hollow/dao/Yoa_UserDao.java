package com.hollow.dao;

import com.hollow.bean.Yoa_User;
import com.hollow.util.Yoa_DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 用户表 sys_user 的数据访问类。
 * 按分工约定，sys_user 的读写只由负责人负责，所以只有这一个类操作它。
 */
public class Yoa_UserDao {

    /**
     * 按邮箱查用户，登录和注册查重都用它。查不到返回 null。
     */
    public Yoa_User findByEmail(String email) {
        String sql = "SELECT id, email, password_hash, nickname, role, status "
                   + "FROM sys_user WHERE email = ?";

        // try-with-resources：连接和 PreparedStatement 用完自动关闭
        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // 有数据，把这一行转成 Yoa_User 对象返回
                Yoa_User user = toUser(rs);
                return user;
            } else {
                // 没有这个邮箱
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException("按邮箱查询用户失败", e);
        }
    }

    /**
     * 新增一个用户，注册时用。返回是否插入成功。
     */
    public boolean insert(Yoa_User user) {
        String sql = "INSERT INTO sys_user (email, password_hash, nickname, role, status) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Yoa_DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 依次填上 5 个问号
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getNickname());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getStatus());

            // 影响行数大于 0 说明插入成功
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            throw new RuntimeException("新增用户失败", e);
        }
    }

    /**
     * 把结果集当前行转成 Yoa_User 对象。
     */
    private Yoa_User toUser(ResultSet rs) throws Exception {
        Yoa_User user = new Yoa_User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setNickname(rs.getString("nickname"));
        user.setRole(rs.getString("role"));
        user.setStatus(rs.getString("status"));
        return user;
    }
}
