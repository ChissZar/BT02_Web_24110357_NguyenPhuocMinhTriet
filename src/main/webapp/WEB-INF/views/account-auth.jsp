<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:choose>
    <c:when test="${mode == 'register'}"><c:set var="heading" value="Đăng ký tài khoản"/></c:when>
    <c:when test="${mode == 'activate'}"><c:set var="heading" value="Kích hoạt tài khoản"/></c:when>
    <c:when test="${mode == 'forgot-password'}"><c:set var="heading" value="Quên mật khẩu"/></c:when>
    <c:otherwise><c:set var="heading" value="Đặt lại mật khẩu"/></c:otherwise>
</c:choose>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${heading}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="form-page"><section class="form-card">
    <h1><c:out value="${heading}"/></h1>
    <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
    <c:if test="${not empty notice}"><p role="status"><c:out value="${notice}"/></p></c:if>
    <form method="post" action="${pageContext.request.contextPath}/${mode}">
        <input type="hidden" name="csrf" value="${sessionScope.authCsrf}">
        <c:if test="${mode == 'register'}">
            <label for="username">Tên đăng nhập</label>
            <input id="username" name="username" minlength="3" maxlength="50" required autocomplete="username" value="${fn:escapeXml(param.username)}">
            <label for="fullname">Họ tên</label>
            <input id="fullname" name="fullname" maxlength="100" required autocomplete="name" value="${fn:escapeXml(param.fullname)}">
        </c:if>
        <label for="email">Email</label>
        <input type="email" id="email" name="email" maxlength="100" required autocomplete="email" value="${fn:escapeXml(param.email)}">
        <c:if test="${mode == 'activate' || mode == 'reset-password'}">
            <label for="otp">Mã OTP gồm 8 chữ số</label>
            <input id="otp" name="otp" inputmode="numeric" maxlength="8" pattern="[0-9]{8}" autocomplete="one-time-code">
            <p class="muted">Mã có hiệu lực 10 phút, tối đa 5 lần thử. Chờ ít nhất 60 giây trước khi gửi lại.</p>
        </c:if>
        <c:if test="${mode == 'register' || mode == 'reset-password'}">
            <label for="password">Mật khẩu mới</label>
            <input type="password" id="password" name="password" maxlength="128" required autocomplete="new-password">
            <label for="confirmPassword">Nhập lại mật khẩu</label>
            <input type="password" id="confirmPassword" name="confirmPassword" maxlength="128" required autocomplete="new-password">
        </c:if>
        <p><button class="button primary" type="submit"><c:out value="${heading}"/></button>
        <c:if test="${mode == 'activate'}"><button class="button" name="action" value="resend" type="submit" formnovalidate>Gửi lại OTP</button></c:if></p>
    </form>
    <p><a href="${pageContext.request.contextPath}/login">Đăng nhập</a> ·
       <a href="${pageContext.request.contextPath}/activate">Kích hoạt / gửi lại mã</a> ·
       <a href="${pageContext.request.contextPath}/forgot-password">Gửi mã đặt lại mật khẩu</a> ·
       <a href="${pageContext.request.contextPath}/reset-password">Đặt lại mật khẩu</a></p>
</section></main>
</body>
</html>
