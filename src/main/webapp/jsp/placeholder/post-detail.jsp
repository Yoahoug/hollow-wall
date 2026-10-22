
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${post.title}"/> - 空心校园留言墙</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .detail-container {
            max-width: 850px;
            margin: 40px auto;
            padding: 24px;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
        }

        .detail-title {
            font-size: 28px;
            margin-bottom: 16px;
            color: #263238;
            overflow-wrap: anywhere;
        }

        .detail-meta {
            color: #78909c;
            font-size: 14px;
            padding-bottom: 18px;
            border-bottom: 1px solid #eceff1;
        }

        .detail-content {
            margin-top: 24px;
            line-height: 1.9;
            font-size: 16px;
            color: #37474f;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }

        .detail-actions {
            margin-top: 28px;
        }

        .detail-actions a {
            color: #1976d2;
            text-decoration: none;
        }

        .detail-actions a:hover {
            text-decoration: underline;
        }


        .comment-section {
            margin-top: 36px;
            padding-top: 24px;
            border-top: 1px solid #eceff1;
        }

        .comment-section h2 {
            font-size: 21px;
            color: #263238;
            margin-bottom: 20px;
        }

        .comment-empty {
            color: #78909c;
            padding: 16px 0;
        }

        .comment-item {
            padding: 16px 0;
            border-bottom: 1px solid #eceff1;
        }

        .comment-meta {
            display: flex;
            justify-content: space-between;
            flex-wrap: wrap;
            gap: 8px;
            color: #78909c;
            font-size: 13px;
        }

        .comment-content {
            margin-top: 10px;
            color: #37474f;
            line-height: 1.8;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }


        .comment-form {
            margin-top: 28px;
        }

        .comment-form h3 {
            font-size: 18px;
            margin-bottom: 14px;
            color: #263238;
        }

        .comment-form textarea {
            display: block;
            box-sizing: border-box;
            width: 100%;
            padding: 12px;
            border: 1px solid #cfd8dc;
            border-radius: 8px;
            resize: vertical;
            font: inherit;
            line-height: 1.6;
        }

        .comment-form textarea:focus {
            outline: none;
            border-color: #1976d2;
        }

        .comment-form button {
            margin-top: 12px;
            padding: 10px 22px;
            border: none;
            border-radius: 6px;
            background: #1976d2;
            color: white;
            cursor: pointer;
        }

        .comment-form button:hover {
            background: #1565c0;
        }

        .comment-login-tip {
            color: #78909c;
        }


        .detail-actions {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        .detail-actions .share-button,
        .detail-actions .back-button {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            padding: 10px 18px;
            border-radius: 6px;
            text-decoration: none;
        }

        .detail-actions .share-button {
            background: #1976d2;
            color: #fff;
        }

        .detail-actions .share-button:hover {
            background: #1565c0;
            text-decoration: none;
        }

        .detail-actions .back-button {
            border: 1px solid #cfd8dc;
            color: #1976d2;
        }

        .detail-actions .back-button:hover {
            background: #f5f9fc;
            text-decoration: none;
        }

    </style>
</head>
<body>
<div class="detail-container">
    <h1 class="detail-title">
        <c:out value="${post.title}"/>
    </h1>

    <div class="detail-meta">
        作者：<c:out value="${post.nickname}" default="匿名用户"/>
        &nbsp;|&nbsp;
        发布时间：<c:out value="${post.createdAt}"/>
        &nbsp;|&nbsp;
        点赞：<c:out value="${post.likeCount}"/>
        &nbsp;|&nbsp;
        评论：<c:out value="${post.commentCount}"/>
    </div>

    <div class="detail-content"><c:out value="${post.content}"/></div>



    <div class="comment-section">
        <h2>评论区（${comments.size()}）</h2>

        <c:choose>
            <c:when test="${empty comments}">
                <p class="comment-empty">还没有评论，来发表第一条评论吧！</p>
            </c:when>

            <c:otherwise>
                <c:forEach var="comment" items="${comments}">
                    <div class="comment-item">
                        <div class="comment-meta">
                        <span>
                            <c:out value="${comment.nickname}"
                                   default="匿名用户"/>
                        </span>
                            <span>
                            <c:out value="${comment.createdAt}"/>
                        </span>
                        </div>

                        <div class="comment-content"><c:out value="${comment.content}"/></div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <div class="comment-form">
            <h3>发表评论</h3>

            <c:choose>
                <c:when test="${not empty sessionScope.loginUser}">
                    <form action="${pageContext.request.contextPath}/app/comment"
                          method="post">
                        <input type="hidden" name="postId"
                               value="${post.id}">

                        <textarea name="content"
                                  rows="4"
                                  maxlength="500"
                                  placeholder="写下你的想法，最多500个字符……"
                                  required></textarea>

                        <button type="submit">提交评论</button>
                    </form>
                </c:when>

                <c:otherwise>
                    <p class="comment-login-tip">
                        请先 <a href="${pageContext.request.contextPath}/login">登录</a>，
                        再发表评论。
                    </p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>



    <div class="detail-actions">
        <a href="${pageContext.request.contextPath}/share?id=${post.id}"
           class="share-button">
            <span aria-hidden="true">↗</span>
            分享帖子
        </a>

        <a href="${pageContext.request.contextPath}/home"
           class="back-button">
            ← 返回首页
        </a>
    </div>


</div>
</body>
</html>
