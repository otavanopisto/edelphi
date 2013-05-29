<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panelAdmin.block.query.queryListTitle" name="titleLocale" />
  </jsp:include>

  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
    <jsp:param name="items" value="CREATE"/>
    <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.dashboard.queryCreateTooltip"/>
    <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createquery.page?panelId=${panel.id}"/>
  </jsp:include>

  <div id="panelQueryListingBlockContent" class="blockContent">

    <c:choose>
      <c:when test="${panelActions['MANAGE_PANEL_MATERIALS']}">
        <c:set var="editAccess" value="true"/>
      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false"/>
      </c:otherwise>
    </c:choose>

    <c:forEach var="query" items="${queries}">
      <jsp:include page="/jsp/fragments/queryadmin_queryrow.jsp">
        <jsp:param name="panelId" value="${panel.id}" />
        <jsp:param name="queryId" value="${query.id}" />
        <jsp:param name="queryName" value="${query.name}" />
        <jsp:param name="queryState" value="${query.state}" />
        <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${query.fullPath}" />
        <jsp:param name="queryVisible" value="${query.visible}" />
        <jsp:param name="queryCreated" value="${query.created.time}" />
        <jsp:param name="queryModified" value="${query.lastModified.time}" />
        <jsp:param name="mayEdit" value="${editAccess}" />
        <jsp:param name="mayDelete" value="${editAccess}" />
        <jsp:param name="selected" value="${param.selectedQueryId eq query.id}" />
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