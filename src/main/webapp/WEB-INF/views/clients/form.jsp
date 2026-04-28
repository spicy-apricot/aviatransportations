<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${client.clientID == null ? 'Создание' : 'Редактирование'} клиента - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <h2 class="mb-3">${client.clientID == null ? 'Создание клиента' : 'Редактирование клиента'}</h2>
    <form method="post" action="${pageContext.request.contextPath}/clients" class="col-lg-6">
        <c:if test="${client.clientID != null}">
            <input type="hidden" name="clientID" value="${client.clientID}">
        </c:if>
        <div class="mb-3">
            <label class="form-label">ФИО</label>
            <input type="text" class="form-control" name="name" value="${client.name}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Телефон</label>
            <input type="tel" class="form-control" name="phoneNumber" value="${client.phoneNumber}" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" name="email" value="${client.email}">
        </div>
        <div class="mb-3">
            <label class="form-label">Адрес</label>
            <input type="text" class="form-control" name="address" value="${client.address}">
        </div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Отмена</a>
    </form>
</div>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
