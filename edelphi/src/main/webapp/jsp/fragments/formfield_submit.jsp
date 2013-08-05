<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="classes">formField formSubmit</c:set>
<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes">${classes} ${param.classes}</c:set>
  </c:when>
</c:choose>

<div class="formFieldContainer formSubmitContainer">
  <c:choose>
    <c:when test="${param.labelLocale != null}">
       <c:set var="label"><fmt:message key="${param.labelLocale}"/></c:set>
    </c:when>
    <c:otherwise>
       <c:set var="label">${param.labelText}</c:set>
    </c:otherwise>
  </c:choose>
  
  <c:choose>
    <c:when test="${param.titleLocale != null}">
      <c:set var="title"><fmt:message key="${param.titleLocale}"/></c:set>
    </c:when>
    <c:otherwise>
      <c:set var="title">${param.title}</c:set>
    </c:otherwise>
  </c:choose>
  
  <c:choose>
    <c:when test="${param.disabled eq 'true'}">
      <input type="submit" id="${param.id}" name="${param.name}" class="${classes}" value="${label}" title="${title}" disabled="disabled"/>
    </c:when>
    <c:otherwise>
      <input type="submit" id="${param.id}" name="${param.name}" class="${classes}" value="${label}" title="${title}"/>
    </c:otherwise>
  </c:choose>
  
</div>