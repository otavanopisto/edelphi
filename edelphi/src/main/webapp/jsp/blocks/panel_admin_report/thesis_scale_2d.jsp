<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <c:set var="reportPageData" value="${reportPageDatas[param.reportPageNumber]}" />
  
  <div class="blockContent">
    <div class="blockContent">
    
      <!-- Title -->
    
      <h2>${reportPageData.queryPage.title}</h2>
      
      <!-- Thesis -->

      <jsp:include page="/jsp/fragments/query_report_thesis.jsp">
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>
      
      <c:choose>
        <c:when test="${!empty reportContext.parameters['show2dAs1d']}">
          <!-- two 1D charts -->
          <ed:queryPageChart reportContext="${reportContext}" output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}">
	        <ed:param name="render2dAxis" value="x"/>
          </ed:queryPageChart>
          <ed:queryPageChart reportContext="${reportContext}" output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}">
	        <ed:param name="render2dAxis" value="y"/>
          </ed:queryPageChart>
        </c:when>
        <c:otherwise>
          <!-- 2D chart -->
          <ed:queryPageChart reportContext="${reportContext}" output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}">
	        <ed:param name="render2dAxis" value="both"/>
          </ed:queryPageChart>
        </c:otherwise>
      </c:choose>

      <!-- Comment list -->

      <jsp:include page="/jsp/fragments/query_commentlist.jsp">
        <jsp:param value="true" name="reportMode"/>
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>
    </div>
  </div>

</div>

