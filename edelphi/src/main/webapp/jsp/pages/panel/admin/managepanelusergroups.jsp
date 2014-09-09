<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.managePanelUserGroups.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message> 
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/usergrouplistblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/usergroupeditblockcontroller.js"></script>
    <c:if test="${fn:length(stamps) gt 1}">
      <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/stampoverlaycontroller.js"></script>
    </c:if>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

  </head>
  <body class="panel_admin manageusergroups">
  
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.managePanelUsergroups"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/managepanelusergroups.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="panel.admin.managePanelUserGroups.pageTitle" name="titleLocale"/>
        </jsp:include>

        <div id="GUI_manageUsergroupsListColumn">
          <jsp:include page="/jsp/blocks/panel_admin/managepanelusergroups_listing.jsp"></jsp:include>
        </div>

        <div id="GUI_manageUsergroupsViewColumn">
          <jsp:include page="/jsp/blocks/panel_admin/managepanelusergroups_usergroupview.jsp"></jsp:include>
        </div>

		  </div>

	  </div>

    <c:if test="${fn:length(stamps) gt 1}">
      <div class="stampTimeLineSpacer" id="stampTimeLineSpacer">
        <jsp:include page="/jsp/fragments/stamp_selector.jsp" />
      </div>
    </c:if>

  </body>
</html>