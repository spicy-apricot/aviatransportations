<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${client.name} - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>${client.name}</h2>
        <a href="${pageContext.request.contextPath}/clients/${client.clientID}/edit" class="btn btn-outline-warning">Редактировать</a>
    </div>
    <dl class="row">
        <dt class="col-sm-3">Телефон</dt><dd class="col-sm-9">${client.phoneNumber}</dd>
        <dt class="col-sm-3">Email</dt><dd class="col-sm-9">${client.email}</dd>
        <dt class="col-sm-3">Адрес</dt><dd class="col-sm-9">${client.address}</dd>
    </dl>
    <h4 class="mt-4">Статистика перелетов</h4>
    <c:choose>
        <c:when test="${not empty traveled}">
            <table class="table">
                <thead><tr><th>Авиакомпания</th><th>Расстояние</th></tr></thead>
                <tbody>
                <c:forEach var="record" items="${traveled}">
                    <tr><td>${record.airline.name}</td><td>${record.distance} км</td></tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise><p class="text-muted">Записей пока нет.</p></c:otherwise>
    </c:choose>
</div>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
