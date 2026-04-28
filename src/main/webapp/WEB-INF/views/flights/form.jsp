<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${flight.flightID == null ? 'Создание' : 'Редактирование'} рейса - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>
<div class="container">
    <h2 class="mb-3">${flight.flightID == null ? 'Создание рейса' : 'Редактирование рейса'}</h2>
    <form method="post" action="${pageContext.request.contextPath}/flights" class="col-lg-8">
        <c:if test="${flight.flightID != null}">
            <input type="hidden" name="flightID" value="${flight.flightID}">
        </c:if>
        <div class="row mb-3">
            <div class="col-md-6">
                <label class="form-label">Аэропорт вылета</label>
                <select name="departureAirportId" class="form-select" required>
                    <c:forEach var="airport" items="${airports}">
                        <option value="${airport.airportID}" ${flight.departureAirport != null && flight.departureAirport.airportID == airport.airportID ? 'selected' : ''}>${airport.city}, ${airport.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-6">
                <label class="form-label">Аэропорт прибытия</label>
                <select name="arrivalAirportId" class="form-select" required>
                    <c:forEach var="airport" items="${airports}">
                        <option value="${airport.airportID}" ${flight.arrivalAirport != null && flight.arrivalAirport.airportID == airport.airportID ? 'selected' : ''}>${airport.city}, ${airport.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label">Авиакомпания</label>
            <select name="airlineId" class="form-select" required>
                <c:forEach var="airline" items="${airlines}">
                    <option value="${airline.airlineID}" ${flight.airline != null && flight.airline.airlineID == airline.airlineID ? 'selected' : ''}>${airline.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="row mb-3">
            <div class="col-md-3">
                <label class="form-label">Дата вылета</label>
                <input type="date" class="form-control" name="departureDate" value="${flight.departureDate}" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Время вылета</label>
                <input type="time" class="form-control" name="departureTime" value="${flight.departureTime}" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Дата прилета</label>
                <input type="date" class="form-control" name="arrivalDate" value="${flight.arrivalDate}" required>
            </div>
            <div class="col-md-3">
                <label class="form-label">Время прилета</label>
                <input type="time" class="form-control" name="arrivalTime" value="${flight.arrivalTime}" required>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-4">
                <label class="form-label">Стоимость</label>
                <input type="number" class="form-control" name="cost" value="${flight.cost}" min="1" required>
            </div>
            <div class="col-md-8">
                <label class="form-label">Свободные места</label>
                <input type="text" class="form-control" name="availableSeats" value="${flight.availableSeats}" placeholder="1A,1B,2A">
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Сохранить</button>
        <a href="${pageContext.request.contextPath}/flights" class="btn btn-outline-secondary">Отмена</a>
    </form>
</div>
<%@ include file="../layout/footer.jsp" %>
</body>
</html>
