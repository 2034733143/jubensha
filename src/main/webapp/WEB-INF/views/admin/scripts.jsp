<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="剧本管理"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>剧本管理</h2>
    <a class="btn btn-primary btn-sm" href="${ctx}/admin/scripts?action=form">+ 新增剧本</a>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a class="active" href="${ctx}/admin/scripts">剧本管理</a>
    <a href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<form class="filter-bar" method="get" action="${ctx}/admin/scripts">
    <div class="field">
        <label>关键词</label>
        <input type="text" name="keyword" value="${keyword}" placeholder="剧本名 / 作者">
    </div>
    <div class="field">
        <label>类型</label>
        <select name="category">
            <option value="">全部类型</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat}" ${cat == category ? 'selected' : ''}>${cat}</option>
            </c:forEach>
        </select>
    </div>
    <button class="btn btn-primary" type="submit">查询</button>
    <a class="btn btn-ghost" href="${ctx}/admin/scripts">重置</a>
</form>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>剧本</th>
            <th>类型 / 难度</th>
            <th>人数 / 时长</th>
            <th>价格</th>
            <th>评分</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${scripts}">
            <tr>
                <td>
                    <div style="font-weight:600">${s.title}</div>
                    <small style="color:var(--muted)">${s.author}</small>
                </td>
                <td>${s.category}<br><small style="color:var(--muted)">${s.difficulty}</small></td>
                <td>${s.playerText}<br><small style="color:var(--muted)">${s.durationText}</small></td>
                <td>¥<fmt:formatNumber value="${s.price}" minFractionDigits="2"/></td>
                <td>${s.reviewCount > 0 ? s.avgRating : '—'} <small style="color:var(--muted)">(${s.reviewCount})</small></td>
                <td><span class="badge ${s.status ? 'badge-OPEN' : 'badge-CANCELED'}">${s.status ? '已上架' : '已下架'}</span></td>
                <td>
                    <div class="actions">
                        <a class="btn btn-ghost btn-sm" href="${ctx}/admin/scripts?action=form&id=${s.id}">编辑</a>
                        <form method="post" action="${ctx}/admin/scripts">
                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="id" value="${s.id}">
                            <input type="hidden" name="status" value="${s.status ? 0 : 1}">
                            <button class="btn ${s.status ? 'btn-danger' : 'btn-primary'} btn-sm" type="submit">
                                ${s.status ? '下架' : '上架'}
                            </button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty scripts}">
            <tr><td colspan="7" class="empty">暂无剧本数据</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="../common/footer.jsp" %>
