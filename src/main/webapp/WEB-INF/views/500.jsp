<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>服务异常 · 谜局剧本杀</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<main class="container">
    <div class="panel empty" style="margin-top:60px">
        <h2 style="color:var(--danger);letter-spacing:2px">500 · 剧情出了岔子</h2>
        <p>服务器处理请求时发生异常，请稍后重试。</p>
        <p><a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/home">回到首页</a></p>
    </div>
</main>
</body>
</html>
