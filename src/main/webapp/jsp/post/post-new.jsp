<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="发帖 - Hollow"/>
<%@ include file="/jsp/header.jsp" %>

<%--
    发帖页。负责人：xhaosen1
    路由 /app/post/new 已经过登录过滤器，未登录到不了这里。
    表单提交到 POST /app/post/save，新帖进入待审核状态。
--%>
<style>
    .post-form {
        width: 640px;
        max-width: 100%;
        margin: 20px auto;
        padding: 28px 30px;
        background: #fff;
        border: 1px solid #e5e6e8;
        border-radius: 4px;
    }
    .post-form h2 {
        margin: 0 0 20px;
        font-size: 18px;
        font-weight: normal;
        text-align: center;
    }
    .form-tip {
        margin: 4px 0 0;
        color: #999;
        font-size: 12px;
    }
</style>

<div class="post-form">
    <h2>发布留言</h2>

    <c:if test="${not empty error}">
        <p class="msg msg-error"><c:out value="${error}"/></p>
    </c:if>

    <form action="${ctx}/app/post/save" method="post">
        <div class="form-item">
            <label for="title">标题</label>
            <input type="text" id="title" name="title" maxlength="100"
                   value="<c:out value='${title}'/>" placeholder="一句话说清你想说的话">
            <p class="form-tip">最多 100 字</p>
        </div>

        <div class="form-item">
            <label for="content">正文</label>
            <textarea id="content" name="content" rows="10" maxlength="10000"
                      placeholder="想对大家说什么…"><c:out value='${content}'/></textarea>
            <p class="form-tip">发布后进入待审核，管理员通过后才会展示在首页</p>
        </div>

        <button type="submit" class="btn btn-primary btn-block">发布</button>
    </form>
</div>

<%@ include file="/jsp/footer.jsp" %>
