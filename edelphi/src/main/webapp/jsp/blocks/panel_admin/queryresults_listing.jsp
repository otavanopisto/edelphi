<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panelAdmin.block.query.queryListTitle" name="titleLocale" />
  </jsp:include>

  <div id="panelQueryResultsListingBlockContent" class="blockContent">
    <c:set var="activeStamp" value="${activeStamp}" scope="request"/>
    <c:set var="latestStamp" value="${latestStamp}" scope="request"/>
    <c:forEach var="query" items="${queries}">
      <c:set var="query" value="${query}" scope="request"/>
      <c:set var="selectedQueryPages" value="${queryPages[query.id]}" scope="request"/>
      <c:set var="queryReplyCount" value="${queryReplyCounts[query.id]}" scope="request"/>      
      
      <jsp:include page="/jsp/fragments/queryresults_queryrow.jsp">
        <jsp:param name="panelId" value="${panel.id}" />
        <jsp:param name="queryName" value="${query.name}"/>
        <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${query.fullPath}"/>
        <jsp:param name="selected" value="${param.queryId eq query.id}" />
        <jsp:param name="queryReplyCount" value="${queryReplyCount}" />        
      </jsp:include>
    </c:forEach>
  </div>

  <c:if test="${param.showAllLink eq 'true'}">
    <div class="adminDashboardQueriesShowAllLink">
      <a href="${pageContext.request.contextPath}/panel/admin/managequeries.page?panelId=${panel.id}">
        <fmt:message key="panel.admin.dashboard.queriesShowAll">
          <fmt:param>${queryCount}</fmt:param>
        </fmt:message>
      </a>
    </div>
  </c:if>

</div>