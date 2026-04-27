<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Ошибка оплаты - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <div class="row justify-content-center mt-5">
        <div class="col-lg-6 text-center">
            <!-- Error Icon -->
            <div class="mb-4">
                <div style="width: 120px; height: 120px; margin: 0 auto; border-radius: 50%;
                            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
                            display: flex; align-items: center; justify-content: center;">
                    <span style="font-size: 60px; color: white;">✕</span>
                </div>
            </div>

            <h2 class="mb-3">😔 Ошибка оплаты</h2>
            <p class="lead text-muted mb-4">${error != null ? error : 'Не удалось обработать платёж'}</p>

            <!-- Order Details (for retry) -->
            <c:if test="${param.flightId != null}">
                <div class="card shadow mb-4 text-start">
                    <div class="card-body">
                        <h6 class="card-title">Детали заказа</h6>
                        <dl class="row mb-0 small">
                            <dt class="col-sm-5">Рейс</dt>
                            <dd class="col-sm-7">#${param.flightId}</dd>
                            <dt class="col-sm-5">Место</dt>
                            <dd class="col-sm-7">${param.seat}</dd>
                            <dt class="col-sm-5">Сумма</dt>
                            <dd class="col-sm-7">${param.flightCost} ₽</dd>
                        </dl>
                    </div>
                </div>
            </c:if>

            <!-- Actions -->
            <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <c:choose>
                    <c:when test="${param.flightId != null}">
                        <a href="${pageContext.request.contextPath}/flights/${param.flightId}/buy?seat=${param.seat}"
                           class="btn btn-primary btn-lg px-4">
                            🔄 Повторить оплату
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/flights"
                           class="btn btn-primary btn-lg px-4">
                            ✈️ Выбрать другой рейс
                        </a>
                    </c:otherwise>
                </c:choose>
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-lg px-4">
                    🏠 На главную
                </a>
            </div>

            <!-- Help -->
            <div class="alert alert-warning mt-4 text-start">
                <h6 class="alert-heading">💡 Возможные причины:</h6>
                <ul class="mb-0 small">
                    <li>Недостаточно средств на карте</li>
                    <li>Неверные данные карты (номер, срок, CVV)</li>
                    <li>Технические проблемы платёжного шлюза</li>
                    <li>Место уже забронировано другим пассажиром</li>
                </ul>
            </div>

            <p class="text-muted small">
                Если проблема сохраняется, свяжитесь с поддержкой:
                <a href="mailto:support@aviatransport.ru">support@aviatransport.ru</a>
            </p>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>