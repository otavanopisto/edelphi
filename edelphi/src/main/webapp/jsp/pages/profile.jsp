<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="index.pageTitle" /> </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/jshash_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/profile.js"></script>
    <c:choose>
      <c:when test="${empty user.defaultEmail.address && (empty user.firstName || empty user.lastName)}">
        <script type="text/javascript">
          var eventQueue = getGlobalEventQueue();
          eventQueue.addItem(new EventQueueItem(getLocale().getText("profile.block.missingNameAndEmail"), {
            className : "eventQueueWarningItem",
            timeout : -1
          }));
        </script>
      </c:when>
      <c:when test="${empty user.firstName || empty user.lastName}">
        <script type="text/javascript">
          var eventQueue = getGlobalEventQueue();
          eventQueue.addItem(new EventQueueItem(getLocale().getText("profile.block.missingName"), {
            className : "eventQueueWarningItem",
            timeout : -1
          }));
        </script>
      </c:when>
      <c:when test="${empty user.defaultEmail.address}">
        <script type="text/javascript">
          var eventQueue = getGlobalEventQueue();
          eventQueue.addItem(new EventQueueItem(getLocale().getText("profile.block.missingEmail"), {
            className : "eventQueueWarningItem",
            timeout : -1
          }));
        </script>
      </c:when>
    </c:choose>
  </head>
  
  <body class="environment index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
	    
		  <div class="GUI_pageContainer">
		    <div id="GUI_profileIdentificationPanel" class="pagePanel">
          <jsp:include page="/jsp/blocks/index/createpanel.jsp"></jsp:include>
          <jsp:include page="/jsp/blocks/index/mypanels.jsp"></jsp:include>
	      </div>
	      
	      <div id="GUI_indexProfilePanel" class="pagePanel">
				  <jsp:include page="/jsp/blocks/profile.jsp"></jsp:include>
	      </div>
	      
	      <div id="GUI_profileOpenPanelsPanel" class="pagePanel">
	        <jsp:include page="/jsp/blocks/index/open_panels.jsp"></jsp:include>
	      </div>
	    </div>
	    
    </div>
    
    <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>
    
  </body>
</html>