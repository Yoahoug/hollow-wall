<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="审核 - Hollow"/>
<%@ include file="/jsp/Yoa_Header.jsp" %>

<%--
    审核页占位页。
    负责人：ewclpy
    路由 /admin/review 已经过管理员过滤器，普通用户到不了这里。
--%>
<div class="placeholder">
    <h2>审核后台</h2>
    <p>这里将来是待审队列、通过、驳回和隐藏操作。</p>
    <p class="muted">负责人：ewclpy</p>
</div>

<%@ include file="/jsp/Yoa_Footer.jsp" %>
