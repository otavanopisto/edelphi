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
    <jsp:include page="/jsp/supports/draft_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/locktoucher.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/localdocumenteditorblockcontroller.js"></script>
  </head>
  <body class="environment_admin index">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.createLocalDocument"/></c:set>
    
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/admin/createlocaldocument.page?lang=${dashboardLang}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">

      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
	        <jsp:param name="titleLocale" value="admin.manageMaterials.pageTitle"/>
		    </jsp:include>

        <div class="GUI_adminManageMaterialsNarrowColumn">
          <c:choose>
            <c:when test="${dashboardCategory eq 'help'}">
              <jsp:include page="/jsp/blocks/admin/help_listing.jsp"></jsp:include>
            </c:when>
            <c:otherwise>
              <jsp:include page="/jsp/blocks/admin/material_listing.jsp"></jsp:include>
            </c:otherwise>
          </c:choose>        
        </div>
        
        <div class="GUI_adminManageMaterialsWideColumn">
          <ed:include page="/jsp/blocks/panel_admin/localdocumenteditor.jsp">
            <ed:param name="localDocumentId" value="${localDocument.id}"/>
            <ed:param name="parentFolderId" value="${parentFolder.id}"/>
            <ed:param name="pageCount" value="${fn:length(localDocumentPages)}"/>
            <c:forEach var="localDocumentPage" items="${localDocumentPages}" varStatus="vs">
              <ed:param name="pageTitle.${vs.index}" value="${localDocumentPage.title}"/>
              <ed:param name="pageContent.${vs.index}" value="${localDocumentPage.content}"/>
            </c:forEach>
          </ed:include>
        </div>
        
        <div class="clearBoth"></div>
        
		  </div>
		    
	  </div>
  </body>
</html>