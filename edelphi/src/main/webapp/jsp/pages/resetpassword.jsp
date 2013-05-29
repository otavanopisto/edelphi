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
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/resetpassword.js"></script>
  </head>
  
  <body class="environment resetPassword">
  
     <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
     </jsp:include>
  
     <div class="GUI_pageWrapper">
	    
		  <div class="GUI_pageContainer">
	      <div class="pagePanel">
				  <jsp:include page="/jsp/blocks/resetpassword.jsp"></jsp:include>
	      </div>
	    </div>
	    
    </div>
    
    <jsp:include page="/jsp/templates/index_footer.jsp"></jsp:include>
    
  </body>
</html>