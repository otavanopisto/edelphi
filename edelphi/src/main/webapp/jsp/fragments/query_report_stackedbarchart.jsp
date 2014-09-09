<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryLiveReportContainer queryStackedBarChartLiveReportContainer">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.answersTitle"></fmt:message></h2>
  <div class="queryStackedBarChartLiveReportFlotrContainer"></div>
  
  <input type="hidden" value="${param.title}" name="title"/>
  
  <c:if test="${param.itemCount gt 0}">
    <c:forEach begin="0" end="${param.itemCount - 1}" varStatus="vs">
      <c:set value="item.${vs.index}.label" var="labelVar"/>
      <c:set value="item.${vs.index}.values" var="valuesVar"/>
   
      <input type="hidden" class="reportItemLabel" value="${param[labelVar]}"/>
      <input type="hidden" class="reportItemValues" value="${param[valuesVar]}"/>
      
    </c:forEach>
  </c:if>
  
</div>