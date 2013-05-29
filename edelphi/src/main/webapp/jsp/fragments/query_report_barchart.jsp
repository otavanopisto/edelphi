<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryLiveReportContainer queryBarChartLiveReportContainer">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.answersTitle"></fmt:message></h2>
  <div class="queryBarChartLiveReportFlotrContainer"></div>
  
  <input type="hidden" value="${param.axisLabel}" name="axisLabel"/>
  
  <c:if test="${param.valueCount gt 0}">
    <c:forEach begin="0" end="${param.valueCount - 1}" varStatus="vs">
      <c:set value="value.${vs.index}" var="value"/>
      <c:set value="name.${vs.index}" var="name"/>
      <c:if test="${!empty(param[name])}">
        <input type="hidden" value="${param[value]}" name="${param[name]}" class="reportValue"/>
      </c:if>
    </c:forEach>
  </c:if>
  
</div>