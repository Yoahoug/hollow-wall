<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${post.title} - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>
<link rel="stylesheet" href="${ctx}/css/Yuj_Browse.css">

<%--
    帖子详情页。数据由 Yuj_PostServlet 放进 request。
    所有用户输入的内容都用 c:out 输出，页面会自动转义。
--%>

<article class="post-detail">
    <h1><c:out value="${post.title}"/></h1>

    <div class="post-meta">
        <span><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
        <span>赞 ${post.likeCount}</span>
        <span>评论 ${post.commentCount}</span>
        <span class="spacer"></span>
        <a href="${ctx}/share?id=${post.id}">分享</a>
    </div>

    <c:if test="${post.status == 'PENDING'}">
        <div class="msg">该帖子正在等待审核，仅作者与管理员可见。</div>
    </c:if>
    <c:if test="${post.status == 'REJECTED'}">
        <div class="msg msg-error">
            审核未通过<c:if test="${not empty post.rejectReason}">：<c:out value="${post.rejectReason}"/></c:if>
        </div>
    </c:if>
    <c:if test="${post.status == 'HIDDEN'}">
        <div class="msg">该帖子已被隐藏，仅作者与管理员可见。</div>
    </c:if>

    <div class="post-body"><c:out value="${post.content}"/></div>
</article>

<section class="comments" id="comments">
    <h2>评论（${post.commentCount}）</h2>

    <c:if test="${not empty error}">
        <div class="msg msg-error"><c:out value="${error}"/></div>
    </c:if>

    <c:if test="${post.status == 'PUBLISHED'}">
        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <form class="comment-form" action="${ctx}/app/comment" method="post">
                    <input type="hidden" name="postId" value="${post.id}">
                    <textarea name="content" rows="3" maxlength="500" placeholder="写下你的评论……"></textarea>
                    <button type="submit" class="btn">发表评论</button>
                </form>
            </c:when>
            <c:otherwise>
                <p class="muted"><a href="${ctx}/login">登录</a>后可以发表评论。</p>
            </c:otherwise>
        </c:choose>
    </c:if>

    <c:choose>
        <c:when test="${empty comments}">
            <p class="empty-tip">还没有评论。</p>
        </c:when>
        <c:otherwise>
            <ul class="comment-list">
                <c:forEach var="comment" items="${comments}">
                    <li class="comment-item">
                        <div class="comment-meta">
                            <fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </div>
                        <p class="comment-content"><c:out value="${comment.content}"/></p>
                    </li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
