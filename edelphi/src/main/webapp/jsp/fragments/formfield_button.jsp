<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="classes">formField formButton</c:set>
<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes">${classes} ${param.classes}</c:set>
  </c:when>
</c:choose>

<div class="formFieldContainer formButtonContainer">
  <c:choose>
    <c:when test="${param.labelLocale != null}">
       <c:set var="label"><fmt:message key="${param.labelLocale}"/></c:set>
    </c:when>
    <c:otherwise>
       <c:set var="label">${param.labelText}</c:set>
    </c:otherwise>
  </c:choose>
  
  <c:choose>
    <c:when test="${param.disabled eq 'true'}">
      <input type="button" name="${param.name}" class="${classes}" value="${label}" disabled="disabled"/>
    </c:when>
    <c:otherwise>
      <input type="button" name="${param.name}" class="${classes}" value="${label}"/>
    </c:otherwise>
  </c:choose>
  
</div>