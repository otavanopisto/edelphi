<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="admin.manageMaterials.pageTitle"/> 
    </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
  </head>
  <body class="environment_admin index">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.manageMaterials"/></c:set>
    
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/editlocaldocument.page?localDocumentId=${localDocument.id}&cat=${dashboardCategory}&lang=${dashboardLang}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param name="titleLocale" value="admin.manageMaterials.pageTitle"/>
        </jsp:include>

        <div class="GUI_adminManageMaterialsNarrowColumn">
          <c:choose>
            <c:when test="${dashboardCategory eq 'help'}">
              <jsp:include page="/jsp/blocks/admin/help_listing.jsp">
                <jsp:param value="${parentFolder.id}" name="parentFolderId"/>
              </jsp:include>
            </c:when>
            <c:otherwise>
              <jsp:include page="/jsp/blocks/admin/material_listing.jsp">
                <jsp:param value="${parentFolder.id}" name="parentFolderId"/>
              </jsp:include>
            </c:otherwise>
          </c:choose>        
        </div>
        
        <div class="GUI_adminManageMaterialsWideColumn">
        </div>
        
        <div class="clearBoth"></div>
        
      </div>
        
    </div>
  </body>
</html>