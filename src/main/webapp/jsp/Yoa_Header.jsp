<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
    全站页头。页面开头 include 本文件，结尾 include Yoa_Footer.jsp。
    页面标题用 <c:set var="pageTitle" value="登录"/> 传。
--%>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${empty pageTitle ? 'Hollow 校园匿名留言墙' : pageTitle}"/></title>
    <link rel="stylesheet" href="${ctx}/css/Yoa_Style.css">
</head>
<body>

<header class="site-header">
    <div class="container">
        <a class="logo" href="${ctx}/home">Hollow</a>

        <span class="nav">
            <a href="${ctx}/home">首页</a>
            <%-- 登录后才显示发帖和我的，管理员多一个后台 --%>
            <c:if test="${not empty sessionScope.loginUser}">
                <a href="${ctx}/app/post/new">发帖</a>
                <a href="${ctx}/app/my">我的</a>
                <c:if test="${sessionScope.loginUser.admin}">
                    <a href="${ctx}/admin/review">后台</a>
                    <a href="${ctx}/admin/stats">统计</a>
                </c:if>
            </c:if>
        </span>

        <span class="user-area">
            <c:choose>
                <c:when test="${not empty sessionScope.loginUser}">
                    <c:out value="${sessionScope.loginUser.nickname}"/>
                    <form action="${ctx}/logout" method="post">
                        <button type="submit" class="link-btn">退出</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <a href="${ctx}/login">登录</a>
                    <a href="${ctx}/register">注册</a>
                </c:otherwise>
            </c:choose>
        </span>
    </div>
</header>

<main class="container main">
