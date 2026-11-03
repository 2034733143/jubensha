<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="剧本库"/>
<%@ include file="common/header.jsp" %>

<div class="section-title">
    <h2>剧本库</h2>
    <span style="color:var(--muted);font-size:13px">共 ${fn:length(scripts)} 个在架剧本</span>
</div>

<form class="filter-bar" method="get" action="${ctx}/scripts">
    <div class="field">
        <label>关键词</label>
        <input type="text" name="keyword" value="${keyword}" placeholder="剧本名 / 作者 / 简介">
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
    <div class="field">
        <label>难度</label>
        <select name="difficulty">
            <option value="">全部难度</option>
            <option value="新手" ${difficulty == '新手' ? 'selected' : ''}>新手</option>
            <option value="进阶" ${difficulty == '进阶' ? 'selected' : ''}>进阶</option>
            <option value="硬核" ${difficulty == '硬核' ? 'selected' : ''}>硬核</option>
        </select>
    </div>
    <div class="field">
        <label>排序</label>
        <select name="sort">
            <option value="new" ${sort == 'new' ? 'selected' : ''}>最新上架</option>
            <option value="rating" ${sort == 'rating' ? 'selected' : ''}>评分最高</option>
            <option value="hot" ${sort == 'hot' ? 'selected' : ''}>评价最多</option>
            <option value="price" ${sort == 'price' ? 'selected' : ''}>价格最低</option>
        </select>
    </div>
    <button class="btn btn-primary" type="submit">筛选</button>
    <a class="btn btn-ghost" href="${ctx}/scripts">重置</a>
</form>

<c:choose>
    <c:when test="${empty scripts}">
        <div class="panel empty">没有找到符合条件的剧本，换个关键词试试。</div>
    </c:when>
    <c:otherwise>
        <div class="grid grid-3">
            <c:forEach var="s" items="${scripts}">
                <a class="card" href="${ctx}/script?id=${s.id}">
                    <div class="cover" style="background: linear-gradient(150deg, ${s.coverColor}, #171126 85%)">
                        <span class="tag pill">${s.category}</span>
                        <h3>${s.title}</h3>
                        <span class="sub">${s.author} 著</span>
                    </div>
                    <div class="card-body">
                        <p class="summary"><c:out value="${s.summary}"/></p>
                        <div class="meta-row">
                            <span class="pill">${s.playerText}</span>
                            <span class="pill">${s.durationText}</span>
                            <span class="pill gold">${s.difficulty}</span>
                        </div>
                        <div class="card-foot">
                            <span class="price">¥<fmt:formatNumber value="${s.price}" maxFractionDigits="0"/><small>/人</small></span>
                            <span class="rating">
                                <c:choose>
                                    <c:when test="${s.reviewCount > 0}">${s.avgRating} <span>★</span></c:when>
                                    <c:otherwise><span class="none">暂无评分</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jsp" %>
