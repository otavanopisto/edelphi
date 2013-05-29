<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryLiveReportContainer queryBubbleChartLiveReportContainer">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.answersTitle"></fmt:message></h2>
  <div class="queryBubbleChartLiveReportFlotrContainer"></div> 
  
  <input type="hidden" value="${param.xAxisLabel}" name="xAxisLabel"/>
  <input type="hidden" value="${param.yAxisLabel}" name="yAxisLabel"/>
  
  <c:if test="${(param.xValueCount gt 0) && (param.yValueCount gt 0)}">
    <c:forEach begin="0" end="${param.xValueCount - 1}" varStatus="xvs">
      <c:forEach begin="0" end="${param.yValueCount - 1}" varStatus="yvs">
        <c:set value="bubble.${xvs.index}.${yvs.index}.value" var="value"/>
        <c:if test="${param[value] ne null}">
        
          <c:set value="bubble.${xvs.index}.${yvs.index}.x" var="x"/>
          <c:set value="bubble.${xvs.index}.${yvs.index}.y" var="y"/>
        
          <input type="hidden" value="${param[value]}" name="bubble.${xvs.index}.${yvs.index}.value" class="bubbleValue"/>
          <input type="hidden" value="${param[x]}" name="bubble.${xvs.index}.${yvs.index}.x" class="bubbleX"/>
          <input type="hidden" value="${param[y]}" name="bubble.${xvs.index}.${yvs.index}.y" class="bubbleY"/>
        </c:if>
      </c:forEach>
      
      <c:set value="xTickLabel.${xvs.index}" var="xLabel"/>
      <c:if test="${!empty(param[xLabel])}">
        <input type="hidden" value="${param[xLabel]}" name="xTickLabel.${xvs.index}" class="bubbleLabelX"/>
      </c:if>
    </c:forEach>
      
    <c:forEach begin="0" end="${param.yValueCount - 1}" varStatus="yvs">
      <c:set value="yTickLabel.${yvs.index}" var="yLabel"/>
      <c:if test="${!empty(param[yLabel])}">
        <input type="hidden" value="${param[yLabel]}" name="yTickLabel.${yvs.index}" class="bubbleLabelY"/>
      </c:if>
    </c:forEach>  
  </c:if>
    
</div>