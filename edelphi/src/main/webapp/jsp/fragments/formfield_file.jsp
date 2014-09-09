<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="classes">formField formFileField</c:set>
<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes">${classes} ${param.classes}</c:set>
  </c:when>
</c:choose>

  

<div class="formFieldContainer formTextFieldContainer">
  <c:choose>
    <c:when test="${param.labelLocale != null}">
       <label class="formFieldLabel" for="${param.name}"><fmt:message key="${param.labelLocale}"/></label>
    </c:when>
    <c:otherwise>
      <label class="formFieldLabel" for="${param.name}">${param.labelText}</label>
    </c:otherwise>
  </c:choose>
  <input type="file" name="${param.name}" class="${classes}" value="${param.value}"/>
</div>