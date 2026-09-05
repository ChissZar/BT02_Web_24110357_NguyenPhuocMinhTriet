<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Category</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="container">
    <div class="page-heading">
        <div>
            <p class="eyebrow">Bài tập 02 - JPA API</p>
            <h1>Quản lý Category</h1>
            <p class="muted">Tổng số kết quả: ${count}</p>
        </div>
        <div class="heading-actions">
            <a class="button ghost" href="${pageContext.request.contextPath}/profile">Hồ sơ cá nhân</a>
            <c:if test="${not empty sessionScope.account}">
                <span class="welcome">Xin chào, <c:out value="${sessionScope.account.fullName}"/></span>
                <a class="button ghost" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </c:if>
            <a class="button primary" href="${pageContext.request.contextPath}/admin/category/add">Thêm Category</a>
        </div>
    </div>

    <form class="search-form" action="${pageContext.request.contextPath}/admin/categories" method="get">
        <input type="search" name="keyword" value="${fn:escapeXml(keyword)}" placeholder="Tìm theo tên Category">
        <button class="button" type="submit">Tìm kiếm</button>
        <c:if test="${not empty keyword}">
            <a class="button ghost" href="${pageContext.request.contextPath}/admin/categories">Xóa lọc</a>
        </c:if>
    </form>

    <div class="table-card">
        <table>
            <thead>
            <tr>
                <th>STT</th>
                <th>ID</th>
                <th>Images</th>
                <th>Category name</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${listcate}" var="cate" varStatus="stt">
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
                <tr>
                    <td>${stt.index + 1}</td>
                    <td>${cate.categoryid}</td>
                    <td><img class="thumbnail" src="${imgUrl}" alt="Ảnh ${fn:escapeXml(cate.categoryname)}"></td>
                    <td><c:out value="${cate.categoryname}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${cate.status == 1}"><span class="badge active">Hoạt động</span></c:when>
                            <c:otherwise><span class="badge locked">Khóa</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/admin/category/edit?id=${cate.categoryid}">Sửa</a>
                        <a class="danger" href="${pageContext.request.contextPath}/admin/category/delete?id=${cate.categoryid}"
                           onclick="return confirm('Bạn có chắc muốn xóa Category này?')">Xóa</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty listcate}">
                <tr>
                    <td class="empty" colspan="6">Chưa có Category phù hợp.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
