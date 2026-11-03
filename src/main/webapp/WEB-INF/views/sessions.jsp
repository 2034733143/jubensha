<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="场次预约"/>
<%@ include file="common/header.jsp" %>

<div class="section-title">
    <h2>场次预约</h2>
    <a href="${ctx}/my/bookings">我的预约 →</a>
</div>

<div class="filter-bar">
    <div class="field">
        <label>选择日期</label>
        <div style="display:flex;gap:8px;flex-wrap:wrap">
            <a class="btn btn-sm ${empty selectedDate ? 'btn-primary' : 'btn-ghost'}" href="${ctx}/sessions">全部</a>
            <c:forEach var="d" items="${dates}">
                <a class="btn btn-sm ${selectedDate == d ? 'btn-primary' : 'btn-ghost'}"
                   href="${ctx}/sessions?date=${d}">${fn:substring(d, 5, 10)}</a>
            </c:forEach>
        </div>
    </div>
</div>

<c:choose>
    <c:when test="${empty sessions}">
        <div class="panel empty">该日期暂无开放场次，试试其它日期。</div>
    </c:when>
    <c:otherwise>
        <div class="grid" style="gap:14px">
            <c:forEach var="s" items="${sessions}">
                <div class="session-card">
                    <div class="session-date">
                        <strong>${s.dayText}</strong>
                        <small>${s.monthText} ${s.weekText}</small>
                    </div>
                    <div class="session-main">
                        <h4><a href="${ctx}/script?id=${s.scriptId}">${s.scriptTitle}</a>
                            <span class="pill">${s.scriptCategory}</span>
                        </h4>
                        <p>${s.timeRangeText} · ${s.room} · DM ${s.dmName}</p>
                        <div class="meta-row" style="margin-top:8px">
                            <span class="pill ${s.remaining <= 2 ? 'gold' : ''}">余位 ${s.remaining}/${s.capacity}</span>
                            <span class="pill purple">¥<fmt:formatNumber value="${s.price}" maxFractionDigits="0"/>/人</span>
                            <span class="badge badge-${s.status}">${s.statusText}</span>
                        </div>
                    </div>
                    <div class="session-side">
                        <div class="seat-bar">
                            <span style="width: ${s.booked * 100 / s.capacity}%"></span>
                        </div>
                        <c:choose>
                            <c:when test="${s.bookable}">
                                <a class="btn btn-primary btn-sm" href="${ctx}/booking?sessionId=${s.id}">立即预约</a>
                            </c:when>
                            <c:otherwise><span class="btn btn-ghost btn-sm disabled">不可预约</span></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jsp" %>
