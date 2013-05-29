<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.inviteUsers.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/inviteactionsblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/invitelistingblockcontroller.js"></script>

  </head>
  <body class="panel_admin index">

    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.inviteUsers"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/inviteusers.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="panel.admin.inviteUsers.pageTitle" name="titleLocale"/>
        </jsp:include>

        <div id="GUI_inviteUsersActionColumn">
          <jsp:include page="/jsp/blocks/panel_admin/inviteusers_actions.jsp"></jsp:include>
        </div>
        
        <div id="GUI_inviteUsersListColumn">
        </div>
        
        <div class="clearBoth"></div>

      </div>

    </div>
  </body>
</html>