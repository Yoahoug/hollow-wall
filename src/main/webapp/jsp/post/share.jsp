<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${post.title} - 分享"/>
<%@ include file="/jsp/header.jsp" %>
<link rel="stylesheet" href="${ctx}/css/browse.css">

<%--
    分享页：只展示正文和分享链接，数据由 ShareServlet 放进 request。
--%>

<c:set var="shareUrl" value="${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${ctx}/post?id=${post.id}"/>

<article class="post-detail">
    <h1><c:out value="${post.title}"/></h1>
    <div class="post-meta">
        <span><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
    </div>
    <div class="post-body"><c:out value="${post.content}"/></div>
</article>

<div class="share-box">
    <h2>分享这条留言</h2>
    <p class="muted">复制下面的链接发给同学：</p>
    <div class="share-url">
        <input type="text" readonly value="${fn:escapeXml(shareUrl)}" onclick="this.select()">
        <a class="btn" href="${ctx}/post?id=${post.id}">查看详情</a>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
