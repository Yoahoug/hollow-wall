package com.hollow.service;

import com.hollow.bean.Yoa_User;
import com.hollow.dao.Yoa_UserDao;
import com.hollow.util.Yoa_PasswordUtil;

/**
 * 用户相关的业务逻辑：注册、登录。
 * Servlet 只负责取参数和跳页面，判断规则写在这里。
 */
public class Yoa_UserService {

    private Yoa_UserDao userDao = new Yoa_UserDao();

    /**
     * 注册。
     * 成功返回 null，失败返回一句提示文字，Servlet 直接显示给用户。
     */
    public String register(String email, String password, String nickname) {
        // 1. 先查邮箱有没有被用过
        Yoa_User oldUser = userDao.findByEmail(email);
        if (oldUser != null) {
            return "这个邮箱已经注册过了";
        }

        // 2. 组装一个用户对象，密码加密后再存
        Yoa_User user = new Yoa_User();
        user.setEmail(email);
        user.setPasswordHash(Yoa_PasswordUtil.hash(password));
        user.setNickname(nickname);
        // 新注册的一律是普通用户，不是管理员
        user.setRole("USER");
        user.setStatus("ACTIVE");

        // 3. 存进数据库
        boolean success = userDao.insert(user);
        if (!success) {
            return "注册失败，请稍后再试";
        }
        return null;
    }

    /**
     * 登录。
     * 成功返回查到的用户对象，失败返回 null。
     */
    public Yoa_User login(String email, String password) {
        // 1. 按邮箱把用户查出来
        Yoa_User user = userDao.findByEmail(email);
        if (user == null) {
            return null;
        }

        // 2. 比对密码。数据库里存的是哈希，用 check 校验明文
        boolean passwordRight = Yoa_PasswordUtil.check(password, user.getPasswordHash());
        if (!passwordRight) {
            return null;
        }

        // 3. 被停用的账号不能登录
        boolean active = "ACTIVE".equals(user.getStatus());
        if (!active) {
            return null;
        }

        return user;
    }
}
