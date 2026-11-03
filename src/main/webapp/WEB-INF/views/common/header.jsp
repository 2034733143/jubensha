<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="user" value="${sessionScope.user}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} · 谜局剧本杀</title>
    <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body>
<header class="topbar">
    <div class="topbar-inner">
        <a class="brand" href="${ctx}/home">谜局<span>MURDER MYSTERY</span></a>
        <nav class="nav">
            <a href="${ctx}/home" class="${pageContext.request.servletPath == '/home' ? 'active' : ''}">首页</a>
            <a href="${ctx}/scripts" class="${pageContext.request.servletPath == '/scripts' ? 'active' : ''}">剧本库</a>
            <a href="${ctx}/sessions" class="${pageContext.request.servletPath == '/sessions' ? 'active' : ''}">场次预约</a>
            <c:if test="${not empty user}">
                <a href="${ctx}/my/bookings" class="${pageContext.request.servletPath == '/my/bookings' ? 'active' : ''}">我的预约</a>
            </c:if>
            <c:if test="${user.admin}">
                <a href="${ctx}/admin" class="${pageContext.request.servletPath == '/admin' ? 'active' : ''}">后台管理</a>
            </c:if>
        </nav>
        <div class="user-area">
            <c:choose>
                <c:when test="${not empty user}">
                    <span>${user.displayName}</span>
                    <c:if test="${user.admin}"><span class="pill gold">管理员</span></c:if>
                    <a class="btn btn-ghost btn-sm" href="${ctx}/logout">退出</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-ghost btn-sm" href="${ctx}/login">登录</a>
                    <a class="btn btn-primary btn-sm" href="${ctx}/register">注册</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<c:if test="${not empty sessionScope.flashMessage}">
    <div class="flash flash-${sessionScope.flashType}">
        <c:out value="${sessionScope.flashMessage}"/>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>

<main class="container">
