<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="评价管理"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>评价管理</h2>
    <span style="color:var(--muted);font-size:13px">共 ${fn:length(reviews)} 条评价</span>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a href="${ctx}/admin/scripts">剧本管理</a>
    <a href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a class="active" href="${ctx}/admin/reviews">评价管理</a>
</div>

<div class="table-wrap">
    <table>
        <thead>
        <tr>
            <th>剧本</th>
            <th>用户</th>
            <th>评分</th>
            <th>内容</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${reviews}">
            <tr>
                <td>${r.scriptTitle}<br><small style="color:var(--muted)">${r.bookingCode}</small></td>
                <td>${r.username}<br><small style="color:var(--muted)">${r.createdText}</small></td>
                <td><span class="stars">${r.stars}</span></td>
                <td style="max-width:340px"><c:out value="${r.content}"/></td>
                <td><span class="badge ${r.visible ? 'badge-OPEN' : 'badge-CANCELED'}">${r.visible ? '显示中' : '已隐藏'}</span></td>
                <td>
                    <div class="actions">
                        <form method="post" action="${ctx}/admin/reviews">
                            <input type="hidden" name="id" value="${r.id}">
                            <input type="hidden" name="action" value="${r.visible ? 'hide' : 'show'}">
                            <button class="btn btn-ghost btn-sm" type="submit">${r.visible ? '隐藏' : '恢复'}</button>
                        </form>
                        <form method="post" action="${ctx}/admin/reviews"
                              onsubmit="return confirm('删除后不可恢复，确定删除这条评价？')">
                            <input type="hidden" name="id" value="${r.id}">
                            <input type="hidden" name="action" value="delete">
                            <button class="btn btn-danger btn-sm" type="submit">删除</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty reviews}">
            <tr><td colspan="6" class="empty">暂无评价</td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<%@ include file="../common/footer.jsp" %>
