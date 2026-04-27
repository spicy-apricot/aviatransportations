<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <title>AviaTransport - Главная</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="layout/header.jsp" %>
<div class="container">
    <div class="p-5 mb-4 bg-light rounded-3">
        <h1 class="display-5 fw-bold">Добро пожаловать в AviaTransport!</h1>
        <p class="col-md-8 fs-4">Найдите и забронируйте авиабилеты на лучшие рейсы по выгодным ценам.</p>
    </div>

    <div class="card mb-4">
        <div class="card-header">🔍 Поиск рейсов</div>
        <div class="card-body">
            <form method="get" action="${pageContext.request.contextPath}/" class="row g-3">
                <div class="col-md-3">
                    <input type="text" class="form-control" name="from" placeholder="Откуда" value="${searchFrom}">
                </div>
                <div class="col-md-3">
                    <input type="text" class="form-control" name="to" placeholder="Куда" value="${searchTo}">
                </div>
                <div class="col-md-2">
                    <input type="date" class="form-control" name="date" value="${searchDate}">
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100">Найти</button>
                </div>
            </form>
        </div>
    </div>

    <c:if test="${not empty flights}">
        <h3>Найдено рейсов: ${fn:length(flights)}</h3>
        <table class="table table-striped">
            <thead>
                <tr>
                    <th>№</th><th>Откуда</th><th>Куда</th><th>Дата</th><th>Цена</th><th>Места</th><th>Действия</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="f" items="${flights}">
                    <tr>
                        <td>${f.flightID}</td>
                        <td>${f.departureAirport.city} (${f.departureAirport.name})</td>
                        <td>${f.arrivalAirport.city} (${f.arrivalAirport.name})</td>
                        <td>${f.departureDate} ${f.departureTime}</td>
                        <td>${f.cost} ₽</td>
                        <td>${fn:length(f.availableSeats) > 0 ? 'Есть' : 'Нет'}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/flights/${f.flightID}" class="btn btn-sm btn-outline-primary">Подробнее</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>