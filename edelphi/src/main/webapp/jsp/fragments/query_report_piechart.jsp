<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryLiveReportContainer queryPieChartLiveReportContainer">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.answersTitle"></fmt:message></h2>
  
  <c:if test="${param.chartCount gt 0}">
    <div class="queryPieChartsLiveReportContainer">
  
  	  <input type="hidden" value="${param.chartCount}" name="chartCount"/>            

      <c:if test="${param.chartCount gt 0}">
        <c:forEach begin="0" end="${param.chartCount - 1}" varStatus="chartVarStatus">
          <div class="queryPieChart">
    	      <c:set value="chart.${chartVarStatus.index}.id" var="idVar"/>
    	      <c:set value="chart.${chartVarStatus.index}.caption" var="captionVar"/>
    	      <c:set value="chart.${chartVarStatus.index}.dataSetSize" var="dataSetSizeVar"/>
    	      <c:set value="${param[dataSetSizeVar]}" var="dataSetSize"/>
    	        
    	   	  <input type="hidden" value="${param[idVar]}" name="chart.id"/> 
    	   	  <input type="hidden" value="${param[captionVar]}" name="chart.caption"/>            
    	   	  <input type="hidden" value="${dataSetSize}" name="chart.dataSetSize"/>            
    	        
    	      <c:if test="${dataSetSize gt 0}">
    	        <c:forEach begin="0" end="${dataSetSize - 1}" varStatus="dataSetVarStatus">
    	          <c:set value="chart.${chartVarStatus.index}.dataSet.${dataSetVarStatus.index}.id" var="dataSetItemIdVar"/>
    	          <c:set value="chart.${chartVarStatus.index}.dataSet.${dataSetVarStatus.index}.caption" var="dataSetItemCaptionVar"/>
    	          <c:set value="chart.${chartVarStatus.index}.dataSet.${dataSetVarStatus.index}.value" var="dataSetItemValueVar"/>
    	
    			  <input type="hidden" value="${param[dataSetItemIdVar]}" name="chart.dataSet.${dataSetVarStatus.index}.id" /> 
    			  <input type="hidden" value="${param[dataSetItemCaptionVar]}" name="chart.dataSet.${dataSetVarStatus.index}.caption" />            
    			  <input type="hidden" value="${param[dataSetItemValueVar]}" name="chart.dataSet.${dataSetVarStatus.index}.value" />           
    	        </c:forEach>
    	      </c:if>
    	      
            <div class="queryPieChartLiveReportFlotrContainer"></div>
    	    </div>
        </c:forEach>
      </c:if>
    </div>
  </c:if>

</div>