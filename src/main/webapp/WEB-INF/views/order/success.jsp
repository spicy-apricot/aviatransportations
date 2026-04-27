<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Заказ оформлен - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <div class="row justify-content-center mt-5">
        <div class="col-lg-6 text-center">
            <!-- Success Icon -->
            <div class="mb-4">
                <div style="width: 120px; height: 120px; margin: 0 auto; border-radius: 50%;
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            display: flex; align-items: center; justify-content: center;">
                    <span style="font-size: 60px; color: white;">✓</span>
                </div>
            </div>

            <h2 class="mb-3">🎉 Заказ успешно оформлен!</h2>
            <p class="lead text-muted mb-4">Ваш билет отправлен на указанный email</p>

            <!-- Order Details -->
            <div class="card shadow mb-4 text-start">
                <div class="card-header bg-light">
                    <strong>Номер заказа:</strong> <code class="fs-6">${orderId}</code>
                </div>
                <div class="card-body">
                    <dl class="row mb-0">
                        <dt class="col-sm-5">Рейс</dt>
                        <dd class="col-sm-7">#${param.flightId}</dd>

                        <dt class="col-sm-5">Место</dt>
                        <dd class="col-sm-7"><span class="badge bg-secondary">${param.seat}</span></dd>

                        <dt class="col-sm-5">Пассажир</dt>
                        <dd class="col-sm-7">${param.clientName}</dd>

                        <dt class="col-sm-5">Оплачено</dt>
                        <dd class="col-sm-7 text-success fw-bold">${param.flightCost} ₽</dd>
                    </dl>
                </div>
            </div>

            <!-- Actions -->
            <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary btn-lg px-4">
                    🏠 На главную
                </a>
                <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary btn-lg px-4">
                    👥 Мои билеты
                </a>
            </div>

            <!-- Info -->
            <div class="alert alert-info mt-4 text-start">
                <h6 class="alert-heading">📧 Что дальше?</h6>
                <ul class="mb-0 small">
                    <li>Проверьте email — электронный билет придёт в течение 5 минут</li>
                    <li>Сохраните номер заказа: <strong>${orderId}</strong></li>
                    <li>Регистрация на рейс открывается за 24 часа до вылета</li>
                </ul>
            </div>
        </div>
    </div>
</div>

<%@ include file="../layout/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>