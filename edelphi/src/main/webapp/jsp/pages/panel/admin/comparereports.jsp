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
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/jquery/jquery.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/comparereports.js"></script>
    <link href="${pageContext.request.contextPath}/_themes/${theme}/css/comparereports.css" rel="stylesheet"/>
  </head>
  <body class="panel_admin_compare">

    <div class="GUI_pageWrapper">
  
      <div class="GUI_pageContainer">
  
        <div class="pageTitle">
          <h1>Paneelin nimi ladataan tähän</h1>
        </div>
        
        <div class="GUI_reportContainer">
        
          <div class="GUI_selectedQueryReportWrapperLeft">
            <div class="selectedQueryReportTitle">Vasemman kyselyn nimi</div>
            <div class="selectedQueryReportActions">
              <div class="selectedQueryReportActions-filters">
                <div class="selectedQueryReportActions-filters-icon"></div>
                <div class="selectedQueryReportActions-filters-container"></div>
              </div>
              <div class="selectedQueryReportActions-exports">
                <div class="selectedQueryReportActions-exports-icon"></div>
                <div class="selectedQueryReportActions-exports-container"></div>
              </div>
              <div class="selectedQueryReportActions-settings">
                <div class="selectedQueryReportActions-settings-icon"></div>
                <div class="selectedQueryReportActions-settings-container"></div>
              </div>
            </div>
          </div>
        
          <div class="GUI_selectedQueryReportWrapperRight">
            <div class="selectedQueryReportTitle">Oikean kyselyn nimi</div>
            <div class="selectedQueryReportActions">
              <div class="selectedQueryReportActions-filters">
                <div class="selectedQueryReportActions-filters-icon"></div>
                <div class="selectedQueryReportActions-filters-container"></div>
              </div>
              <div class="selectedQueryReportActions-exports">
                <div class="selectedQueryReportActions-exports-icon"></div>
                <div class="selectedQueryReportActions-exports-container"></div>
              </div>
              <div class="selectedQueryReportActions-settings">
                <div class="selectedQueryReportActions-settings-icon"></div>
                <div class="selectedQueryReportActions-settings-container"></div>
              </div>
            </div>
          </div>
        </div>
        
      </div>
  
    </div>
  
  </body>
</html>