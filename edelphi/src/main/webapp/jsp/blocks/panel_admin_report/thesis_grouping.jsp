<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <c:set var="reportPageData" value="${reportPageDatas[param.reportPageNumber]}" />
  
  <div class="blockContent">
    <div class="blockContent">
      <h2>${reportPageData.queryPage.title}</h2>
      
      <jsp:include page="/jsp/fragments/query_report_thesis.jsp">
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>

      <c:forEach var="optionGroup" items="${reportPageData.groups}">
        <ed:queryPageChart reportContext="${reportContext}" output="${param.reportChartFormat}" width="${param.reportChartWidth}" height="${param.reportChartHeight}" queryPageId="${reportPageData.queryPage.id}">
          <ed:param name="groupId" value="${optionGroup.id}"/>
        </ed:queryPageChart>
      </c:forEach>

      <!-- Applying of comments -->

      <jsp:include page="/jsp/fragments/query_commentlist.jsp">
        <jsp:param value="true" name="reportMode"/>
        <jsp:param value="${reportPageData.queryPage.id}" name="queryPageId"/>
      </jsp:include>
    </div>
  </div>

</div>

