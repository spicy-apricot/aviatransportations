<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>Оформление заказа</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <h2>🎫 Оформление билета</h2>

    <div class="card mb-4">
        <div class="card-header">Информация о рейсе</div>
        <div class="card-body">
            <p><strong>Рейс №${flight.flightID}</strong>: ${flight.departureAirport.city} → ${flight.arrivalAirport.city}</p>
            <p><strong>Дата:</strong> ${flight.departureDate} в ${flight.departureTime}</p>
            <p><strong>Место:</strong> ${seat}</p>
            <p><strong>Стоимость:</strong> ${flight.cost} ₽</p>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/order/confirm">
        <input type="hidden" name="flightId" value="${flight.flightID}">
        <input type="hidden" name="seat" value="${seat}">

        <div class="mb-3">
            <label class="form-label">ФИО пассажира *</label>
            <input type="text" name="clientName" class="form-control" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Телефон *</label>
            <input type="tel" name="clientPhone" class="form-control" pattern="[0-9+\-\s()]{10,}" required>
        </div>

        <button type="submit" class="btn btn-primary">Перейти к оплате →</button>
        <a href="${pageContext.request.contextPath}/flights/${flight.flightID}" class="btn btn-secondary">Отмена</a>
    </form>
</div>
</body>
</html>