<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="后台首页 - Hollow"/>
<%@ include file="/jsp/header.jsp" %>

<div class="container" style="padding: 20px;">
    <h2>后台管理首页</h2>
    <p style="color:#888; margin-bottom: 30px;">欢迎使用 Hollow 校园匿名留言墙管理后台</p>

    <div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 30px;">
        <div style="background: #f0f7ff; padding: 20px; border-radius: 8px; text-align: center;">
            <div style="font-size: 32px; font-weight: bold; color: #1890ff;">`</div>
            <div style="color: #666; margin-top: 8px;">总用户数</div>
        </div>
        <div style="background: #f6ffed; padding: 20px; border-radius: 8px; text-align: center;">
            <div style="font-size: 32px; font-weight: bold; color: #52c41a;">`</div>
            <div style="color: #666; margin-top: 8px;">总帖子数</div>
        </div>
        <div style="background: #fff7e6; padding: 20px; border-radius: 8px; text-align: center;">
            <div style="font-size: 32px; font-weight: bold; color: #fa8c16;">`</div>
            <div style="color: #666; margin-top: 8px;">总点赞数</div>
        </div>
        <div style="background: #fff1f0; padding: 20px; border-radius: 8px; text-align: center;">
            <div style="font-size: 32px; font-weight: bold; color: #f5222d;">`</div>
            <div style="color: #666; margin-top: 8px;">总评论数</div>
        </div>
    </div>

    <div style="background: #fafafa; padding: 20px; border-radius: 8px;">
        <h3>快捷操作</h3>
        <ul style="margin-top: 15px; line-height: 2;">
            <li><a href="/hollow/admin/user" style="color: #1890ff;">用户管理</a> - 查看、停用、删除用户</li>
            <li><a href="/hollow/admin/review" style="color: #1890ff;">帖子审核</a> - 审核待发布的帖子</li>
            <li><a href="/hollow/" style="color: #1890ff;">返回首页</a> - 查看前台页面</li>
        </ul>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
