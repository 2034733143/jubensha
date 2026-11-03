<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="首页"/>
<%@ include file="common/header.jsp" %>

<section class="hero">
    <div>
        <h1>今晚，<em>换一个人</em><br>走进另一段人生</h1>
        <p>8 本独家剧本、4 个沉浸式主题房、每日多场次开放预约。选本、选场、下单、评价，全流程线上完成。</p>
        <div class="hero-actions">
            <a class="btn btn-primary" href="${ctx}/scripts">浏览剧本库</a>
            <a class="btn btn-ghost" href="${ctx}/sessions">查看今日场次</a>
        </div>
    </div>
    <div class="hero-panel">
        <h3>实时数据 · 谜局门店</h3>
        <div class="hero-stats">
            <div><strong>${stats.scriptCount}</strong><small>在架剧本</small></div>
            <div><strong>${stats.sessionToday}</strong><small>今日场次</small></div>
            <div><strong>${stats.totalBookings}</strong><small>累计预约</small></div>
            <div><strong><fmt:formatNumber value="${stats.avgRating}" minFractionDigits="1" maxFractionDigits="1"/></strong><small>平均评分</small></div>
        </div>
    </div>
</section>

<div class="section-title">
    <h2>今日场次</h2>
    <a href="${ctx}/sessions">全部场次 →</a>
</div>

<c:choose>
    <c:when test="${empty todaySessions}">
        <div class="panel empty">今天暂时没有开放场次，看看明天的安排？</div>
    </c:when>
    <c:otherwise>
        <div class="grid grid-2">
            <c:forEach var="s" items="${todaySessions}">
                <div class="session-card">
                    <div class="session-date">
                        <strong>${s.dayText}</strong>
                        <small>${s.monthText} ${s.weekText}</small>
                    </div>
                    <div class="session-main">
                        <h4>${s.scriptTitle}</h4>
                        <p>${s.timeRangeText} · ${s.room} · DM ${s.dmName}</p>
                        <div class="meta-row" style="margin-top:8px">
                            <span class="pill ${s.bookable ? 'gold' : ''}">余位 ${s.remaining}/${s.capacity}</span>
                            <span class="pill purple">¥<fmt:formatNumber value="${s.price}" maxFractionDigits="0"/>/人</span>
                        </div>
                    </div>
                    <div class="session-side">
                        <c:choose>
                            <c:when test="${s.bookable}">
                                <a class="btn btn-primary btn-sm" href="${ctx}/booking?sessionId=${s.id}">立即预约</a>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-${s.status}">${s.statusText}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<div class="section-title">
    <h2>口碑剧本</h2>
    <a href="${ctx}/scripts?sort=rating">按评分排序 →</a>
</div>

<div class="grid grid-4">
    <c:forEach var="s" items="${featured}">
        <a class="card" href="${ctx}/script?id=${s.id}">
            <div class="cover" style="background: linear-gradient(150deg, ${s.coverColor}, #171126 85%)">
                <span class="tag pill">${s.category}</span>
                <h3>${s.title}</h3>
                <span class="sub">${s.author} 著</span>
            </div>
            <div class="card-body">
                <div class="rating">
                    <c:choose>
                        <c:when test="${s.reviewCount > 0}">
                            <span class="score">${s.avgRating}</span><span>★</span>
                            <span class="pill">${s.reviewCount} 条评价</span>
                        </c:when>
                        <c:otherwise><span class="none">暂无评分</span></c:otherwise>
                    </c:choose>
                </div>
                <p class="summary"><c:out value="${s.summary}"/></p>
                <div class="meta-row">
                    <span class="pill">${s.playerText}</span>
                    <span class="pill">${s.durationText}</span>
                    <span class="pill gold">${s.difficulty}</span>
                </div>
                <div class="card-foot">
                    <span class="price">¥<fmt:formatNumber value="${s.price}" maxFractionDigits="0"/><small>/人</small></span>
                    <span class="btn btn-ghost btn-sm">查看详情</span>
                </div>
            </div>
        </a>
    </c:forEach>
</div>

<c:if test="${not empty tomorrowSessions}">
    <div class="section-title">
        <h2>明日预告</h2>
        <a href="${ctx}/sessions?date=${tomorrowSessions[0].dateText}">查看全部 →</a>
    </div>
    <div class="grid grid-2">
        <c:forEach var="s" items="${tomorrowSessions}">
            <div class="session-card">
                <div class="session-date">
                    <strong>${s.dayText}</strong>
                    <small>${s.monthText} ${s.weekText}</small>
                </div>
                <div class="session-main">
                    <h4>${s.scriptTitle}</h4>
                    <p>${s.timeRangeText} · ${s.room} · DM ${s.dmName}</p>
                </div>
                <div class="session-side">
                    <a class="btn btn-ghost btn-sm" href="${ctx}/booking?sessionId=${s.id}">预约</a>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>

<%@ include file="common/footer.jsp" %>
