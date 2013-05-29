<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="messageCenter.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/messagecenterview.js"></script>
  </head>
  <body class="panel_admin index">

    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.messageCenter"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/messagecenter.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="messageCenter.pageTitle" name="titleLocale"/>
        </jsp:include>

        <form action="${pageContext.request.contextPath}/panel/admin/sendemail.json" method="post">
          <input type="hidden" name="panelId" value="${panel.id}"/>

	        <div id="GUI_messageCenterContentColumn">
	          <jsp:include page="/jsp/blocks/panel_admin/messagecenter_content.jsp"></jsp:include>
	        </div>
	        
	        <div id="GUI_messageCenterMessagesColumn">
	          <jsp:include page="/jsp/blocks/panel_admin/messagecenter_messages.jsp"></jsp:include>
	        </div>
	        
	        <div class="clearBoth"></div>
        
        </form>
        


      </div>

    </div>
  </body>
</html>