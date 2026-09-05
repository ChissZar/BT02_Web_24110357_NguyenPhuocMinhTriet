<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Tài khoản</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <sitemesh:write property="head"/>
</head>
<body>
    <nav class="container heading-actions" aria-label="Điều hướng tài khoản">
        <a class="button" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a>
        <a class="button primary" href="${pageContext.request.contextPath}/profile">Hồ sơ</a>
        <a class="button ghost" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
    </nav>
    <sitemesh:write property="body"/>
    <footer class="container muted">Quản lý tài khoản</footer>
</body>
</html>
