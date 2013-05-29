<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="panelAdmin.block.queryResults.pageTitle" /></title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/datagrid_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/flotr_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/buttoninputcomponent.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/mathutils.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/queryresultslistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/queryreportoptions.js"></script>
  </head>
  <body class="panel_admin">

    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.queryReport"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/queryresults_queryreport.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
    
    <div class="GUI_pageWrapper">
  
      <div class="GUI_pageContainer">
  
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param name="titleLocale" value="panelAdmin.block.queryResults.pageTitle" />
        </jsp:include>
  
        <div class="GUI_adminManageQueriesNarrowColumn">
          <jsp:include page="/jsp/blocks/panel_admin/queryresults_listing.jsp" />
        </div>
  
        <div class="GUI_adminManageQueriesWideColumn">
          <div id="GUI_panelReportControlsContainer" class="pagePanel">
            <ed:include page="/jsp/blocks/panel_admin_report/report_options.jsp">
            </ed:include>
          </div>
        
          <div id="GUI_panelReportContainer" class="pagePanel">
            <ed:include page="/jsp/blocks/panel_admin_report/query_pages.jsp">
              <ed:param name="reportPageDatas" value="${reportPageDatas}"/>
              <ed:param name="reportChartWidth" value="740"/>
              <ed:param name="reportChartHeight" value="450"/>
              <ed:param name="reportChartFormat" value="${chartFormat}"/>
            </ed:include>
          </div>
        </div>
      </div>
  
    </div>

    <c:if test="${fn:length(stamps) gt 1}">
      <div class="stampTimeLineSpacer">
        <jsp:include page="/jsp/fragments/stamp_selector.jsp" />
      </div>
    </c:if>
    
  </body>
</html>