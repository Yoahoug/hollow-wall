<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${post.title} - Hollow"/>
<%@ include file="/jsp/header.jsp" %>
<link rel="stylesheet" href="${ctx}/css/browse.css">

<%--
    帖子详情页。数据由 PostServlet 放进 request。
    所有用户输入的内容都用 c:out 输出，页面会自动转义。
--%>

<%-- 一次性提示：点赞 / 取消点赞的结果（由 LikeServlet 放进 session） --%>
<c:if test="${not empty sessionScope.flashMsg}">
    <p class="msg ${sessionScope.flashType == 'error' ? 'msg-error' : 'msg-success'}">
        <c:out value="${sessionScope.flashMsg}"/>
    </p>
    <c:remove var="flashMsg" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<article class="post-detail">
    <h1><c:out value="${post.title}"/></h1>

    <div class="post-meta">
        <span><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
        <span>评论 ${post.commentCount}</span>
        <%-- 点赞：登录才能点，已赞过显示"已赞 N"，再点取消 --%>
        <c:if test="${post.status == 'PUBLISHED'}">
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
        </c:if>
        <c:if test="${post.status != 'PUBLISHED'}">
            <span>赞 ${post.likeCount}</span>
        </c:if>
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

<%@ include file="/jsp/footer.jsp" %>
