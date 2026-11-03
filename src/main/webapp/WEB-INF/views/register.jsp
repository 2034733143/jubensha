<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="注册"/>
<%@ include file="common/header.jsp" %>

<div class="auth-wrap">
    <div class="panel">
        <h3>注册账号</h3>
        <c:if test="${not empty error}">
            <div class="flash flash-error" style="margin:0 0 16px"><c:out value="${error}"/></div>
        </c:if>
        <form method="post" action="${ctx}/register">
            <div class="field" style="margin-bottom:14px">
                <label>用户名（3-20 位字母/数字/下划线）</label>
                <input type="text" name="username" value="${username}" required maxlength="20">
            </div>
            <div class="field" style="margin-bottom:14px">
                <label>密码（至少 6 位）</label>
                <input type="password" name="password" required minlength="6">
            </div>
            <div class="field" style="margin-bottom:14px">
                <label>昵称</label>
                <input type="text" name="realName" value="${realName}" maxlength="50" placeholder="选填">
            </div>
            <div class="field" style="margin-bottom:18px">
                <label>手机号</label>
                <input type="text" name="phone" value="${phone}" maxlength="11" placeholder="选填，用于门店联系">
            </div>
            <button class="btn btn-primary" type="submit" style="width:100%">注册并登录</button>
        </form>
        <div class="auth-hint">已有账号？<a href="${ctx}/login">直接登录</a></div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
