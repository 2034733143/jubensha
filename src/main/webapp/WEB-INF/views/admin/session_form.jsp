<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty gameSession ? '新增场次' : '编辑场次'}"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>${empty gameSession ? '新增场次' : '编辑场次'}</h2>
    <a href="${ctx}/admin/sessions">返回列表 →</a>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a href="${ctx}/admin/scripts">剧本管理</a>
    <a class="active" href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<div class="panel form-panel">
    <form method="post" action="${ctx}/admin/sessions">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="${gameSession.id}">
        <div class="form-grid">
            <div class="field full">
                <label>所属剧本 *</label>
                <select name="scriptId" required>
                    <c:forEach var="sc" items="${scripts}">
                        <option value="${sc.id}" ${sc.id == gameSession.scriptId ? 'selected' : ''}>
                            ${sc.title}（${sc.category} · ${sc.difficulty} · ${sc.playerText}）
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>房间 *</label>
                <input type="text" name="room" value="${gameSession.room}" maxlength="50" required placeholder="如：A 雾隐厅">
            </div>
            <div class="field">
                <label>DM 主持 *</label>
                <input type="text" name="dmName" value="${gameSession.dmName}" maxlength="50" required>
            </div>
            <div class="field">
                <label>开始时间 *</label>
                <input type="datetime-local" name="startTime" value="${gameSession.startInput}" required>
            </div>
            <div class="field">
                <label>结束时间 *</label>
                <input type="datetime-local" name="endTime" value="${gameSession.endInput}" required>
            </div>
            <div class="field">
                <label>座位数 *</label>
                <input type="number" name="capacity" value="${empty gameSession ? 6 : gameSession.capacity}" min="1" max="20" required>
            </div>
            <div class="field">
                <label>单人价格（元）*</label>
                <input type="number" name="price" value="${empty gameSession ? '128.00' : gameSession.price}" min="0" step="0.01" required>
            </div>
        </div>
        <c:if test="${not empty gameSession}">
            <div class="auth-hint">当前已有 ${gameSession.booked} 人预约，修改座位数时请勿低于该人数。</div>
        </c:if>
        <div class="form-actions">
            <button class="btn btn-primary" type="submit">保存</button>
            <a class="btn btn-ghost" href="${ctx}/admin/sessions">取消</a>
        </div>
    </form>
</div>

<%@ include file="../common/footer.jsp" %>
