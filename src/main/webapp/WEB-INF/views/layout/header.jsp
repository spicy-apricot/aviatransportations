<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
  <div class="container">
    <a class="navbar-brand" href="${pageContext.request.contextPath}/">AviaTransport</a>
    <div class="navbar-nav">
      <a class="nav-link" href="${pageContext.request.contextPath}/">Главная</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/clients">Клиенты</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/flights">Рейсы</a>
    </div>
  </div>
</nav>
