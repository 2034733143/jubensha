<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="预约下单"/>
<%@ include file="common/header.jsp" %>

<div class="section-title">
    <h2>确认预约信息</h2>
    <a href="${ctx}/script?id=${script.id}">返回剧本详情 →</a>
</div>

<div class="panel form-panel">
    <div class="order-summary">
        <div>
            <b>${script.title}</b><br>
            <span style="color:var(--muted);font-size:13px">
                ${gameSession.dateText}（${gameSession.weekText}）${gameSession.timeRangeText}
                · ${gameSession.room} · DM ${gameSession.dmName}
            </span>
        </div>
        <div style="text-align:right">
            <span style="color:var(--muted);font-size:13px">单价</span><br>
            <b>¥<fmt:formatNumber value="${gameSession.price}" minFractionDigits="2"/></b>
        </div>
    </div>

    <div class="meta-row" style="margin-bottom:18px">
        <span class="pill">剧本人数 ${script.playerText}</span>
        <span class="pill">时长 ${script.durationText}</span>
        <span class="pill ${gameSession.remaining <= 2 ? 'gold' : ''}">当前余位 ${gameSession.remaining}/${gameSession.capacity}</span>
    </div>

    <c:if test="${not empty error}">
        <div class="flash flash-error" style="margin:0 0 18px"><c:out value="${error}"/></div>
    </c:if>

    <form method="post" action="${ctx}/booking">
        <input type="hidden" name="sessionId" value="${gameSession.id}">
        <div class="form-grid">
            <div class="field">
                <label>预约人数</label>
                <select name="playerCount">
                    <c:forEach var="i" begin="1" end="${gameSession.remaining}">
                        <option value="${i}" ${i == playerCount ? 'selected' : ''}>${i} 人</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>联系人姓名</label>
                <input type="text" name="contactName" value="${empty defaultName ? user.displayName : defaultName}" required maxlength="50">
            </div>
            <div class="field">
                <label>联系电话</label>
                <input type="text" name="contactPhone" value="${defaultPhone}" required maxlength="20" placeholder="11 位手机号">
            </div>
            <div class="field full">
                <label>备注（可选）</label>
                <textarea name="remark" maxlength="200" placeholder="例如：希望安排靠窗座位、有玩家过生日等"><c:out value="${remark}"/></textarea>
            </div>
        </div>
        <div class="form-actions">
            <button class="btn btn-primary" type="submit">提交预约</button>
            <a class="btn btn-ghost" href="${ctx}/sessions">再看看其它场次</a>
        </div>
    </form>
    <div class="auth-hint">提交后预约状态为「待确认」，可在“我的预约”中模拟支付以确认座位。</div>
</div>

<%@ include file="common/footer.jsp" %>
