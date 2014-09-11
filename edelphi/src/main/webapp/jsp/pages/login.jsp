<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="generic.errorPage.notLoggedIn.title" /></title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/jshash_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/loginblockcontroller.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/registerblockcontroller.js"></script>
  </head>
  <body class="environment login">
  
    <jsp:include page="/jsp/templates/error_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <!-- Not Logged In -->
	      <div class="errorPageContentWrapper">
	        <div class="errorPageTitleContainer notLoggedIn"><fmt:message key="generic.errorPage.notLoggedIn.title" /></div>
	        <div class="errorPageIconContainer notLoggedInIcon"></div>
	        <div class="errorPageDescriptionContainer">
	          <fmt:message key="generic.errorPage.notLoggedIn.description"/>
          </div>
	        <div class="clearBoth"></div>
	        <div class="errorPageGoToFrontPageLinkContainer"><a href="${pageContext.request.contextPath}/index.page"><fmt:message key="generic.errorPage.goToFrontPageLabel" /></a></div>
	        <div class="errorPageLoginRegisterContainer">
	          <jsp:include page="/jsp/blocks/index/login.jsp"></jsp:include>
              <jsp:include page="/jsp/blocks/index/register.jsp"></jsp:include>
	        </div> 
	        <div class="clearBoth"></div> 
	      </div>

      </div>
      
    </div>
  </body>
</html>