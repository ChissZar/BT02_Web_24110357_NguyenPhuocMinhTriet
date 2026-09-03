<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập nhật Category</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="form-page">
    <a class="back-link" href="${pageContext.request.contextPath}/admin/categories">← Quay lại danh sách</a>
    <section class="form-card">
        <p class="eyebrow">CRUD Category</p>
        <h1>Cập nhật Category</h1>
        <c:if test="${not empty alert}">
            <div class="alert"><c:out value="${alert}"/></div>
        </c:if>

        <c:choose>
            <c:when test="${not empty cate.images and (fn:startsWith(cate.images, 'http://') or fn:startsWith(cate.images, 'https://'))}">
                <c:set var="imgUrl" value="${cate.images}"/>
            </c:when>
            <c:otherwise>
                <c:url value="/image" var="imgUrl">
                    <c:param name="fname" value="${cate.images}"/>
                </c:url>
            </c:otherwise>
        </c:choose>

        <form action="${pageContext.request.contextPath}/admin/category/update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="categoryid" value="${cate.categoryid}">

            <label for="categoryname">Category name</label>
            <input type="text" id="categoryname" name="categoryname" value="${fn:escapeXml(cate.categoryname)}" required>

            <label for="images">Link images</label>
            <input type="text" id="images" name="images" value="${fn:escapeXml(cate.images)}">

            <img class="preview" src="${imgUrl}" alt="Ảnh ${fn:escapeXml(cate.categoryname)}">

            <label for="images1">Upload images mới</label>
            <input type="file" id="images1" name="images1" accept="image/*">

            <fieldset>
                <legend>Status</legend>
                <label class="radio-option">
                    <input type="radio" name="status" value="1" ${cate.status == 1 ? 'checked' : ''}>
                    Hoạt động
                </label>
                <label class="radio-option">
                    <input type="radio" name="status" value="0" ${cate.status != 1 ? 'checked' : ''}>
                    Khóa
                </label>
            </fieldset>

            <button class="button primary full" type="submit">Update</button>
        </form>
    </section>
</main>
</body>
</html>
