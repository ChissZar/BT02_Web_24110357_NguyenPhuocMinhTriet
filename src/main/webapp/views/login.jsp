<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="login-page">
    <section class="form-card login-card">
        <p class="eyebrow">Bài tập Login với Cookie và Session</p>
        <h1>Đăng nhập hệ thống</h1>
        <p class="muted">Đăng nhập để tiếp tục đến trang quản lý Category.</p>

        <c:if test="${not empty alert}">
            <div class="alert"><c:out value="${alert}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="username">Tài khoản</label>
            <input type="text" id="username" name="username" value="${fn:escapeXml(param.username)}" autocomplete="username" required>

            <label for="password">Mật khẩu</label>
            <input type="password" id="password" name="password" autocomplete="current-password" required>

            <label class="checkbox-option">
                <input type="checkbox" name="remember">
                Nhớ tài khoản trong 30 phút
            </label>

            <button class="button primary full" type="submit">Đăng nhập</button>
        </form>

        <p class="login-hint">Tài khoản được tạo khi chạy TestJpa: <strong>trungnh / 123</strong></p>
    </section>
</main>
</body>
</html>
