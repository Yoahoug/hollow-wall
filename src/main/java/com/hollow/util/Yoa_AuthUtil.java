package com.hollow.util;

import com.hollow.bean.Yoa_User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 登录状态工具类。
 *
 * 登录成功后把 Yoa_User 对象放进 Session，之后全项目都用这里取。
 * Session 的键名只在这里定义一次，避免各处乱起名字导致取不到。
 */
public class Yoa_AuthUtil {

    /** Session 中保存当前登录用户的键名。页面里用 ${sessionScope.loginUser} 取 */
    public static final String SESSION_USER = "loginUser";

    /**
     * 取当前登录的用户。
     * 没登录返回 null。
     */
    public static Yoa_User currentUser(HttpServletRequest request) {
        // false 表示没有 Session 就返回 null，不要新建
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Yoa_User user = (Yoa_User) session.getAttribute(SESSION_USER);
        return user;
    }

    /**
     * 是否已经登录。
     */
    public static boolean isLogin(HttpServletRequest request) {
        Yoa_User user = currentUser(request);
        if (user == null) {
            return false;
        }
        return true;
    }

    /**
     * 是否是管理员。
     * 没登录当然也不是管理员。
     */
    public static boolean isAdmin(HttpServletRequest request) {
        Yoa_User user = currentUser(request);
        if (user == null) {
            return false;
        }
        return user.isAdmin();
    }
}
