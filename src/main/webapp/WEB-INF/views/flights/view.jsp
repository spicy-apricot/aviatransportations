<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Рейс #${flight.flightID} - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Рейс #${flight.flightID}</h2>
        <a href="${pageContext.request.contextPath}/flights/${flight.flightID}/edit" class="btn btn-outline-warning">Редактировать</a>
    </div>
    <div class="card mb-4">
        <div class="card-body">
            <h4>${flight.departureAirport.city} - ${flight.arrivalAirport.city}</h4>
            <p class="mb-1">Вылет: ${flight.departureDate} ${flight.departureTime}, ${flight.departureAirport.name}</p>
            <p class="mb-1">Прилет: ${flight.arrivalDate} ${flight.arrivalTime}, ${flight.arrivalAirport.name}</p>
            <p class="mb-1">Авиакомпания: ${flight.airline.name}</p>
            <p class="mb-0">Стоимость: <strong>${flight.cost} руб.</strong></p>
        </div>
    </div>
    <h4>Доступные места</h4>
    <c:choose>
        <c:when test="${not empty availableSeats}">
            <div class="d-flex flex-wrap gap-2">
                <c:forEach var="seat" items="${availableSeats}">
                    <a href="${pageContext.request.contextPath}/flights/${flight.flightID}/buy?seat=${seat}" class="btn btn-outline-success">Купить ${seat}</a>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise><div class="alert alert-warning">Свободных мест нет.</div></c:otherwise>
    </c:choose>
</div>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
