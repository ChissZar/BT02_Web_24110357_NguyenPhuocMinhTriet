<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head><title>Hồ sơ cá nhân</title></head>
<body>
<main class="form-page">
    <section class="form-card">
        <h1>Hồ sơ cá nhân</h1>
        <p class="muted">Tài khoản: <c:out value="${profile.userName}"/></p>
        <c:if test="${not empty sessionScope.profileSuccess}">
            <p role="status"><c:out value="${sessionScope.profileSuccess}"/></p>
            <c:remove var="profileSuccess" scope="session"/>
        </c:if>
        <c:if test="${not empty alert}"><p class="alert" role="alert"><c:out value="${alert}"/></p></c:if>
        <c:url var="avatarUrl" value="/image"><c:param name="fname" value="${profile.avatar}"/></c:url>
        <img class="preview" src="${fn:escapeXml(avatarUrl)}" alt="Ảnh đại diện">
        <form action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data">
            <input type="hidden" name="csrf" value="${sessionScope.profileCsrf}">
            <label for="fullname">Họ và tên</label>
            <input id="fullname" name="fullname" maxlength="100" required autocomplete="name"
                   value="${fn:escapeXml(submitted ? fullNameInput : profile.fullName)}">
            <label for="phone">Số điện thoại</label>
            <input type="tel" id="phone" name="phone" maxlength="20" autocomplete="tel"
                   value="${fn:escapeXml(submitted ? phoneInput : profile.phone)}">
            <label for="images">Ảnh đại diện mới</label>
            <input type="file" id="images" name="images" accept="image/jpeg,image/png" aria-describedby="image-help">
            <p id="image-help" class="muted">JPEG hoặc PNG, tối đa 5 MB. Không chọn ảnh để giữ ảnh hiện tại.</p>
            <button class="button primary full" type="submit">Lưu thay đổi</button>
        </form>
    </section>
</main>
</body>
</html>
