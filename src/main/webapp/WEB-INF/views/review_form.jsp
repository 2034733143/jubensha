<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="发表评价"/>
<%@ include file="common/header.jsp" %>

<div class="section-title">
    <h2>发表评价</h2>
    <a href="${ctx}/my/bookings">返回我的预约 →</a>
</div>

<div class="panel form-panel">
    <div class="order-summary">
        <div>
            <b>${script.title}</b><br>
            <span style="color:var(--muted);font-size:13px">
                预约编号 ${booking.code} · ${booking.sessionStartText} · ${booking.room} · ${booking.playerCount} 人
            </span>
        </div>
        <div style="text-align:right">
            <span style="color:var(--muted);font-size:13px">实付</span><br>
            <b>¥<fmt:formatNumber value="${booking.amount}" minFractionDigits="2"/></b>
        </div>
    </div>

    <form method="post" action="${ctx}/my/review">
        <input type="hidden" name="bookingId" value="${booking.id}">
        <div class="field" style="margin-bottom:18px">
            <label>本局评分</label>
            <div style="display:flex;gap:18px;padding:6px 0;font-size:15px">
                <label><input type="radio" name="rating" value="5"> ★★★★★ 神作</label>
                <label><input type="radio" name="rating" value="4" checked> ★★★★ 值得一玩</label>
                <label><input type="radio" name="rating" value="3"> ★★★ 中规中矩</label>
                <label><input type="radio" name="rating" value="2"> ★★ 有待改进</label>
                <label><input type="radio" name="rating" value="1"> ★ 不推荐</label>
            </div>
        </div>
        <div class="field">
            <label>评价内容（至少 5 个字，最多 500 字）</label>
            <textarea name="content" maxlength="500" required placeholder="可以从剧本逻辑、DM 主持、房间氛围、队友体验等方面说说你的感受…"></textarea>
        </div>
        <div class="form-actions">
            <button class="btn btn-primary" type="submit">发布评价</button>
            <a class="btn btn-ghost" href="${ctx}/my/bookings">取消</a>
        </div>
    </form>
</div>

<%@ include file="common/footer.jsp" %>
