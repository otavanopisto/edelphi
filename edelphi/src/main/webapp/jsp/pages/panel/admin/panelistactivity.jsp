<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="panel.admin.panelistActivity.pageTitle" /></title>
<jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/panelistactivity.js"></script>
</head>
<body class="panel_admin index">

  <c:set var="pageBreadcrumbTitle">
    <fmt:message key="panel.admin.panelistActivity.pageTitle" />
  </c:set>

  <jsp:include page="/jsp/templates/panel_header.jsp">
    <jsp:param value="management" name="activeTrail" />
    <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle" />
    <jsp:param value="${pageContext.request.contextPath}/panel/admin/panelistactivity.page?panelId=${panel.id}"
      name="breadcrumbPageUrl" />
  </jsp:include>

  <div id="panelistActivityBlock" class="GUI_pageWrapper">

    <div class="GUI_pageContainer">

      <jsp:include page="/jsp/fragments/page_title.jsp">
        <jsp:param value="panel.admin.panelistActivity.pageTitle" name="titleLocale" />
      </jsp:include>

      <div id="GUI_panelistActivityNarrowColumn">
        <div class="block">
          <div class="blockTitle">
            <h2>
              <fmt:message key="panel.admin.panelistActivity.queriesListingBlockTitle"></fmt:message>
            </h2>
          </div>
          <c:forEach var="panelQuery" items="${queries}">
            <jsp:useBean id="created" class="java.util.Date" />
            <jsp:useBean id="modified" class="java.util.Date" />

            <jsp:setProperty name="created" property="time" value="${panelQuery.created.time}" />
            <jsp:setProperty name="modified" property="time" value="${panelQuery.lastModified.time}" />

            <c:choose>
              <c:when test="${query.id eq panelQuery.id}">
                <c:set var="queryRowClasses" value="panelAdminQueryRow selectedQueryRow" />
              </c:when>
              <c:otherwise>
                <c:set var="queryRowClasses" value="panelAdminQueryRow" />
              </c:otherwise>
            </c:choose>
            <div class="${queryRowClasses}">
              <input type="hidden" name="queryId" value="${panelQuery.id}" />
              <div class="panelAdminGenericTitle">${panelQuery.name}</div>
              <div class="panelAdminGenericMeta">
                <fmt:message key="panel.admin.panelistActivity.queryCreatedModified">
                  <fmt:param>
                    <fmt:formatDate value="${created}" />
                  </fmt:param>
                  <fmt:param>
                    <fmt:formatDate value="${modified}" />
                  </fmt:param>
                </fmt:message>
              </div>
            </div>
          </c:forEach>
        </div>
      </div>

      <div id="GUI_panelistActivityWideColumn">
        <div class="block">
          <div class="blockTitle">
            <h2>
              <fmt:message key="panel.admin.panelistActivity.panelistActivityBlockTitle"></fmt:message>
            </h2>
          </div>

          <c:choose>
            <c:when test="${!empty query}">
              <div id="panelistActivityAnsweredReplicants">
                <div class="answeredReplicantsTitleWrapper">
                  <h3>
                    <span id="repliedPanelistName"><fmt:message
                        key="panel.admin.panelistActivity.answeredReplicantsTitle"></fmt:message></span>
                  </h3>
                  <div class="panelistActivityLastLoginTitle">
                    <span id="repliedPanelistLogin"><fmt:message
                        key="panel.admin.panelistActivity.latestLoginLabel"></fmt:message></span>
                  </div>
                  <div class="panelistActivityLastReplyTitle">
                    <span id="repliedPanelistReply"><fmt:message
                        key="panel.admin.panelistActivity.answeredTimeLabel"></fmt:message></span>
                  </div>
                </div>
                <c:forEach var="repliedPanelist" items="${repliedPanelists}">
                  <div class="repliedPanelist">
                    <div class="panelistActivityName">
                      <c:choose>
                        <c:when test="${empty repliedPanelist.name}">${repliedPanelist.email}</c:when>
                        <c:otherwise>${repliedPanelist.name}</c:otherwise>
                      </c:choose>
                    </div>
                    <div class="panelActivityMeta panelAdminGenericMeta">
                      <div class="panelistActivityLastLogin">
                        <input type="hidden" name="lastLogin" value="${repliedPanelist.lastLogin.time}" />
                        <fmt:formatDate pattern="d.M.yyyy" value="${repliedPanelist.lastLogin}" />
                      </div>
                      <div class="panelistActivityLastReply">
                        <input type="hidden" name="lastReply" value="${repliedPanelist.replyDate.time}" />
                        <fmt:formatDate pattern="d.M.yyyy" value="${repliedPanelist.replyDate}" />
                      </div>
                    </div>
                    <c:if test="${!empty repliedPanelist.name}">
                      <div class="panelistActivityEmail">${repliedPanelist.email}</div>
                    </c:if>
                  </div>
                </c:forEach>
              </div>

              <div id="panelistActivityUnAnsweredReplicants">
                <div class="unansweredReplicantsTitleWrapper">
                  <h3>
                    <span id="unrepliedPanelistName"><fmt:message
                        key="panel.admin.panelistActivity.unAnsweredReplicantsTitle"></fmt:message></span>
                  </h3>
                  <div class="panelistActivityLastLoginTitle">
                    <span id="unrepliedPanelistLogin"><fmt:message
                        key="panel.admin.panelistActivity.latestLoginLabel"></fmt:message></span>
                  </div>
                </div>
                <c:forEach var="unrepliedPanelist" items="${unrepliedPanelists}">
                  <div class="unrepliedPanelist">
                    <div class="panelistActivityName">
                      <c:choose>
                        <c:when test="${empty unrepliedPanelist.name}">${unrepliedPanelist.email}</c:when>
                        <c:otherwise>${unrepliedPanelist.name}</c:otherwise>
                      </c:choose>
                    </div>
                    <div class="panelActivityMeta panelAdminGenericMeta">
                      <div class="panelistActivityLastLogin">
                        <input type="hidden" name="lastLogin" value="${unrepliedPanelist.lastLogin.time}" />
                        <fmt:formatDate pattern="d.M.yyyy" value="${unrepliedPanelist.lastLogin}" />
                      </div>
                    </div>
                    <c:if test="${!empty unrepliedPanelist.name}">
                      <div class="panelistActivityEmail">${unrepliedPanelist.email}</div>
                    </c:if>
                  </div>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <div class="panelistActivityOnEmptyDescription genericOnEmptyInfo">
                <fmt:message key="panel.admin.panelistActivity.onEmptyDescription"></fmt:message>
              </div>
            </c:otherwise>
          </c:choose>

        </div>
      </div>

    </div>
  </div>
</body>
</html>