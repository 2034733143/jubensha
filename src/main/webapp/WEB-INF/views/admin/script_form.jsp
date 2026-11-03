<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${empty script ? '新增剧本' : '编辑剧本'}"/>
<c:set var="formTitle" value="${empty script ? '新增剧本' : '编辑剧本'}"/>
<%@ include file="../common/header.jsp" %>

<div class="section-title">
    <h2>${formTitle}</h2>
    <a href="${ctx}/admin/scripts">返回列表 →</a>
</div>

<div class="admin-tabs">
    <a href="${ctx}/admin">看板</a>
    <a class="active" href="${ctx}/admin/scripts">剧本管理</a>
    <a href="${ctx}/admin/sessions">场次管理</a>
    <a href="${ctx}/admin/bookings">预约管理</a>
    <a href="${ctx}/admin/reviews">评价管理</a>
</div>

<div class="panel form-panel">
    <form method="post" action="${ctx}/admin/scripts">
        <input type="hidden" name="action" value="save">
        <input type="hidden" name="id" value="${script.id}">
        <div class="form-grid">
            <div class="field">
                <label>剧本名称 *</label>
                <input type="text" name="title" value="${script.title}" required maxlength="100">
            </div>
            <div class="field">
                <label>作者</label>
                <input type="text" name="author" value="${script.author}" maxlength="50">
            </div>
            <div class="field">
                <label>类型 *</label>
                <select name="category">
                    <c:forEach var="cat" items="${['硬核推理','情感沉浸','机制阵营','恐怖惊悚']}">
                        <option value="${cat}" ${cat == script.category ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>难度 *</label>
                <select name="difficulty">
                    <c:forEach var="d" items="${['新手','进阶','硬核']}">
                        <option value="${d}" ${d == script.difficulty ? 'selected' : ''}>${d}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>最少人数</label>
                <input type="number" name="minPlayers" value="${empty script ? 4 : script.minPlayers}" min="1" max="20" required>
            </div>
            <div class="field">
                <label>最多人数</label>
                <input type="number" name="maxPlayers" value="${empty script ? 6 : script.maxPlayers}" min="1" max="20" required>
            </div>
            <div class="field">
                <label>时长（分钟）</label>
                <input type="number" name="durationMin" value="${empty script ? 240 : script.durationMin}" min="30" step="30" required>
            </div>
            <div class="field">
                <label>单人价格（元）</label>
                <input type="number" name="price" value="${empty script ? '128.00' : script.price}" min="0" step="0.01" required>
            </div>
            <div class="field">
                <label>封面主色</label>
                <input type="color" name="coverColor" value="${empty script ? '#4c3f6e' : script.coverColor}" style="height:40px;padding:4px">
            </div>
            <div class="field">
                <label>上架状态</label>
                <select name="status">
                    <option value="1" ${empty script or script.status ? 'selected' : ''}>上架</option>
                    <option value="0" ${not empty script and not script.status ? 'selected' : ''}>下架</option>
                </select>
            </div>
            <div class="field full">
                <label>一句话简介（列表展示）</label>
                <input type="text" name="summary" value="${script.summary}" maxlength="200">
            </div>
            <div class="field full">
                <label>剧本详情</label>
                <textarea name="description" rows="6"><c:out value="${script.description}"/></textarea>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn btn-primary" type="submit">保存</button>
            <a class="btn btn-ghost" href="${ctx}/admin/scripts">取消</a>
        </div>
    </form>
</div>

<%@ include file="../common/footer.jsp" %>
