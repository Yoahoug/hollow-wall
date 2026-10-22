
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="pageTitle" value="首页 - Hollow"/>
<%@ include file="/jsp/header.jsp" %>

<div class="home-container">

    <div class="home-heading">
        <h2>校园匿名留言墙</h2>
        <p>分享想法，发现校园里的声音。</p>
    </div>

    <!-- 关键词搜索 -->
    <form action="${pageContext.request.contextPath}/home"
          method="get" class="search-form">
        <input type="text"
               name="keyword"
               value="<c:out value='${keyword}'/>"
               placeholder="搜索帖子标题或内容">
        <button type="submit">搜索</button>

        <c:if test="${not empty keyword}">
            <a href="${pageContext.request.contextPath}/home">清除搜索</a>
        </c:if>
    </form>

    <p class="post-total">共找到 <c:out value="${totalPosts}"/> 条帖子</p>

    <!-- 帖子列表 -->
    <c:choose>
        <c:when test="${empty posts}">
            <div class="empty-posts">
                <h3>暂时没有找到帖子</h3>
                <p>试试其他关键词，或者稍后再来看看。</p>
            </div>
        </c:when>

        <c:otherwise>
            <div class="post-list">
                <c:forEach var="post" items="${posts}">
                    <article class="post-card">

                        <h3>
                            <a href="${pageContext.request.contextPath}/post?id=${post.id}">
                                <c:out value="${post.title}"/>
                            </a>
                        </h3>

                        <p class="post-content">
                            <c:out value="${post.content}"/>
                        </p>

                        <div class="post-meta">
                            <span>作者：<c:out value="${post.nickname}"/></span>
                            <span>
                                发布时间：
                                <c:out value="${post.createdAt}"/>
                            </span>
                            <span>点赞：<c:out value="${post.likeCount}"/></span>
                            <span>评论：<c:out value="${post.commentCount}"/></span>
                        </div>

                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>

    <!-- 分页 -->
    <c:if test="${totalPages > 1}">
        <nav class="pagination" aria-label="帖子分页">

            <c:if test="${currentPage > 1}">
                <a href="${pageContext.request.contextPath}/home?page=${currentPage - 1}&amp;keyword=${encodedKeyword}">
                    上一页
                </a>
            </c:if>

            <span>
                第 <c:out value="${currentPage}"/> /
                <c:out value="${totalPages}"/> 页
            </span>

            <c:if test="${currentPage < totalPages}">
                <a href="${pageContext.request.contextPath}/home?page=${currentPage + 1}&amp;keyword=${encodedKeyword}">
                    下一页
                </a>
            </c:if>

        </nav>
    </c:if>

</div>

<%@ include file="/jsp/footer.jsp" %>
