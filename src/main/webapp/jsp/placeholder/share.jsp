
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>分享帖子 - 空心校园留言墙</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">

    <style>
        .share-container {
            max-width: 760px;
            margin: 40px auto;
            padding: 28px;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06);
        }

        .share-container h1 {
            color: #263238;
            font-size: 26px;
            overflow-wrap: anywhere;
        }

        .share-meta {
            margin: 16px 0;
            padding-bottom: 16px;
            border-bottom: 1px solid #eceff1;
            color: #78909c;
            font-size: 14px;
        }

        .share-content {
            margin: 24px 0;
            color: #37474f;
            line-height: 1.9;
            white-space: pre-wrap;
            overflow-wrap: anywhere;
        }

        .share-link {
            display: flex;
            gap: 10px;
            margin-top: 24px;
        }

        .share-link input {
            flex: 1;
            min-width: 0;
            padding: 10px;
            border: 1px solid #cfd8dc;
            border-radius: 6px;
        }

        .share-link button {
            padding: 10px 18px;
            border: none;
            border-radius: 6px;
            background: #1976d2;
            color: white;
            cursor: pointer;
        }

        .share-link button:hover {
            background: #1565c0;
        }

        .share-actions {
            margin-top: 24px;
        }

        .share-actions a {
            color: #1976d2;
            text-decoration: none;
        }

        #copyMessage {
            margin-top: 10px;
            color: #2e7d32;
        }

        @media (max-width: 600px) {
            .share-container {
                margin: 20px 12px;
                padding: 20px;
            }

            .share-link {
                flex-direction: column;
            }
        }
    </style>
</head>

<body>
<div class="share-container">

    <h1><c:out value="${post.title}"/></h1>

    <div class="share-meta">
        作者：<c:out value="${post.nickname}" default="匿名用户"/>
        &nbsp;|&nbsp;
        发布时间：<c:out value="${post.createdAt}"/>
    </div>

    <div class="share-content"><c:out value="${post.content}"/></div>

    <h3>分享这篇帖子</h3>
    <p>复制下面的链接，就可以分享给其他人。</p>

    <div class="share-link">
        <input type="text" id="shareUrl" readonly>
        <button type="button" id="copyButton">复制链接</button>
    </div>

    <p id="copyMessage" role="status"></p>

    <div class="share-actions">
        <a href="${pageContext.request.contextPath}/post?id=${post.id}">
            ← 返回帖子详情
        </a>
        &nbsp;|&nbsp;
        <a href="${pageContext.request.contextPath}/home">
            返回首页
        </a>
    </div>

</div>

<script>
    (function () {
        var contextPath = '${pageContext.request.contextPath}';
        var postId = '${post.id}';

        var shareUrl = window.location.origin
            + contextPath
            + '/share?id='
            + encodeURIComponent(postId);

        var input = document.getElementById('shareUrl');
        var button = document.getElementById('copyButton');
        var message = document.getElementById('copyMessage');

        input.value = shareUrl;

        button.addEventListener('click', function () {
            if (navigator.clipboard && window.isSecureContext) {
                navigator.clipboard.writeText(shareUrl)
                    .then(function () {
                        message.textContent = '链接已复制！';
                    })
                    .catch(function () {
                        input.focus();
                        input.select();
                        message.textContent = '请手动复制选中的链接。';
                    });
            } else {
                input.focus();
                input.select();

                try {
                    var copied = document.execCommand('copy');
                    message.textContent = copied
                        ? '链接已复制！'
                        : '请手动复制选中的链接。';
                } catch (error) {
                    message.textContent = '请手动复制选中的链接。';
                }
            }
        });
    })();
</script>

</body>
</html>
