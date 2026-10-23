<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="审核 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<%--
    审核后台。负责人：ewclpy
    路由 /admin/review 已经过管理员过滤器，普通用户到不了这里。
    待审核队列按提交时间从早到晚，已发布列表仅供查看。
--%>
<style>
    .review-head {
        display: flex;
        align-items: baseline;
        gap: 12px;
        margin-bottom: 16px;
    }
    .review-head h2 {
        margin: 0;
        font-size: 18px;
        font-weight: normal;
    }
    .review-block {
        margin-bottom: 30px;
    }
    .review-block h3 {
        margin: 0 0 12px;
        font-size: 15px;
        font-weight: normal;
        color: #555;
    }
    .review-item {
        padding: 16px 20px;
        margin-bottom: 14px;
        background: #fff;
        border: 1px solid #e5e6e8;
        border-radius: 4px;
    }
    .review-item .item-head {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 8px;
    }
    .review-item .item-title {
        font-size: 15px;
        font-weight: bold;
        color: #2f3b52;
        margin-right: auto;
    }
    .review-item .item-time {
        color: #999;
        font-size: 12px;
    }
    .review-item .item-content {
        margin: 0 0 12px;
        color: #444;
        white-space: pre-wrap;
        word-break: break-word;
    }
    .review-item .item-actions {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
    }
    .review-item .item-actions form {
        margin: 0;
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
    .review-item input[type="text"] {
        width: 240px;
        padding: 5px 8px;
        border: 1px solid #ccc;
        border-radius: 3px;
        font-size: 13px;
        font-family: inherit;
    }
    .review-item input[type="text"]:focus {
        outline: none;
        border-color: #3a6ea5;
    }
    .btn-sm {
        padding: 4px 12px;
        font-size: 13px;
    }
    .btn-approve {
        background: #2f7a3f;
    }
    .btn-approve:hover {
        background: #276734;
    }
    .btn-reject {
        background: #b93a34;
    }
    .btn-reject:hover {
        background: #a1302b;
    }
</style>

<div class="review-head">
    <h2>审核后台</h2>
    <span class="muted">待审核 ${pendingCount} 条</span>
</div>

<c:if test="${not empty error}">
    <p class="msg msg-error"><c:out value="${error}"/></p>
</c:if>

<%-- 待审核队列 --%>
<div class="review-block">
    <h3>待审核</h3>

    <c:choose>
        <c:when test="${empty pendingPosts}">
            <p class="empty-tip">没有待审核的帖子。</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="post" items="${pendingPosts}">
                <div class="review-item">
                    <div class="item-head">
                        <span class="item-title"><c:out value="${post.title}"/></span>
                        <span class="item-time">
                            <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </span>
                    </div>

                    <p class="item-content"><c:out value="${post.content}"/></p>

                    <div class="item-actions">
                        <%-- 通过 --%>
                        <form action="${ctx}/admin/review" method="post"
                              onsubmit="return confirm('确定通过这条帖子吗？通过后会显示在首页。')">
                            <input type="hidden" name="postId" value="${post.id}">
                            <input type="hidden" name="action" value="approve">
                            <button type="submit" class="btn btn-sm btn-approve">通过</button>
                        </form>

                        <%-- 驳回，理由必填 --%>
                        <form action="${ctx}/admin/review" method="post">
                            <input type="hidden" name="postId" value="${post.id}">
                            <input type="hidden" name="action" value="reject">
                            <input type="text" name="rejectReason" maxlength="200"
                                   placeholder="驳回理由（必填，会展示给作者）" required>
                            <button type="submit" class="btn btn-sm btn-reject">驳回</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<%-- 已发布列表，仅供查看 --%>
<div class="review-block">
    <h3>已发布</h3>

    <c:choose>
        <c:when test="${empty publishedPosts}">
            <p class="empty-tip">还没有已发布的帖子。</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="post" items="${publishedPosts}">
                <div class="review-item">
                    <div class="item-head">
                        <span class="item-title"><c:out value="${post.title}"/></span>
                        <span class="item-time">
                            <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </span>
                    </div>

                    <p class="item-content"><c:out value="${post.content}"/></p>

                    <div class="item-actions">
                        <span class="muted">赞 ${post.likeCount} · 评论 ${post.commentCount}</span>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
