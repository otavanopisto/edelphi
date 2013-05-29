<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryLiveReportContainer queryLineChartLiveReportContainer">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.answersTitle"></fmt:message></h2>
  <div class="queryLineChartLiveReportFlotrContainer"></div>
  
  <input type="hidden" name="title" value="${param.title}"/>
  <input type="hidden" name="userDataSetLabel" value="${param.userDataSetLabel}"/>
  <input type="hidden" name="seriesCount" value="${param['serie.count']}"/>
  <input type="hidden" name="replyCount" value="${param['replyCount']}"/>
  
  <c:set var="tickLabelCount" value="${param['tickLabelCount']}"/>
  
  <input type="hidden" name="tickLabelCount" value="${tickLabelCount}"/>
  <c:if test="${tickLabelCount gt 0}">
    <c:forEach begin="0" end="${tickLabelCount - 1}" var="tickLabelIndex">
      <c:set var="tickLabelIndexVar" value="tickLabel.${tickLabelIndex}"/>
      <input type="hidden" name="tickLabel.${tickLabelIndex}" value="${param[tickLabelIndexVar]}"/>
    </c:forEach>
  </c:if>
  
  <c:set var="serieValueCount" value="${param['serie.count']}"/>
  <c:if test="${serieValueCount gt 0}">
    <c:forEach begin="0" end="${serieValueCount - 1}" var="serieIndex">
      <c:set value="serie.${serieIndex}.count" var="countVar"/>
      <c:set value="serie.${serieIndex}.caption" var="captionVar"/>
      <input type="hidden" name="serie.${serieIndex}.count" value="${param[countVar]}"/>
      <input type="hidden" name="serie.${serieIndex}.caption" value="${param[captionVar]}"/>
    
      <c:if test="${param[countVar] gt 0}">
        <c:forEach begin="0" end="${param[countVar] - 1}" var="valueIndex">
          <c:set value="serie.${serieIndex}.${valueIndex}" var="valueVar"/>
        
          <input type="hidden" name="serieValue.${serieIndex}.${valueIndex}" value="${param[valueVar]}"/>
        </c:forEach>
      </c:if>
      
    </c:forEach> 
  </c:if>

</div>