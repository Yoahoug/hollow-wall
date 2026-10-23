<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="用户管理 - Hollow"/>
<%@ include file="/jsp/header.jsp" %>

<div class="container" style="padding: 20px;">
    <h2>用户管理</h2>
    <p class="muted" style="color:#888; margin-bottom: 20px;">
        管理员可以停用、启用或删除用户。
    </p>

    <table border="1" cellpadding="10" cellspacing="0" style="width: 100%; border-collapse: collapse;">
        <tr style="background: #f5f5f5;">
            <th>ID</th>
            <th>邮箱</th>
            <th>昵称</th>
            <th>角色</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${users}" var="u">
        <tr>
            <td>${u.id}</td>
            <td><c:out value="${u.email}"/></td>
            <td><c:out value="${u.nickname}"/></td>
            <td>${u.role}</td>
            <td>
                <c:if test="${u.status == 'ACTIVE'}">
                    <span style="color: green;">正常</span>
                </c:if>
                <c:if test="${u.status == 'DISABLED'}">
                    <span style="color: red;">已停用</span>
                </c:if>
            </td>
            <td>
                <form method="post" action="/hollow/admin/user" style="display: inline; margin: 0 5px;">
                    <input type="hidden" name="userId" value="${u.id}">
                    <c:if test="${u.status == 'ACTIVE'}">
                        <button type="submit" name="action" value="disable">停用</button>
                    </c:if>
                    <c:if test="${u.status == 'DISABLED'}">
                        <button type="submit" name="action" value="enable">启用</button>
                    </c:if>
                    <button type="submit" name="action" value="delete"
                            onclick="return confirm('确定删除这个用户吗？他的帖子、评论、点赞都会一起删掉。')">
                        删除
                    </button>
                </form>
            </td>
        </tr>
        </c:forEach>
    </table>
</div>

<%@ include file="/jsp/footer.jsp" %>
