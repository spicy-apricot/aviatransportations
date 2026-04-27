<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${client.fullName} - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <!-- Breadcrumb -->
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/clients">Клиенты</a></li>
            <li class="breadcrumb-item active">${client.fullName}</li>
        </ol>
    </nav>

    <div class="row">
        <!-- Client Info Card -->
        <div class="col-lg-4 mb-4">
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">👤 Информация</h5>
                    <a href="${pageContext.request.contextPath}/clients/${client.id}/edit" class="btn btn-sm btn-light">✏️</a>
                </div>
                <div class="card-body">
                    <h4 class="card-title">${client.fullName}</h4>
                    <hr>
                    <dl class="row mb-0">
                        <dt class="col-sm-4">Телефон</dt>
                        <dd class="col-sm-8"><a href="tel:${client.phone}">${client.phone}</a></dd>

                        <dt class="col-sm-4">Email</dt>
                        <dd class="col-sm-8"><a href="mailto:${client.email}">${client.email}</a></dd>

                        <dt class="col-sm-4">ID</dt>
                        <dd class="col-sm-8"><code>${client.id}</code></dd>
                    </dl>
                </div>
                <div class="card-footer">
                    <form method="post" action="${pageContext.request.contextPath}/clients/${client.id}/delete"
                          onsubmit="return confirm('Удалить клиента ${client.fullName}?')">
                        <button class="btn btn-danger w-100">🗑️ Удалить клиента</button>
                    </form>
                </div>
            </div>

            <!-- Bonus Cards -->
            <div class="card shadow-sm mt-3">
                <div class="card-header bg-warning">⭐ Бонусные карты</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty client.bonusCards}">
                            <c:forEach var="bc" items="${client.bonusCards}">
                                <div class="d-flex justify-content-between align-items-center mb-2 p-2 border rounded">
                                    <div>
                                        <strong>${bc.airline.name}</strong><br>
                                        <small class="text-muted">ID: ${bc.id}</small>
                                    </div>
                                    <span class="badge bg-warning text-dark fs-6">${bc.points} ✦</span>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Бонусных карт нет</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Tickets History -->
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-header bg-info text-white">
                    <h5 class="mb-0">🎫 История билетов</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty client.tickets}">
                            <div class="table-responsive">
                                <table class="table table-sm">
                                    <thead>
                                        <tr>
                                            <th>Рейс</th>
                                            <th>Маршрут</th>
                                            <th>Дата</th>
                                            <th>Место</th>
                                            <th>Статус</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="t" items="${client.tickets}">
                                            <tr>
                                                <td><strong>#${t.flight.flightID}</strong></td>
                                                <td>
                                                    <small>${t.flight.departureAirport.city}</small> →
                                                    <small>${t.flight.arrivalAirport.city}</small>
                                                </td>
                                                <td>${t.flight.departureDate}</td>
                                                <td><span class="badge bg-secondary">${t.seatNumber}</span></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${t.paid}">
                                                            <span class="badge bg-success">Оплачен</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-warning text-dark">Ожидает</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">У клиента ещё нет билетов</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Traveled Stats -->
            <div class="card shadow-sm mt-3">
                <div class="card-header">📊 Статистика перелётов</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty traveled}">
                            <c:forEach var="t" items="${traveled}">
                                <div class="d-flex justify-content-between mb-2">
                                    <span>${t.airline.name}</span>
                                    <strong>${t.distance} км</strong>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted mb-0">Статистика пока не доступна</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>