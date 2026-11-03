<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="我的预约"/>
<%@ include file="common/header.jsp" %>

<div class="section-title">
    <h2>我的预约</h2>
    <a href="${ctx}/sessions">继续选场 →</a>
</div>

<c:choose>
    <c:when test="${empty bookings}">
        <div class="panel empty">还没有预约记录，去<a href="${ctx}/sessions">挑一场</a>开始你的剧本杀之旅。</div>
    </c:when>
    <c:otherwise>
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>预约编号</th>
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
                        <td style="font-family:monospace;color:var(--gold)">${b.code}</td>
                        <td>
                            <div style="font-weight:600">${b.scriptTitle}</div>
                            <div style="color:var(--muted);font-size:12px">${b.sessionStartText} · ${b.room}</div>
                        </td>
                        <td>${b.playerCount} 人</td>
                        <td>¥<fmt:formatNumber value="${b.amount}" minFractionDigits="2"/></td>
                        <td><span class="badge badge-${b.status}">${b.statusText}</span></td>
                        <td>
                            <div class="actions">
                                <c:if test="${b.payable}">
                                    <form method="post" action="${ctx}/my/booking/action">
                                        <input type="hidden" name="bookingId" value="${b.id}">
                                        <input type="hidden" name="action" value="pay">
                                        <button class="btn btn-primary btn-sm" type="submit">模拟支付</button>
                                    </form>
                                </c:if>
                                <c:if test="${b.cancelable}">
                                    <form method="post" action="${ctx}/my/booking/action"
                                          onsubmit="return confirm('确认取消该预约？座位将立即释放。')">
                                        <input type="hidden" name="bookingId" value="${b.id}">
                                        <input type="hidden" name="action" value="cancel">
                                        <button class="btn btn-danger btn-sm" type="submit">取消</button>
                                    </form>
                                </c:if>
                                <c:if test="${b.status == 'FINISHED' and empty reviewed[b.id]}">
                                    <a class="btn btn-ghost btn-sm" href="${ctx}/my/review?bookingId=${b.id}">写评价</a>
                                </c:if>
                                <c:if test="${not empty reviewed[b.id]}">
                                    <span class="pill gold">已评价</span>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jsp" %>
