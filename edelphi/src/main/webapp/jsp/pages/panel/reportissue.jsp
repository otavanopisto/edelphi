<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="pageTitle"><fmt:message key="panel.reportIssue.pageTitle"/></c:set>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>${pageTitle}</title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>

    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/reportissueblockcontroller.js"></script>
  </head>
  <body class="panel index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="reportIssue" name="activeTrail"/>
      <jsp:param value="${pageTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}${panel.fullPath}/reportissue.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
    
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/blocks/generic/reportissue.jsp"></jsp:include>
        
      </div>
    
    </div>

  </body>

</html>