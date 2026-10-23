<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="首页 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>
<link rel="stylesheet" href="${ctx}/css/Yuj_Browse.css">

<%--
    首页：已发布帖子的列表、分页与关键词搜索。
    负责人：yujie8791-ai
    数据由 Yuj_HomeServlet 放进 request，路由 /home 与 / 不用改。
--%>

<div class="browse-head">
    <h1>留言墙</h1>
    <form class="search-form" action="${ctx}/home" method="get">
        <input type="text" name="q" value="${fn:escapeXml(keyword)}" placeholder="搜索标题或内容">
        <button type="submit" class="btn">搜索</button>
        <a class="btn btn-secondary" href="${ctx}/home">清除</a>
    </form>
</div>

<%-- 一次性提示：点赞 / 取消点赞的结果（由 Xhs_LikeServlet 放进 session） --%>
<c:if test="${not empty sessionScope.flashMsg}">
    <p class="msg ${sessionScope.flashType == 'error' ? 'msg-error' : 'msg-success'}">
        <c:out value="${sessionScope.flashMsg}"/>
    </p>
    <c:remove var="flashMsg" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<c:if test="${not empty keyword}">
    <p class="muted search-tip">
        搜索“<c:out value="${keyword}"/>”，共 ${postPage.total} 条结果。
        <a href="${ctx}/home">查看全部</a>
    </p>
</c:if>

<c:choose>
    <c:when test="${empty postPage.posts}">
        <p class="empty-tip">
            <c:choose>
                <c:when test="${not empty keyword}">没有找到相关留言，换个关键词试试。</c:when>
                <c:otherwise>还没有留言。登录后发第一条吧。</c:otherwise>
            </c:choose>
        </p>
    </c:when>
    <c:otherwise>
        <p class="muted">共 ${postPage.total} 条留言</p>

        <c:forEach var="post" items="${postPage.posts}">
            <article class="post-card">
                <h2>
                    <a href="${ctx}/post?id=${post.id}"><c:out value="${post.title}"/></a>
                </h2>
                <p class="post-content"><c:out value="${fn:substring(post.content, 0, 120)}"/><c:if test="${fn:length(post.content) > 120}">…</c:if></p>
                <div class="post-meta">
                    <span><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                    <span>评论 ${post.commentCount}</span>
                    <span class="spacer"></span>
                    <%-- 点赞：登录才能点，已赞过显示"已赞 N"，再点取消 --%>
                    <c:choose>
                        <c:when test="${empty sessionScope.loginUser}">
                            <span>赞 ${post.likeCount}</span>
                        </c:when>
                        <c:when test="${likedIds.contains(post.id)}">
                            <form class="like-form" action="${ctx}/app/like" method="post">
                                <input type="hidden" name="postId" value="${post.id}">
                                <input type="hidden" name="action" value="cancel">
                                <button type="submit" class="btn-like liked" title="取消点赞">已赞 ${post.likeCount}</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form class="like-form" action="${ctx}/app/like" method="post">
                                <input type="hidden" name="postId" value="${post.id}">
                                <button type="submit" class="btn-like">赞 ${post.likeCount}</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                    <a href="${ctx}/share?id=${post.id}">分享</a>
                </div>
            </article>
        </c:forEach>

        <c:if test="${postPage.totalPages > 1}">
            <div class="pager">
                <c:if test="${postPage.page > 1}">
                    <c:url var="prevUrl" value="/home">
                        <c:param name="page" value="${postPage.page - 1}"/>
                        <c:param name="q" value="${keyword}"/>
                    </c:url>
                    <a href="${prevUrl}">上一页</a>
                </c:if>

                <span>第 ${postPage.page} / ${postPage.totalPages} 页</span>

                <c:if test="${postPage.page < postPage.totalPages}">
                    <c:url var="nextUrl" value="/home">
                        <c:param name="page" value="${postPage.page + 1}"/>
                        <c:param name="q" value="${keyword}"/>
                    </c:url>
                    <a href="${nextUrl}">下一页</a>
                </c:if>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
