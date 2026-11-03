<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="无权限"/>
<%@ include file="common/header.jsp" %>

<div class="panel empty" style="margin-top:40px">
    <h2 style="color:var(--danger);letter-spacing:2px">403 · 无访问权限</h2>
    <p>后台管理仅限管理员账号访问，请使用管理员账号登录。</p>
    <p><a class="btn btn-primary btn-sm" href="${ctx}/login">切换账号</a>
        <a class="btn btn-ghost btn-sm" href="${ctx}/home">返回首页</a></p>
</div>

<%@ include file="common/footer.jsp" %>
