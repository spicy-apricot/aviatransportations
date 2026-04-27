<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head><title>Клиенты</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <h2>Клиенты</h2>

    <form method="get" class="mb-3">
        <div class="input-group">
            <input type="text" name="search" class="form-control" placeholder="Поиск по ФИО или телефону" value="${search}">
            <button class="btn btn-outline-secondary" type="submit">Найти</button>
            <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Сбросить</a>
        </div>
    </form>

    <a href="${pageContext.request.contextPath}/clients/new" class="btn btn-success mb-3">+ Создать клиента</a>

    <table class="table table-hover">
        <thead>
            <tr><th>ФИО</th><th>Телефон</th><th>Рейсы</th><th>Действия</th></tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${clients}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/clients/${c.id}">${c.fullName}</a></td>
                    <td>${c.phone}</td>
                    <td>${fn:length(c.tickets)} билетов</td>
                    <td>
                        <a href="${pageContext.request.contextPath}/clients/${c.id}/edit" class="btn btn-sm btn-warning">✏️</a>
                        <form method="post" action="${pageContext.request.contextPath}/clients/${c.id}/delete" style="display:inline" onsubmit="return confirm('Удалить клиента?')">
                            <button class="btn btn-sm btn-danger">🗑️</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>