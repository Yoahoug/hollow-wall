package com.hollow.servlet;

import com.hollow.bean.Yoa_User;
import com.hollow.dao.Ewc_AdminUserDao;
import com.hollow.util.Yoa_AuthUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 管理员后台：用户管理
 * GET  /admin/user       显示用户列表页
 * POST /admin/user       处理停用/启用/删除操作
 */
@WebServlet("/admin/user")
public class Ewc_UserServlet extends HttpServlet {

    private final Ewc_AdminUserDao dao = new Ewc_AdminUserDao();

    // 显示用户列表页
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Yoa_User> users = dao.findAll();
        request.setAttribute("users", users);
        request.getRequestDispatcher("/jsp/admin/Ewc_User.jsp").forward(request, response);
    }

    // 处理操作（停用/启用/删除）
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        long userId;
        try {
            userId = Long.parseLong(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "userId 不合法");
            return;
        }

        // 不允许对自己停用或删除：只有一个管理员时，会把后台彻底锁死
        boolean destructive = "disable".equals(action) || "delete".equals(action);
        Yoa_User currentUser = Yoa_AuthUtil.currentUser(request);
        if (destructive && currentUser != null && currentUser.getId() == userId) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "不能停用或删除当前登录的账号");
            return;
        }

        if ("disable".equals(action)) {
            dao.updateStatus(userId, "DISABLED");
        } else if ("enable".equals(action)) {
            dao.updateStatus(userId, "ACTIVE");
        } else if ("delete".equals(action)) {
            dao.delete(userId);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "action 不合法");
            return;
        }

        // 操作完跳回用户列表页
        response.sendRedirect(request.getContextPath() + "/admin/user");
    }
}
