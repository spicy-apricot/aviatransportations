<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Оплата - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-lg-6">
            <div class="card shadow">
                <div class="card-header bg-success text-white text-center">
                    <h4 class="mb-0">💳 Оплата заказа</h4>
                </div>
                <div class="card-body">
                    <!-- Order Recap -->
                    <div class="alert alert-light border mb-4">
                        <p class="mb-1"><strong>Рейс:</strong> #${flightId}</p>
                        <p class="mb-1"><strong>Место:</strong> ${seat}</p>
                        <p class="mb-1"><strong>Пассажир:</strong> ${clientName}</p>
                        <hr class="my-2">
                        <p class="mb-0 fs-5">К оплате: <strong class="text-success">${param.flightCost} ₽</strong></p>
                    </div>

                    <!-- Bonus Cards -->
                    <c:if test="${not empty bonusCards}">
                        <div class="mb-4">
                            <label class="form-label">⭐ Применить бонусную карту</label>
                            <select name="bonusCardId" class="form-select">
                                <option value="">Не использовать</option>
                                <c:forEach var="bc" items="${bonusCards}">
                                    <option value="${bc.id}">
                                        ${bc.airline.name} — ${bc.points} ✦
                                        (скидка до ${bc.points * 10} ₽)
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </c:if>

                    <!-- Payment Form -->
                    <form method="post" action="${pageContext.request.contextPath}/order/pay" id="paymentForm">
                        <input type="hidden" name="flightId" value="${flightId}">
                        <input type="hidden" name="seat" value="${seat}">
                        <input type="hidden" name="clientName" value="${clientName}">
                        <input type="hidden" name="clientPhone" value="${clientPhone}">
                        <input type="hidden" name="flightCost" value="${param.flightCost}">

                        <div class="mb-3">
                            <label class="form-label">Номер карты</label>
                            <input type="text" class="form-control" name="cardNumber"
                                   placeholder="0000 0000 0000 0000" required
                                   pattern="[0-9\s]{16,19}">
                        </div>
                        <div class="row mb-3">
                            <div class="col-6">
                                <label class="form-label">Срок действия</label>
                                <input type="text" class="form-control" name="cardExpiry"
                                       placeholder="MM/YY" required pattern="(0[1-9]|1[0-2])\/[0-9]{2}">
                            </div>
                            <div class="col-6">
                                <label class="form-label">CVV</label>
                                <input type="text" class="form-control" name="cardCvv"
                                       placeholder="123" required pattern="[0-9]{3,4}" maxlength="4">
                            </div>
                        </div>

                        <div class="form-check mb-4">
                            <input class="form-check-input" type="checkbox" id="agree" required>
                            <label class="form-check-label" for="agree">
                                Я согласен с <a href="#" target="_blank">условиями оплаты</a>
                            </label>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success btn-lg">
                                💰 Оплатить ${param.flightCost} ₽
                            </button>
                            <a href="${pageContext.request.contextPath}/flights/${flightId}"
                               class="btn btn-outline-secondary">Отмена</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Security Notice -->
            <div class="text-center mt-3 text-muted small">
                🔒 Платёж защищён протоколом SSL. Данные карты не сохраняются.
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>