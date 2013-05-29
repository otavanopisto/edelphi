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
      <fmt:message key="panel.block.bulletin.title">
        <fmt:param>${bulletin.title}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
  </head>
  <body class="environment index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
      <jsp:param value="${bulletin.title}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/viewbulletin.page?bulletinId=${bulletin.id}" name="breadcrumbPageUrl"/>
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
          <div class="newsEntryContainer">
	          <div class="GUI_pageContainer">
	            <h2 class="newsEntryTitle">${bulletin.title}</h2>
	            <div class="genericMeta"><fmt:formatDate value="${bulletin.created}" dateStyle="long"/></div>
	            <div class="newsEntryContent">${bulletin.message}</div>
	          </div>
	        </div>
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