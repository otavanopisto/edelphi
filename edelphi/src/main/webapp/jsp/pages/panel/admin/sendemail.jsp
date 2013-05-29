<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.admin.sendEmail.pageTitle"/>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/sendmailview.js"></script>
  </head>
  <body class="panel_admin index">

    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.sendEmail"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/sendemail.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
          <jsp:param value="panel.admin.sendEmail.pageTitle" name="titleLocale"/>
        </jsp:include>

        <form action="${pageContext.request.contextPath}/panel/admin/sendemail.json" method="post">
          <input type="hidden" name="panelId" value="${panel.id}"/>

	        <div id="GUI_emailContentColumn">
	          <jsp:include page="/jsp/blocks/panel_admin/sendemail_content.jsp"></jsp:include>
	        </div>
	        
	        <div id="GUI_emailRecipientsColumn">
	          <jsp:include page="/jsp/blocks/panel_admin/sendemail_recipients.jsp"></jsp:include>
	        </div>
	        
	        <div class="clearBoth"></div>
        
        </form>
        


      </div>

    </div>
  </body>
</html>