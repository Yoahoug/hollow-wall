<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="我的 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<%--
    我的页面占位页。
    负责人：xhaosen1
    路由 /app/my 已经过登录过滤器，未登录到不了这里。
--%>
<div class="placeholder">
    <h2>我的</h2>
    <p>这里将来显示个人信息、我的帖子和各自的审核状态。</p>
    <p class="muted">负责人：xhaosen1</p>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
