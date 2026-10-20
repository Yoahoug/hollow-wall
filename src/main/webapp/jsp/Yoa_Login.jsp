<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="登录 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<div class="auth-box">
    <h2>登录</h2>

    <%-- 注册成功后跳过来会带上 success 提示 --%>
    <c:if test="${not empty success}">
        <p class="msg msg-success"><c:out value="${success}"/></p>
    </c:if>

    <%-- 登录失败时带着错误信息 --%>
    <c:if test="${not empty error}">
        <p class="msg msg-error"><c:out value="${error}"/></p>
    </c:if>

    <form action="${ctx}/login" method="post">
        <div class="form-item">
            <label for="email">邮箱</label>
            <%-- value 回填用户刚才填的邮箱，密码框不回填 --%>
            <input type="text" id="email" name="email" value="<c:out value='${email}'/>" placeholder="请输入邮箱">
        </div>

        <div class="form-item">
            <label for="password">密码</label>
            <input type="password" id="password" name="password" placeholder="请输入密码">
        </div>

        <button type="submit" class="btn btn-primary btn-block">登录</button>
    </form>

    <p class="auth-tip">
        还没有账号？<a href="${ctx}/register">去注册</a>
    </p>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
