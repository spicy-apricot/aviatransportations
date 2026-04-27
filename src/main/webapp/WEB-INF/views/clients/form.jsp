<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${client.id == null ? 'Создание' : 'Редактирование'} клиента - AviaTransport</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%@ include file="../layout/header.jsp" %>

<div class="container">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/clients">Клиенты</a></li>
            <li class="breadcrumb-item active">${client.id == null ? 'Создание' : 'Редактирование'}</li>
        </ol>
    </nav>

    <div class="row justify-content-center">
        <div class="col-lg-6">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">${client.id == null ? '➕ Создать клиента' : '✏️ Редактировать клиента'}</h4>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/clients" id="clientForm">
                        <c:if test="${client.id != null}">
                            <input type="hidden" name="id" value="${client.id}">
                        </c:if>

                        <div class="mb-3">
                            <label for="fullName" class="form-label">ФИО *</label>
                            <input type="text" class="form-control" id="fullName" name="fullName"
                                   value="${client.fullName}" required minlength="3" maxlength="100"
                                   placeholder="Иванов Иван Иванович">
                            <div class="invalid-feedback">Введите ФИО (мин. 3 символа)</div>
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Телефон *</label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   value="${client.phone}" required pattern="[0-9+\-\s()]{10,}"
                                   placeholder="+7 (999) 123-45-67">
                            <div class="invalid-feedback">Введите корректный номер телефона</div>
                        </div>

                        <div class="mb-4">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="${client.email}" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                                   placeholder="example@email.com">
                            <div class="invalid-feedback">Введите корректный email</div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary flex-grow-1">💾 Сохранить</button>
                            <a href="${pageContext.request.contextPath}/clients" class="btn btn-outline-secondary">Отмена</a>
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
// Простая валидация на клиенте
document.getElementById('clientForm').addEventListener('submit', function(e) {
    const phone = document.getElementById('phone').value;
    const digits = phone.replace(/\D/g, '');
    if (digits.length < 10) {
        e.preventDefault();
        document.getElementById('phone').classList.add('is-invalid');
        alert('Номер телефона должен содержать не менее 10 цифр');
    }
});
</script>
</body>
</html>