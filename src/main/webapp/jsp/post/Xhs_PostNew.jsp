<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="发帖 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<%--
    发帖页占位页。
    负责人：xhaosen1
    路由 /app/post/new 已经过登录过滤器，未登录到不了这里。
--%>
<div class="placeholder">
    <h2>发帖</h2>
    <p>这里将来是发帖表单，提交后进入待审核状态。</p>
    <p class="muted">负责人：xhaosen1</p>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
