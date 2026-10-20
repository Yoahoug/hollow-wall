<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="注册 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<div class="auth-box">
    <h2>注册</h2>

    <c:if test="${not empty error}">
        <p class="msg msg-error"><c:out value="${error}"/></p>
    </c:if>

    <form action="${ctx}/register" method="post">
        <div class="form-item">
            <label for="email">邮箱</label>
            <input type="text" id="email" name="email" value="<c:out value='${email}'/>" placeholder="用作登录账号">
        </div>

        <div class="form-item">
            <label for="nickname">昵称</label>
            <input type="text" id="nickname" name="nickname" value="<c:out value='${nickname}'/>" placeholder="别人看到的名字">
        </div>

        <div class="form-item">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" placeholder="至少 6 位">
        </div>

        <div class="form-item">
            <label for="confirm">确认密码</label>
            <input type="password" id="confirm" name="confirm" placeholder="再输一次">
        </div>

        <button type="submit" class="btn btn-primary btn-block">注册</button>
    </form>

    <p class="auth-tip">
        已有账号？<a href="${ctx}/login">去登录</a>
    </p>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
