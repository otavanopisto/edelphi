<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="pageTitle">
  <h1>
    <fmt:message key="${param.titleLocale}">
      <c:if test="${not empty param.titleLocaleParam}">
        <fmt:param>${param.titleLocaleParam}</fmt:param>
      </c:if>
    </fmt:message>
  </h1>
</div>
