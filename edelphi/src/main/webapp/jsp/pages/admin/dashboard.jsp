<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="admin.dashboard.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/admin/bulletinlistblockcontroller.js"></script>
  </head>
  <body class="environment_admin index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
		    <div class="dashboardLocaleSelectorContainer">
		      <c:choose>
		        <c:when test="${dashboardLang eq 'fi'}"><a href="?lang=fi" class="dashboardLanguageLinkSelected">FI</a></c:when>
		        <c:otherwise><a href="?lang=fi">FI</a></c:otherwise>
		      </c:choose>
		      
		      <c:choose>
		        <c:when test="${dashboardLang eq 'en'}"><a href="?lang=en" class="dashboardLanguageLinkSelected">EN</a></c:when>
		        <c:otherwise><a href="?lang=en">EN</a></c:otherwise>
		      </c:choose>
		    </div>      
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="admin.dashboard.pageTitle" name="titleLocale"/>
        </jsp:include>

        <div class="GUI_indexAdminDashboardNarrowColumn">
          <c:set scope="request" var="materials" value="${helpMaterials}"/>
          <c:set scope="request" var="materialCount" value="${helpMaterialCount}"/>
          <c:set scope="request" var="materialTrees" value="${helpMaterialTrees}"/>
          
          <div id="GUI_indexAdminDashboardGuidesContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/admin/help_listing.jsp">
              <jsp:param value="true" name="showAllLink"/>
              <jsp:param value="${helpMaterialFolderId}" name="parentFolderId"/>
            </jsp:include>
          </div>

        </div>

        <div class="GUI_indexAdminDashboardNarrowColumn">
          
          <div id="GUI_indexAdminDashboardMaterialsContainer" class="pagePanel">
            <c:set scope="request" var="materials" value="${materialMaterials}"/>
            <c:set scope="request" var="materialCount" value="${materialMaterialCount}"/>
            <c:set scope="request" var="materialTrees" value="${materialMaterialTrees}"/>

            <jsp:include page="/jsp/blocks/admin/material_listing.jsp">
              <jsp:param value="true" name="showAllLink"/>
              <jsp:param value="${materialFolderId}" name="parentFolderId"/>
            </jsp:include>
          </div>

        </div>

        <div class="GUI_indexAdminDashboardNarrowColumn">
          <div id="GUI_indexAdminDashboardUsersContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/admin/usermanagementtools.jsp"></jsp:include>
          </div>
        
          <div id="GUI_indexAdminDashboardBulletinsContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/admin/bulletins.jsp"></jsp:include>
          </div>

          <div class="clearBoth"></div>
        </div>
        
        
        <div class="clearBoth"></div>
        
      </div>
        
    </div>
  </body>
</html>