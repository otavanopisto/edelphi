<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.block.queriesTitle" name="titleLocale" />
  </jsp:include>

  <div id="panelQueryListingBlockContent" class="blockContent">
  
    <c:choose>
      <c:when test="${panelActions['MANAGE_PANEL_MATERIALS']}">
        <c:set var="editAccess" value="true"/>

        <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
          <jsp:param name="items" value="CREATE"/>
          <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.dashboard.queryCreateTooltip"/>
          <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createquery.page?panelId=${panel.id}"/>
        </jsp:include>

      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false"/>
      </c:otherwise>
    </c:choose>
  
    <c:choose>
      <c:when test="${empty(unfinishedQueries) and empty(notStartedQueries) and empty(finishedQueries) and empty(closedQueries)}">
        <div class="queriesEmptyDescription">
          <fmt:message key="panel.block.queriesEmptyDescription"/>
        </div>
      </c:when>
      <c:otherwise>

		    <c:choose>
		      <c:when test="${fn:length(unfinishedQueries) > 0}">
		        <h3>
		          <fmt:message key="panel.block.queries.unfinishedQueriesTitle" />
		        </h3>
		        <!-- TODO: Needs to check users privileges to edit and/or delete query -->
		        <c:forEach var="query" items="${unfinishedQueries}">
		          <jsp:include page="/jsp/fragments/panel_queryrow.jsp">
		            <jsp:param name="queryId" value="${query.id}" />
		            <jsp:param name="queryName" value="${query.name}" />
		            <jsp:param name="queryState" value="${query.state}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${query.fullPath}" />
		            <jsp:param name="queryDescription" value="${query.description}" />
		            <jsp:param name="queryCreated" value="${query.created.time}" />
		            <jsp:param name="queryVisible" value="${query.visible}" />
		            <jsp:param name="answerButton" value="true" />
		            <jsp:param name="answerButtonLocale" value="panel.block.queries.continueAnsweringTitle" />
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="timeToAnswer" value="${query.timeToAnswer}" />
		          </jsp:include>
		        </c:forEach>
		      </c:when>
		    </c:choose>
		
		    <c:choose>
		      <c:when test="${fn:length(notStartedQueries) > 0}">
		        <h3>
		          <fmt:message key="panel.block.queries.notStartedQueriesTitle" />
		        </h3>
		        <!-- TODO: Needs to check users privileges to edit and/or delete query -->
		        <c:forEach var="query" items="${notStartedQueries}">
		          <jsp:include page="/jsp/fragments/panel_queryrow.jsp">
		            <jsp:param name="queryId" value="${query.id}" />
		            <jsp:param name="queryName" value="${query.name}" />
		            <jsp:param name="queryState" value="${query.state}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${query.fullPath}" />
		            <jsp:param name="queryDescription" value="${query.description}" />
		            <jsp:param name="queryCreated" value="${query.created.time}" />
		            <jsp:param name="queryVisible" value="${query.visible}" />
		            <jsp:param name="answerButton" value="true" />
		            <jsp:param name="answerButtonLocale" value="panel.block.queries.startAnsweringTitle" />
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="timeToAnswer" value="${query.timeToAnswer}" />
		          </jsp:include>
		        </c:forEach>
		      </c:when>
		    </c:choose>
		
		    <c:choose>
		      <c:when test="${fn:length(finishedQueries) > 0}">
		        <h3>
		          <fmt:message key="panel.block.queries.finishedQueriesTitle" />
		        </h3>
		        <!-- TODO: Needs to check users privileges to edit and/or delete query -->
		        <c:forEach var="query" items="${finishedQueries}">
		          <jsp:include page="/jsp/fragments/panel_queryrow.jsp">
		            <jsp:param name="queryId" value="${query.id}" />
		            <jsp:param name="queryName" value="${query.name}" />
		            <jsp:param name="queryState" value="${query.state}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${query.fullPath}" />
		            <jsp:param name="queryDescription" value="${query.description}" />
		            <jsp:param name="queryCreated" value="${query.created.time}" />
		            <jsp:param name="queryVisible" value="${query.visible}" />
		            <jsp:param name="answerButton" value="true" />
		            <jsp:param name="answerButtonLocale" value="panel.block.queries.changeAnswersTitle" />
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="timeToAnswer" value="${query.timeToAnswer}" />
		          </jsp:include>
		        </c:forEach>
		      </c:when>
		    </c:choose>
		
		    <c:choose>
		      <c:when test="${fn:length(closedQueries) > 0}">
		        <h3>
		          <fmt:message key="panel.block.queries.closedQueriesTitle" />
		        </h3>
		        <!-- TODO: Needs to check users privileges to edit and/or delete query -->
		        <c:forEach var="query" items="${closedQueries}">
		          <jsp:include page="/jsp/fragments/panel_queryrow.jsp">
		            <jsp:param name="queryId" value="${query.id}" />
		            <jsp:param name="queryName" value="${query.name}" />
		            <jsp:param name="queryState" value="${query.state}" />
		            <jsp:param name="queryDescription" value="${query.description}" />
		            <jsp:param name="queryCreated" value="${query.created.time}" />
		            <jsp:param name="queryVisible" value="${query.visible}" />
		            <jsp:param name="answerButton" value="false" />
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		          </jsp:include>
		        </c:forEach>
		      </c:when>
		    </c:choose>

      </c:otherwise>
    </c:choose>
  </div>

</div>