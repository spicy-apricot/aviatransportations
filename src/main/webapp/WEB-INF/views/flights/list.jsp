<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Рейсы - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>✈️ Рейсы</h2>
        <a href="${pageContext.request.contextPath}/flights/new" class="btn btn-success">+ Создать рейс</a>
    </div>

    <!-- Filters -->
    <form method="get" class="card mb-4">
        <div class="card-body">
            <div class="row g-3">
                <div class="col-md-3">
                    <input type="text" name="filter" class="form-control"
                           placeholder="Поиск по номеру или аэропорту" value="${filter}">
                </div>
                <div class="col-md-2">
                    <input type="number" name="minCost" class="form-control"
                           placeholder="Цена от" value="${minCost}" min="0">
                </div>
                <div class="col-md-2">
                    <input type="number" name="maxCost" class="form-control"
                           placeholder="Цена до" value="${maxCost}" min="0">
                </div>
                <div class="col-md-2">
                    <select name="airlineId" class="form-select">
                        <option value="">Все авиакомпании</option>
                        <c:forEach var="a" items="${airlines}">
                            <option value="${a.id}" ${airlineId == a.id ? 'selected' : ''}>${a.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1">
                    <button type="submit" class="btn btn-primary w-100">🔍</button>
                </div>
                <div class="col-md-2">
                    <a href="${pageContext.request.contextPath}/flights" class="btn btn-outline-secondary w-100">Сбросить</a>
                </div>
            </div>
        </div>
    </form>

    <!-- Flights Table -->
    <c:choose>
        <c:when test="${not empty flights}">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>№</th>
                            <th>Маршрут</th>
                            <th>Дата/Время</th>
                            <th>Авиакомпания</th>
                            <th>Цена</th>
                            <th>Места</th>
                            <th class="text-end">Действия</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="f" items="${flights}">
                            <tr>
                                <td><strong>#${f.flightID}</strong></td>
                                <td>
                                    <div>
                                        <span class="text-primary">${f.departureAirport.city}</span>
                                        <span class="mx-1">→</span>
                                        <span class="text-success">${f.arrivalAirport.city}</span>
                                    </div>
                                    <small class="text-muted">
                                        ${f.departureAirport.name} → ${f.arrivalAirport.name}
                                    </small>
                                </td>
                                <td>
                                    <div>${f.departureDate}</div>
                                    <small class="text-muted">${f.departureTime}</small>
                                </td>
                                <td>${f.airline.name}</td>
                                <td class="fw-bold text-success">${f.cost} ₽</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${fn:length(f.availableSeats) > 0}">
                                            <span class="badge bg-success">${fn:length(f.availableSeats)} мест</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">Нет мест</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-end">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/flights/${f.flightID}"
                                           class="btn btn-outline-primary">👁️</a>
                                        <a href="${pageContext.request.contextPath}/flights/${f.flightID}/edit"
                                           class="btn btn-outline-warning">✏️</a>
                                        <form method="post" action="${pageContext.request.contextPath}/flights/${f.flightID}/delete"
                                              style="display:inline" onsubmit="return confirm('Удалить рейс #${f.flightID}?')">
                                            <button type="submit" class="btn btn-outline-danger">🗑️</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info text-center">
                <p class="mb-0">Рейсы не найдены. <a href="${pageContext.request.contextPath}/flights/new">Создать первый рейс</a></p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>