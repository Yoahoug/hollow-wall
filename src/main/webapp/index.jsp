<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
    站点根路径 /hollow/ 的入口页。
    这里只做一次转发，真正的首页内容在 /home。
    不直接把 Servlet 映射到 "/" ，否则静态资源（css、图片）会全部失效。
--%>
<jsp:forward page="/home"/>
