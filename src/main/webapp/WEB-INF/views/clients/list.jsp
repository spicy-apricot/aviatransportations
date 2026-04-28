<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Клиенты - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Клиенты</h2>
        <a href="${pageContext.request.contextPath}/clients/new" class="btn btn-success">Создать клиента</a>
    </div>

    <form method="get" class="mb-3">
        <div class="input-group">
            <input type="text" name="search" class="form-control" placeholder="Поиск по ФИО или телефону" value="${search}">
            <button class="btn btn-outline-secondary" type="submit">Найти</button>
            <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Сбросить</a>
        </div>
    </form>

    <table class="table table-hover">
        <thead>
        <tr>
            <th>ID</th>
            <th>ФИО</th>
            <th>Телефон</th>
            <th>Email</th>
            <th class="text-end">Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="client" items="${clients}">
            <tr>
                <td>${client.clientID}</td>
                <td><a href="${pageContext.request.contextPath}/clients/${client.clientID}">${client.name}</a></td>
                <td>${client.phoneNumber}</td>
                <td>${client.email}</td>
                <td class="text-end">
                    <a href="${pageContext.request.contextPath}/clients/${client.clientID}/edit" class="btn btn-sm btn-outline-warning">Редактировать</a>
                    <form method="post" action="${pageContext.request.contextPath}/clients/${client.clientID}/delete" style="display:inline" onsubmit="return confirm('Удалить клиента?')">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Удалить</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
