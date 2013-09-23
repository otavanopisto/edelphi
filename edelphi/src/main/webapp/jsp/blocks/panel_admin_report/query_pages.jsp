<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<!-- Default for size graphs is 740x450 -->
<c:set var="reportChartWidth" value="740"/>
<c:set var="reportChartHeight" value="450"/>

<c:if test="${!empty(param.reportChartWidth)}">
  <c:set var="reportChartWidth" value="${param.reportChartWidth}"/>
</c:if>

<c:if test="${!empty(param.reportChartHeight)}">
  <c:set var="reportChartHeight" value="${param.reportChartHeight}"/>
</c:if>
      
<c:set var="reportPageNumber" value="0"/>
<c:forEach var="reportPageData" items="${reportPageDatas}">

  <div class="reportPage">
    <ed:include page="${reportPageData.jspFile}">
      <ed:param name="reportPageNumber" value="${reportPageNumber}"/>
      <ed:param name="reportChartWidth" value="${reportChartWidth}"/>
      <ed:param name="reportChartHeight" value="${reportChartHeight}"/>
      <ed:param name="reportChartFormat" value="${param.reportChartFormat}"/>
    </ed:include>
  </div>
    
  <c:set var="reportPageNumber" value="${reportPageNumber + 1}"/>
</c:forEach>
