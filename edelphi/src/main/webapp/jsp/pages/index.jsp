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
    <c:choose>
      <c:when test="${loggedUserId gt 0}">
        <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/createpanel.js"></script>
      </c:when>
      <c:otherwise>
        <jsp:include page="/jsp/supports/jshash_support.jsp"></jsp:include>
        <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/loginblockcontroller.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/registerblockcontroller.js"></script>
      </c:otherwise>
    </c:choose>
  </head>
  <body class="environment index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
	    
		  <div class="GUI_pageContainer">
		  
		    <div id="GUI_indexIdentificationPanel" class="pagePanel">
          <c:choose>
            <c:when test="${loggedUserId gt 0}">
              <jsp:include page="/jsp/blocks/index/createpanel.jsp"></jsp:include>
              <jsp:include page="/jsp/blocks/index/mypanels.jsp"></jsp:include>
            </c:when>
            
            <c:otherwise>
    	        <jsp:include page="/jsp/blocks/index/login.jsp"></jsp:include>
    	        <jsp:include page="/jsp/blocks/index/register.jsp"></jsp:include>
            </c:otherwise>
          </c:choose>
	      </div>
	      
	      <div id="GUI_indexNewsPanel" class="pagePanel">
				  <jsp:include page="/jsp/blocks/index/news.jsp"></jsp:include>
	      </div>
	      
	      <div id="GUI_indexOpenPanelsPanel" class="pagePanel">
	        <jsp:include page="/jsp/blocks/index/open_panels.jsp"></jsp:include>
	      </div>
	      
	      <div class="clearBoth"></div>
	    </div>
	    
    </div>
    
    <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>
    
  </body>
</html>