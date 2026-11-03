<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="后台看板"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>后台看板</h2>
    <span style="color:var(--muted);font-size:13px">数据实时统计 · 今日 ${stats.sessionToday} 场</span>
</div>

<div class="admin-tabs">
    <a class="active" href="${ctx}/admin">看板</a>
    <a href="${ctx}/admin/scripts">剧本管理</a>
    <a href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<div class="stat-grid">
    <div class="stat"><small>在架剧本</small><strong>${stats.scriptCount}</strong></div>
    <div class="stat"><small>今日场次</small><strong>${stats.sessionToday}</strong></div>
    <div class="stat"><small>待确认预约</small><strong>${stats.pendingBookings}</strong></div>
    <div class="stat"><small>累计营业额</small><strong>¥<fmt:formatNumber value="${stats.revenue}" maxFractionDigits="0"/></strong></div>
</div>
<div class="stat-grid" style="margin-top:16px">
    <div class="stat"><small>累计预约</small><strong>${stats.totalBookings}</strong></div>
    <div class="stat"><small>注册用户</small><strong>${stats.userCount}</strong></div>
    <div class="stat"><small>公开评价</small><strong>${stats.reviewCount}</strong></div>
    <div class="stat"><small>平均评分</small><strong><fmt:formatNumber value="${stats.avgRating}" minFractionDigits="1" maxFractionDigits="1"/></strong></div>
</div>

<div class="section-title">
    <h2>待确认预约</h2>
    <a href="${ctx}/admin/bookings?status=PENDING">全部待确认 →</a>
</div>

<c:choose>
    <c:when test="${empty pendingBookings}">
        <div class="panel empty">没有待确认的预约。</div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>编号</th>
                    <th>用户</th>
                    <th>剧本 / 场次</th>
                    <th>人数</th>
                    <th>金额</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="b" items="${pendingBookings}" end="7">
                    <tr>
                        <td style="font-family:monospace;color:var(--gold)">${b.code}</td>
                        <td>${b.username}<br><small style="color:var(--muted)">${b.contactPhone}</small></td>
                        <td>${b.scriptTitle}<br><small style="color:var(--muted)">${b.sessionStartText}</small></td>
                        <td>${b.playerCount} 人</td>
                        <td>¥<fmt:formatNumber value="${b.amount}" minFractionDigits="2"/></td>
                        <td>
                            <div class="actions">
                                <form method="post" action="${ctx}/admin/bookings">
                                    <input type="hidden" name="id" value="${b.id}">
                                    <input type="hidden" name="action" value="confirm">
                                    <button class="btn btn-primary btn-sm" type="submit">确认</button>
                                </form>
                                <form method="post" action="${ctx}/admin/bookings">
                                    <input type="hidden" name="id" value="${b.id}">
                                    <input type="hidden" name="action" value="cancel">
                                    <button class="btn btn-danger btn-sm" type="submit">取消</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<div class="section-title">
    <h2>今日场次</h2>
    <a href="${ctx}/admin/sessions">全部场次 →</a>
</div>

<c:choose>
    <c:when test="${empty todaySessions}">
        <div class="panel empty">今天没有场次安排。</div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>时间</th>
                    <th>剧本</th>
                    <th>房间 / DM</th>
                    <th>上座</th>
                    <th>状态</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="s" items="${todaySessions}">
                    <tr>
                        <td>${s.timeRangeText}</td>
                        <td>${s.scriptTitle}</td>
                        <td>${s.room} / ${s.dmName}</td>
                        <td>${s.booked}/${s.capacity}</td>
                        <td><span class="badge badge-${s.status}">${s.statusText}</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="../common/footer.jsp" %>
