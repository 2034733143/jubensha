<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="场次管理"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>场次管理</h2>
    <a class="btn btn-primary btn-sm" href="${ctx}/admin/sessions?action=form">+ 新增场次</a>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a href="${ctx}/admin/scripts">剧本管理</a>
    <a class="active" href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<form class="filter-bar" method="get" action="${ctx}/admin/sessions">
    <div class="field">
        <label>状态</label>
        <select name="status">
            <option value="">全部状态</option>
            <c:forEach var="st" items="${['OPEN','FULL','STARTED','FINISHED','CANCELED']}">
                <option value="${st}" ${st == status ? 'selected' : ''}>${st}</option>
            </c:forEach>
        </select>
    </div>
    <div class="field">
        <label>日期</label>
        <input type="date" name="date" value="${date}">
    </div>
    <button class="btn btn-primary" type="submit">查询</button>
    <a class="btn btn-ghost" href="${ctx}/admin/sessions">重置</a>
</form>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>开场时间</th>
            <th>剧本</th>
            <th>房间 / DM</th>
            <th>上座</th>
            <th>价格</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${sessions}">
            <tr>
                <td>${s.dateText} ${s.weekText}<br><small style="color:var(--muted)">${s.timeRangeText}</small></td>
                <td>${s.scriptTitle}<br><small style="color:var(--muted)">${s.scriptCategory}</small></td>
                <td>${s.room}<br><small style="color:var(--muted)">DM ${s.dmName}</small></td>
                <td>${s.booked}/${s.capacity}</td>
                <td>¥<fmt:formatNumber value="${s.price}" minFractionDigits="2"/></td>
                <td><span class="badge badge-${s.status}">${s.statusText}</span></td>
                <td>
                    <div class="actions">
                        <c:if test="${s.status == 'OPEN' or s.status == 'FULL'}">
                            <a class="btn btn-ghost btn-sm" href="${ctx}/admin/sessions?action=form&id=${s.id}">编辑</a>
                            <form method="post" action="${ctx}/admin/sessions">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${s.id}">
                                <input type="hidden" name="status" value="STARTED">
                                <button class="btn btn-ghost btn-sm" type="submit">开场</button>
                            </form>
                            <form method="post" action="${ctx}/admin/sessions"
                                  onsubmit="return confirm('取消后该场次不可恢复，确定取消？')">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${s.id}">
                                <input type="hidden" name="status" value="CANCELED">
                                <button class="btn btn-danger btn-sm" type="submit">取消</button>
                            </form>
                        </c:if>
                        <c:if test="${s.status == 'STARTED'}">
                            <form method="post" action="${ctx}/admin/sessions"
                                  onsubmit="return confirm('结束场次会将该场次所有预约标记为已完成，并开放评价。')">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="id" value="${s.id}">
                                <input type="hidden" name="status" value="FINISHED">
                                <button class="btn btn-primary btn-sm" type="submit">结束并结算</button>
                            </form>
                        </c:if>
                        <c:if test="${s.status == 'FINISHED' or s.status == 'CANCELED'}">
                            <span style="color:var(--muted);font-size:12px">无可用操作</span>
                        </c:if>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty sessions}">
            <tr><td colspan="7" class="empty">暂无场次数据</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="../common/footer.jsp" %>
