<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="classes">formField formCheckBox</c:set>
<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes">${classes} ${param.classes}</c:set>
  </c:when>
</c:choose>

<div class="formFieldContainer formCheckBoxContainer">
  <c:choose>
    <c:when test="${param.labelLocale != null}">
       <c:set var="label"><fmt:message key="${param.labelLocale}"/></c:set>
    </c:when>
    <c:otherwise>
       <c:set var="label">${param.labelText}</c:set>
    </c:otherwise>
  </c:choose>
  <c:set var="_checked">
    <c:if test="${param.checked eq 'true'}">checked</c:if>
  </c:set>
  <input type="checkbox" id="${param.name}${param.value}" name="${param.name}" value="${param.value}" ${_checked}/><label for="${param.name}${param.value}">${label}</label><br/>
</div>