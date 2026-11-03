<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="预约管理"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>预约管理</h2>
    <span style="color:var(--muted);font-size:13px">共 ${fn:length(bookings)} 条记录</span>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a href="${ctx}/admin/scripts">剧本管理</a>
    <a href="${ctx}/admin/sessions">场次管理</a>
    <a class="active" href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<form class="filter-bar" method="get" action="${ctx}/admin/bookings">
    <div class="field">
        <label>状态</label>
        <select name="status">
            <option value="">全部状态</option>
            <c:forEach var="st" items="${['PENDING','CONFIRMED','FINISHED','CANCELED']}">
                <option value="${st}" ${st == status ? 'selected' : ''}>${st}</option>
            </c:forEach>
        </select>
    </div>
    <button class="btn btn-primary" type="submit">查询</button>
    <a class="btn btn-ghost" href="${ctx}/admin/bookings">重置</a>
</form>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>编号</th>
            <th>用户</th>
            <th>剧本 / 场次</th>
            <th>人数</th>
            <th>金额</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="b" items="${bookings}">
            <tr>
                <td style="font-family:monospace;color:var(--gold)">${b.code}<br>
                    <small style="color:var(--muted)">${b.createdText}</small></td>
                <td>${b.username}<br><small style="color:var(--muted)">${b.contactName} ${b.contactPhone}</small></td>
                <td>${b.scriptTitle}<br><small style="color:var(--muted)">${b.sessionStartText} · ${b.room}</small></td>
                <td>${b.playerCount} 人</td>
                <td>¥<fmt:formatNumber value="${b.amount}" minFractionDigits="2"/></td>
                <td><span class="badge badge-${b.status}">${b.statusText}</span></td>
                <td>
                    <div class="actions">
                        <c:if test="${b.status == 'PENDING'}">
                            <form method="post" action="${ctx}/admin/bookings">
                                <input type="hidden" name="id" value="${b.id}">
                                <input type="hidden" name="action" value="confirm">
                                <button class="btn btn-primary btn-sm" type="submit">确认</button>
                            </form>
                        </c:if>
                        <c:if test="${b.status == 'PENDING' or b.status == 'CONFIRMED'}">
                            <form method="post" action="${ctx}/admin/bookings">
                                <input type="hidden" name="id" value="${b.id}">
                                <input type="hidden" name="action" value="cancel">
                                <button class="btn btn-danger btn-sm" type="submit">取消</button>
                            </form>
                        </c:if>
                        <c:if test="${b.status == 'CONFIRMED'}">
                            <form method="post" action="${ctx}/admin/bookings">
                                <input type="hidden" name="id" value="${b.id}">
                                <input type="hidden" name="action" value="finish">
                                <button class="btn btn-ghost btn-sm" type="submit">标记完成</button>
                            </form>
                        </c:if>
                        <c:if test="${b.status == 'FINISHED' or b.status == 'CANCELED'}">
                            <span style="color:var(--muted);font-size:12px">已归档</span>
                        </c:if>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty bookings}">
            <tr><td colspan="7" class="empty">暂无预约记录</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="../common/footer.jsp" %>
