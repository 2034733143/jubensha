<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="登录"/>
<%@ include file="common/header.jsp" %>

<div class="auth-wrap">
    <div class="panel">
        <h3>登录谜局</h3>
        <c:if test="${not empty error}">
            <div class="flash flash-error" style="margin:0 0 16px"><c:out value="${error}"/></div>
        </c:if>
        <form method="post" action="${ctx}/login">
            <input type="hidden" name="redirect" value="${redirect}">
            <div class="field" style="margin-bottom:14px">
                <label>用户名</label>
                <input type="text" name="username" value="${username}" required autofocus>
            </div>
            <div class="field" style="margin-bottom:18px">
                <label>密码</label>
                <input type="password" name="password" required>
            </div>
            <button class="btn btn-primary" type="submit" style="width:100%">登录</button>
        </form>
        <div class="auth-hint">
            还没有账号？<a href="${ctx}/register">立即注册</a><br>
            演示账号：普通用户 xiaoming / 123456 &nbsp;·&nbsp; 管理员 admin / admin123
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
