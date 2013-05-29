<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

<%--   <jsp:include page="/jsp/fragments/block_title.jsp"> --%>
<%--     <jsp:param name="titleLocale" value="index.block.myPanelsBlockTitle"/> --%>
<%--     <jsp:param name="helpText" value="<p><b>Omat paneelit</b> -ohjeistus</p>"/> --%>
<%--   </jsp:include> --%>
  
  <c:set var="reportPageData" value="${reportPageDatas[param.reportPageNumber]}" />
  
  <div class="blockContent">
    <div class="blockContent">
      <h2>${reportPageData.queryPage.title}</h2>

      <jsp:include page="/jsp/fragments/query_report_thesis.jsp">
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>

      <!-- Default for THESIS_SCALE_1D is barchart -->

      <ed:queryPageChart output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}" stampId="${reportPageData.stamp.id}">
        <c:forEach var="filter" items="${reportReplyFilters}">
          <ed:param name="filter:${filter.type}" value="${filter.value}"/>
        </c:forEach>
      </ed:queryPageChart>

      <table>
        <tr>
          <td>A</td>
          <td><fmt:formatNumber maxFractionDigits="3" value="${reportPageData.statistics.avg}"/></td>
        </tr>
        <tr>
          <td>n</td>
          <td>${reportPageData.statistics.count}</td>
        </tr>
        <tr>
          <td>Md</td>
          <td>
            ${reportPageData.statistics.dataNames[reportPageData.statistics.median]} (<fmt:formatNumber maxFractionDigits="0" value="${reportPageData.statistics.median}"/>)
          </td>
        </tr>
        <tr>
          <td>min</td>
          <td>
            ${reportPageData.statistics.dataNames[reportPageData.statistics.min]} (<fmt:formatNumber maxFractionDigits="0" value="${reportPageData.statistics.min}"/>)
          </td>
        </tr>
        <tr>
          <td>max</td>
          <td>
            ${reportPageData.statistics.dataNames[reportPageData.statistics.max]} (<fmt:formatNumber maxFractionDigits="0" value="${reportPageData.statistics.max}"/>)
          </td>
        </tr>
        <tr>
          <td>stdDev</td>
          <td><fmt:formatNumber maxFractionDigits="3" value="${reportPageData.statistics.stdDev}"/></td>
        </tr>
      </table>

      <!-- Applying of comments -->

      <jsp:include page="/jsp/fragments/query_commentlist.jsp">
        <jsp:param value="true" name="reportMode"/>
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>
    </div>
  </div>

</div>

