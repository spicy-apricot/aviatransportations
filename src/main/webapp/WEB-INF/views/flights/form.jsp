<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/flights">Рейсы</a></li>
            <li class="breadcrumb-item active">${flight.flightID == null ? 'Создание' : 'Редактирование'}</li>
        </ol>
    </nav>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">${flight.flightID == null ? '➕ Создать рейс' : '✏️ Редактировать рейс'}</h4>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/flights" id="flightForm">
                        <c:if test="${flight.flightID != null}">
                            <input type="hidden" name="flightID" value="${flight.flightID}">
                        </c:if>

                        <!-- Route Section -->
                        <h5 class="border-bottom pb-2 mb-3">🗺️ Маршрут</h5>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Аэропорт вылета *</label>
                                <select name="departureAirport.id" class="form-select" required>
                                    <option value="">Выберите аэропорт</option>
                                    <c:forEach var="a" items="${airports}">
                                        <option value="${a.id}" ${flight.departureAirport?.id == a.id ? 'selected' : ''}>
                                            ${a.city} — ${a.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Аэропорт прибытия *</label>
                                <select name="arrivalAirport.id" class="form-select" required>
                                    <option value="">Выберите аэропорт</option>
                                    <c:forEach var="a" items="${airports}">
                                        <option value="${a.id}" ${flight.arrivalAirport?.id == a.id ? 'selected' : ''}>
                                            ${a.city} — ${a.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- Schedule Section -->
                        <h5 class="border-bottom pb-2 mb-3">📅 Расписание</h5>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Дата вылета *</label>
                                <input type="date" class="form-control" name="departureDate"
                                       value="${flight.departureDate}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Время вылета *</label>
                                <input type="time" class="form-control" name="departureTime"
                                       value="${flight.departureTime}" required>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Дата прибытия *</label>
                                <input type="date" class="form-control" name="arrivalDate"
                                       value="${flight.arrivalDate}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Время прибытия *</label>
                                <input type="time" class="form-control" name="arrivalTime"
                                       value="${flight.arrivalTime}" required>
                            </div>
                        </div>

                        <!-- Flight Details -->
                        <h5 class="border-bottom pb-2 mb-3">✈️ Детали рейса</h5>
                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Авиакомпания *</label>
                                <select name="airline.id" class="form-select" required>
                                    <option value="">Выберите авиакомпанию</option>
                                    <c:forEach var="al" items="${airlines}">
                                        <option value="${al.id}" ${flight.airline?.id == al.id ? 'selected' : ''}>
                                            ${al.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Тип самолёта</label>
                                <input type="text" class="form-control" name="aircraftType"
                                       value="${flight.aircraftType}" placeholder="Boeing 737">
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label class="form-label">Стоимость (₽) *</label>
                                <input type="number" class="form-control" name="cost"
                                       value="${flight.cost}" required min="100" step="100">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Всего мест *</label>
                                <input type="number" class="form-control" name="totalSeats"
                                       value="${flight.totalSeats}" required min="1" max="500">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Длительность (мин)</label>
                                <input type="number" class="form-control" name="flightDuration"
                                       value="${flight.flightDuration}" min="10" max="1440">
                            </div>
                        </div>

                        <!-- Available Seats -->
                        <div class="mb-4">
                            <label class="form-label">Доступные места (через запятую)</label>
                            <input type="text" class="form-control" name="availableSeats"
                                   value="${flight.availableSeats}"
                                   placeholder="1A,1B,2A,2B,3C">
                            <div class="form-text">Оставьте пустым для автоматического заполнения</div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary flex-grow-1">💾 Сохранить рейс</button>
                            <a href="${pageContext.request.contextPath}/flights" class="btn btn-outline-secondary">Отмена</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
// Валидация: дата прибытия не раньше вылета
document.getElementById('flightForm').addEventListener('submit', function(e) {
    const depDate = new Date(document.querySelector('[name="departureDate"]').value);
    const arrDate = new Date(document.querySelector('[name="arrivalDate"]').value);
    if (arrDate < depDate) {
        e.preventDefault();
        alert('Дата прибытия не может быть раньше даты вылета!');
    }
});
</script>
</body>
</html>