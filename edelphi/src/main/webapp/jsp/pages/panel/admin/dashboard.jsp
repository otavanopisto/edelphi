<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.dashboard.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/settingsblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/querylistingblockcontroller.js"></script>
  </head>
  <body class="panel_admin index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="panel.admin.dashboard.pageTitle" name="titleLocale"/>
        </jsp:include>

        <div class="GUI_adminDashboardNarrowColumn">
        
          <div id="GUI_adminDashboardSettingsContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel_admin/settings.jsp"></jsp:include>
          </div>
          
          <div id="GUI_adminDashboardMaterialsContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel_admin/material_listing.jsp">
              <jsp:param value="true" name="showAllLink"/>
            </jsp:include>
          </div>

        </div>
        
        <div class="GUI_adminDashboardWideColumn">

          <div id="GUI_adminDashboardProcessContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel_admin/process.jsp"></jsp:include>
          </div>
          
          <div id="GUI_adminDashboardQueriesContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel_admin/query_listing.jsp">
              <jsp:param value="true" name="showAllLink"/>
            </jsp:include>
          </div>
          
          <div id="GUI_adminDashboardUsersContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel_admin/users.jsp"></jsp:include>
          </div>
          
          <div class="clearBoth"></div>
	        
        </div>
        
        <div class="clearBoth"></div>
        
      </div>
        
    </div>
  </body>
</html>