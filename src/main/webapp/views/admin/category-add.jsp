<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Category</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="form-page">
    <a class="back-link" href="${pageContext.request.contextPath}/admin/categories">← Quay lại danh sách</a>
    <section class="form-card">
        <p class="eyebrow">CRUD Category</p>
        <h1>Thêm Category</h1>
        <c:if test="${not empty alert}">
            <div class="alert"><c:out value="${alert}"/></div>
        </c:if>
        <form action="${pageContext.request.contextPath}/admin/category/insert" method="post" enctype="multipart/form-data">
            <label for="categoryname">Category name</label>
            <input type="text" id="categoryname" name="categoryname" value="${fn:escapeXml(param.categoryname)}" required>

            <label for="images">Link images</label>
            <input type="url" id="images" name="images" value="${fn:escapeXml(param.images)}" placeholder="https://example.com/image.jpg">

            <label for="images1">Upload images</label>
            <input type="file" id="images1" name="images1" accept="image/*">

            <fieldset>
                <legend>Status</legend>
                <label class="radio-option">
                    <input type="radio" name="status" value="1" ${param.status == '0' ? '' : 'checked'}>
                    Hoạt động
                </label>
                <label class="radio-option">
                    <input type="radio" name="status" value="0" ${param.status == '0' ? 'checked' : ''}>
                    Khóa
                </label>
            </fieldset>

            <button class="button primary full" type="submit">Insert</button>
        </form>
    </section>
</main>
</body>
</html>
