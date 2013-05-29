<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.viewPanel.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/materiallistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/querylistingblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_blocks/bulletinsblockcontroller.js"></script>
  </head>
  <body class="panel index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
	      <div id="GUI_panelMaterialsContainer" class="pagePanel">
	        <jsp:include page="/jsp/blocks/panel/materials.jsp"></jsp:include>
	      </div>
	      
	      <div id="GUI_panelQueriesContainer" class="pagePanel">
	        <jsp:include page="/jsp/blocks/panel/queries.jsp"></jsp:include>
	      </div>
	      
	      <div id="GUI_panelUpdatesContainer" class="pagePanel">
            <jsp:include page="/jsp/blocks/panel/bulletins.jsp"></jsp:include>
	      </div>
	      
	      <div class="clearBoth"></div>
      
      </div>
	  
    </div>
    
  </body>
</html>