<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="label"><fmt:message key="${param.label}"/></c:set>


  <c:choose>
    <c:when test="${param.hidden eq 'true'}">
      <c:set var="classes" value="blockContextualLink blockContextualLinkHidden ${param.action}"/>
    </c:when>
    <c:when test="${param.disabled eq 'true'}">
      <c:set var="classes" value="blockContextualLink blockContextualLinkDisabled ${param.action}"/>
    </c:when>
    <c:otherwise>
      <c:set var="classes" value="blockContextualLink ${param.action}"/>
    </c:otherwise>
  </c:choose>
  
  <div class="${classes}">
    <a href="${param.disabled eq 'true' ? 'javascript:null(void);' : param.href}" onclick="${param.disabled eq 'true' ? '' : param.onclick}" title="${label}" target="${param.target}">
      <span class="blockContextualLinkTooltip">
        <span class="blockContextualLinkTooltipText">${label}</span>
        <span class="blockContextualLinkTooltipArrow"></span>
      </span>
    </a>
  </div>

