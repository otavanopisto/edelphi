<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="classes">formField formSelect</c:set>
<c:choose>
  <c:when test="${!empty(param.classes)}">
    <c:set var="classes">${classes} ${param.classes}</c:set>
  </c:when>
</c:choose>

<div class="formFieldContainer formSelectContainer">
  <c:choose>
    <c:when test="${param.labelLocale != null}">
      <label class="formFieldLabel" for="${param.id}"><fmt:message key="${param.labelLocale}"/></label>
    </c:when>
    <c:otherwise>
      <label class="formFieldLabel" for="${param.id}">${param.labelText}</label>
    </c:otherwise>
  </c:choose>
  <select name="${param.name}">
    <c:forEach var="_option" items="${param.options}">
      <c:set var="_selected">
        <c:if test="${_option eq param.value}">selected</c:if>
      </c:set>
      <c:set var="_title" value="option.${_option}"/>
      <option value="${_option}" ${_selected}>${param[_title]}</option>
    </c:forEach> 
  </select>
</div>