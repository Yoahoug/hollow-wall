package com.hollow.bean;

/**
 * 用户实体类，对应 sys_user 表的一行。
 * 只放属性和 get/set 方法，不做别的处理。
 */
public class Yoa_User {

    private long id;
    private String email;
    private String passwordHash;
    private String nickname;
    private String role;    // USER 普通用户 / ADMIN 管理员
    private String status;  // ACTIVE 正常 / DISABLED 已停用

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 页面上判断是不是管理员，省得每次都写 "ADMIN".equals(...)
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
