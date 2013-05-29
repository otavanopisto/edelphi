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
      <fmt:message key="panelAdmin.block.query.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/datagrid_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/flotr_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/draft_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/buttoninputcomponent.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/mathutils.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/querylistingblockcontroller.js"></script>
    <c:if test="${resourceLocked eq false}">
      <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/locktoucher.js"></script>
      <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/queryeditorblockcontroller.js"></script>
      <script type="text/javascript">
        document.observe("dom:loaded", function() {
          initDrafting($('panelAdminQueryEditorBlock'));
        });
      </script>
    </c:if>
  </head>
  <body class="panel_admin index">
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.editQuery"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/editquery.page?panelId=${panel.id}&queryId=${query.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">

      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
			    <jsp:param name="titleLocale" value="panelAdmin.block.query.pageTitle"/>
			  </jsp:include>
        
        <div class="GUI_adminManageQueriesNarrowColumn">
          <jsp:include page="/jsp/blocks/panel_admin/query_listing.jsp">
            <jsp:param name="selectedQueryId" value="${query.id}"/>
          </jsp:include>
        </div>
        
        <div class="GUI_adminManageQueriesWideColumn">
          <c:if test="${resourceLocked eq false}">
            <ed:include page="/jsp/blocks/panel_admin/queryeditor.jsp">
              <ed:param name="queryId" value="${query.id}"/>
              <ed:param name="queryName" value="${query.name}"/>
              <ed:param name="parentFolderId" value="${panel.rootFolder.id}"/>
            </ed:include>
          </c:if>
        </div>
        
        <div class="clearBoth"></div>
        
		  </div>
		    
	  </div>
  </body>
</html>