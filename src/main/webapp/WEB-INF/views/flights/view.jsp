<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/flights">Рейсы</a></li>
            <li class="breadcrumb-item active">#${flight.flightID}</li>
        </ol>
    </nav>

    <div class="row">
        <!-- Flight Details -->
        <div class="col-lg-8">
            <div class="card shadow mb-4">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                    <h4 class="mb-0">✈️ Рейс #${flight.flightID}</h4>
                    <a href="${pageContext.request.contextPath}/flights/${flight.flightID}/edit" class="btn btn-sm btn-light">✏️</a>
                </div>
                <div class="card-body">
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <h5>🛫 Вылет</h5>
                            <p class="mb-1"><strong>${flight.departureAirport.city}</strong></p>
                            <p class="text-muted mb-1">${flight.departureAirport.name}</p>
                            <p class="mb-0">
                                <span class="badge bg-primary fs-6">${flight.departureDate}</span>
                                <span class="badge bg-secondary fs-6">${flight.departureTime}</span>
                            </p>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <h5>🛬 Прилёт</h5>
                            <p class="mb-1"><strong>${flight.arrivalAirport.city}</strong></p>
                            <p class="text-muted mb-1">${flight.arrivalAirport.name}</p>
                            <p class="mb-0">
                                <span class="badge bg-success fs-6">${flight.arrivalDate}</span>
                                <span class="badge bg-secondary fs-6">${flight.arrivalTime}</span>
                            </p>
                        </div>
                    </div>

                    <hr>

                    <div class="row">
                        <div class="col-md-4">
                            <strong>Авиакомпания</strong><br>
                            ${flight.airline.name}
                        </div>
                        <div class="col-md-4">
                            <strong>Стоимость</strong><br>
                            <span class="fs-5 text-success fw-bold">${flight.cost} ₽</span>
                        </div>
                        <div class="col-md-4">
                            <strong>Самолёт</strong><br>
                            ${flight.aircraftType}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Available Seats -->
            <div class="card shadow">
                <div class="card-header">
                    <h5 class="mb-0">💺 Доступные места</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${fn:length(availableSeats) > 0}">
                            <p class="text-muted mb-3">Выберите место для бронирования:</p>
                            <div class="d-flex flex-wrap gap-2">
                                <c:forEach var="seat" items="${availableSeats}">
                                    <a href="${pageContext.request.contextPath}/flights/${flight.flightID}/buy?seat=${seat}"
                                       class="btn btn-outline-success btn-sm" style="min-width: 50px;">
                                        ${seat}
                                    </a>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-danger">
                                <strong>⚠️ Мест нет</strong><br>
                                На этот рейс все места уже забронированы.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <!-- Quick Actions -->
            <div class="card shadow mb-4">
                <div class="card-header bg-info text-white">⚡ Быстрые действия</div>
                <div class="card-body">
                    <div class="d-grid gap-2">
                        <a href="${pageContext.request.contextPath}/flights/${flight.flightID}/edit"
                           class="btn btn-outline-primary">✏️ Редактировать рейс</a>
                        <form method="post" action="${pageContext.request.contextPath}/flights/${flight.flightID}/delete"
                              onsubmit="return confirm('Удалить рейс #${flight.flightID}?')">
                            <button class="btn btn-outline-danger w-100">🗑️ Удалить рейс</button>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Flight Info -->
            <div class="card shadow">
                <div class="card-header">📋 Детали</div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-6">ID рейса</dt>
                        <dd class="col-6"><code>${flight.flightID}</code></dd>

                        <dt class="col-6">Длительность</dt>
                        <dd class="col-6">${flight.flightDuration} мин</dd>

                        <dt class="col-6">Всего мест</dt>
                        <dd class="col-6">${flight.totalSeats}</dd>

                        <dt class="col-6">Свободно</dt>
                        <dd class="col-6">
                            <c:choose>
                                <c:when test="${fn:length(availableSeats) > 0}">
                                    <span class="text-success">${fn:length(availableSeats)}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-danger">0</span>
                                </c:otherwise>
                            </c:choose>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>