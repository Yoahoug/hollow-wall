<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="我的 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<%--
    "我的"页面。负责人：xhaosen1
    路由 /app/my 已经过登录过滤器，未登录到不了这里。
    显示个人信息与自己的全部帖子（含审核状态），可删除自己的帖子、点赞。
--%>
<style>
    .my-info {
        display: flex;
        align-items: baseline;
        gap: 16px;
        padding: 16px 20px;
        margin-bottom: 20px;
        background: #fff;
        border: 1px solid #e5e6e8;
        border-radius: 4px;
    }
    .my-info .name {
        font-size: 17px;
        font-weight: bold;
        color: #2f3b52;
    }
    .post-item {
        padding: 16px 20px;
        margin-bottom: 14px;
        background: #fff;
        border: 1px solid #e5e6e8;
        border-radius: 4px;
    }
    .post-item .post-head {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    .post-item .post-title {
        font-size: 15px;
        font-weight: bold;
        color: #2f3b52;
        margin-right: auto;
    }
    .post-item .post-time {
        color: #999;
        font-size: 12px;
    }
    .post-item .post-content {
        margin: 10px 0;
        color: #444;
        white-space: pre-line;
        word-break: break-word;
    }
    .post-item .reject-reason {
        margin: 8px 0;
        padding: 8px 12px;
        background: #fdecea;
        color: #b93a34;
        border-radius: 3px;
        font-size: 13px;
    }
    .post-item .post-foot {
        display: flex;
        align-items: center;
        gap: 14px;
        color: #888;
        font-size: 13px;
    }
    .post-item .post-foot form {
        margin-left: auto;
        display: inline;
    }
    .post-item .post-foot form + form {
        margin-left: 8px;
    }
    .btn-sm {
        padding: 3px 10px;
        font-size: 12px;
        border-radius: 3px;
    }
    .btn-ghost {
        background: none;
        border: 1px solid #ccc;
        color: #555;
    }
    .btn-ghost:hover {
        background: #f2f3f5;
    }
    .btn-danger {
        background: none;
        border: 1px solid #d8a1a1;
        color: #b93a34;
    }
    .btn-danger:hover {
        background: #fdf3f2;
    }

    /* 审核状态标签 */
    .badge {
        display: inline-block;
        padding: 1px 8px;
        border-radius: 10px;
        font-size: 12px;
        white-space: nowrap;
    }
    .badge-pending   { background: #fdf3e0; color: #b27b23; }
    .badge-published { background: #eaf6ec; color: #2f7a3f; }
    .badge-rejected  { background: #fdecea; color: #b93a34; }
    .badge-hidden    { background: #eceff3; color: #7a8494; }
</style>

<%-- 一次性提示：发帖成功、删除结果、点赞重复被拦截等 --%>
<c:if test="${not empty sessionScope.flashMsg}">
    <p class="msg ${sessionScope.flashType == 'error' ? 'msg-error' : 'msg-success'}">
        <c:out value="${sessionScope.flashMsg}"/>
    </p>
    <c:remove var="flashMsg" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<%-- 个人信息 --%>
<div class="my-info">
    <span class="name"><c:out value="${sessionScope.loginUser.nickname}"/></span>
    <span class="muted"><c:out value="${sessionScope.loginUser.email}"/></span>
    <span class="muted">共 ${fn:length(posts)} 条帖子</span>
</div>

<%-- 帖子列表 --%>
<c:choose>
    <c:when test="${empty posts}">
        <div class="placeholder">
            <h2>还没有发过帖子</h2>
            <p>去<a href="${ctx}/app/post/new">发布第一条留言</a>吧</p>
        </div>
    </c:when>
    <c:otherwise>
        <c:forEach var="post" items="${posts}">
            <div class="post-item">
                <div class="post-head">
                    <span class="post-title"><c:out value="${post.title}"/></span>

                    <%-- 审核状态 --%>
                    <c:choose>
                        <c:when test="${post.status == 'PENDING'}"><span class="badge badge-pending">审核中</span></c:when>
                        <c:when test="${post.status == 'PUBLISHED'}"><span class="badge badge-published">已发布</span></c:when>
                        <c:when test="${post.status == 'REJECTED'}"><span class="badge badge-rejected">已驳回</span></c:when>
                        <c:when test="${post.status == 'HIDDEN'}"><span class="badge badge-hidden">已隐藏</span></c:when>
                    </c:choose>

                    <span class="post-time">
                        <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </span>
                </div>

                <div class="post-content"><c:out value="${post.content}"/></div>

                <%-- 驳回时展示理由 --%>
                <c:if test="${post.status == 'REJECTED' and not empty post.rejectReason}">
                    <p class="reject-reason">驳回理由：<c:out value="${post.rejectReason}"/></p>
                </c:if>

                <div class="post-foot">
                    <span>赞 ${post.likeCount}</span>
                    <span>评论 ${post.commentCount}</span>

                    <%-- 已发布的帖子可以点赞 / 取消点赞 --%>
                    <c:if test="${post.status == 'PUBLISHED'}">
                        <c:choose>
                            <c:when test="${likedIds.contains(post.id)}">
                                <form action="${ctx}/app/like" method="post">
                                    <input type="hidden" name="postId" value="${post.id}">
                                    <input type="hidden" name="action" value="cancel">
                                    <button type="submit" class="btn btn-sm btn-ghost">取消点赞</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form action="${ctx}/app/like" method="post">
                                    <input type="hidden" name="postId" value="${post.id}">
                                    <button type="submit" class="btn btn-sm btn-ghost">点赞</button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <%-- 删除自己的帖子，任何状态都可以删 --%>
                    <form action="${ctx}/app/post/delete" method="post"
                          onsubmit="return confirm('确定删除这条帖子吗？删除后不可恢复')">
                        <input type="hidden" name="postId" value="${post.id}">
                        <button type="submit" class="btn btn-sm btn-danger">删除</button>
                    </form>
                </div>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
