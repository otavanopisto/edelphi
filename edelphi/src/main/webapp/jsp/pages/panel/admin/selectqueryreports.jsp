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
  <body class="panel_admin_selection">

    <div class="GUI_pageWrapper">
  
      <div class="GUI_pageContainer">
  
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param name="titleLocale" value="panelAdmin.block.selectqueryreports.pageTitle" />
        </jsp:include>
  
        <div class="pageDescription"><fmt:message key="panelAdmin.block.selectqueryreports.description"/></div>
        
        <form id="compareReportsSelectionForm" name="compareReportsSelectionForm">
          <div class="queryListingWrapper">

            <!-- Single query selection rowsy to bes looped tills the ends of timesy -->
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection1" name="querySelection1" /></div>
              <div class="queryListingNameContainer"><label for="querySelection1">1. Suomalainen alkoholismi ja turismi</label></div>
            </div>
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection2" name="querySelection2" /></div>
              <div class="queryListingNameContainer"><label for="querySelection2">2. Suomalainen pessimismi ja *-ismi</label></div>
            </div>
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection3" name="querySelection3" /></div>
              <div class="queryListingNameContainer"><label for="querySelection3">3. Kolmannen kierroksen kootut selitykset</label></div>
            </div>
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection4" name="querySelection4" /></div>
              <div class="queryListingNameContainer"><label for="querySelection4">4. Itseisarvot itseisarviona</label></div>
            </div>
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection5" name="querySelection5" /></div>
              <div class="queryListingNameContainer"><label for="querySelection5">5. Kolmannen asteen yhteydet</label></div>
            </div>
            <div class="queryListingRow">
              <div class="queryListingInputContainer"><input type="checkbox" id="querySelection6" name="querySelection6" /></div>
              <div class="queryListingNameContainer"><label for="querySelection6">6. Kärppä Jänis ja Roope ankka</label></div>
            </div>
                      
          </div>
        
          <div class="querySubmitRow">
            <div class="queryListingInputContainer"><input type="submit" id="selectionSubmit" name="selectionSubmit" /></div>
          </div>
        </form>
        
      </div>
  
    </div>
  
  </body>
</html>