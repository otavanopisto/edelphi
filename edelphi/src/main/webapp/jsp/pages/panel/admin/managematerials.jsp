<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
	  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	  <title><fmt:message key="panel.admin.manageMaterials.pageTitle"><fmt:param>${panel.name}</fmt:param></fmt:message></title>
	  <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
	  <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
	</head>
	<body class="panel_admin index">
	
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.manageMaterials"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/managematerials.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
	  
	  <div class="GUI_pageWrapper">
	   
	    <div class="GUI_pageContainer">
	
	      <jsp:include page="/jsp/fragments/page_title.jsp">
	        <jsp:param value="panel.admin.manageMaterials.pageTitle" name="titleLocale" />
	        <jsp:param name="titleLocaleParam" value="${panel.name}"/>
	      </jsp:include>
	
	      <div class="GUI_adminManageMaterialsNarrowColumn">
	        <jsp:include page="/jsp/blocks/panel_admin/material_listing.jsp"></jsp:include>
	      </div>
	
	      <div class="GUI_adminManageMaterialsWideColumn"></div>
	    </div>
	
	  </div>
	</body>
</html>