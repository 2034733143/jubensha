<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="${script.title}"/>
<%@ include file="common/header.jsp" %>

<div class="detail-head">
    <div class="detail-cover" style="background: linear-gradient(150deg, ${script.coverColor}, #171126 90%)">
        <span class="pill">${script.category}</span>
        <h2 style="margin-top:14px">${script.title}</h2>
        <small>${script.author} 著 · ${script.difficulty}</small>
    </div>
    <div class="detail-body">
        <h1>${script.title}</h1>
        <div class="meta-row">
            <span class="pill">${script.playerText}</span>
            <span class="pill">${script.durationText}</span>
            <span class="pill gold">${script.category}</span>
            <span class="pill purple">${script.difficulty}</span>
            <span class="rating">
                <c:choose>
                    <c:when test="${script.reviewCount > 0}">
                        <span class="score">${script.avgRating}</span> ★（${script.reviewCount} 条评价）
                    </c:when>
                    <c:otherwise><span class="none">暂无评分</span></c:otherwise>
                </c:choose>
            </span>
        </div>
        <p class="description"><c:out value="${script.description}"/></p>

        <div class="section-title" style="margin:26px 0 12px">
            <h2 style="font-size:18px">开放场次</h2>
        </div>
        <c:choose>
            <c:when test="${empty sessions}">
                <div class="panel empty" style="padding:24px">暂无开放场次，可联系门店加场。</div>
            </c:when>
            <c:otherwise>
                <div class="grid" style="gap:12px">
                    <c:forEach var="s" items="${sessions}">
                        <div class="session-card">
                            <div class="session-date">
                                <strong>${s.dayText}</strong>
                                <small>${s.monthText} ${s.weekText}</small>
                            </div>
                            <div class="session-main">
                                <h4>${s.timeRangeText}</h4>
                                <p>${s.room} · DM ${s.dmName} · 余位 ${s.remaining}/${s.capacity}</p>
                                <div class="seat-bar" style="margin-top:8px">
                                    <span style="width: ${s.booked * 100 / s.capacity}%"></span>
                                </div>
                            </div>
                            <div class="session-side">
                                <span class="price">¥<fmt:formatNumber value="${s.price}" maxFractionDigits="0"/></span>
                                <c:choose>
                                    <c:when test="${s.bookable}">
                                        <a class="btn btn-primary btn-sm" href="${ctx}/booking?sessionId=${s.id}">预约</a>
                                    </c:when>
                                    <c:otherwise><span class="badge badge-${s.status}">${s.statusText}</span></c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="section-title">
    <h2>玩家评价</h2>
    <span style="color:var(--muted);font-size:13px">${script.reviewCount} 条公开评价</span>
</div>

<div class="panel">
    <c:choose>
        <c:when test="${empty reviews}">
            <div class="empty">还没有玩家评价，快来成为第一个打分的人。</div>
        </c:when>
        <c:otherwise>
            <c:forEach var="r" items="${reviews}">
                <div class="review-item">
                    <div class="review-head">
                        <span><b>${r.username}</b> · <span class="stars">${r.stars}</span></span>
                        <span>${r.createdText}</span>
                    </div>
                    <p class="review-text"><c:out value="${r.content}"/></p>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="common/footer.jsp" %>
