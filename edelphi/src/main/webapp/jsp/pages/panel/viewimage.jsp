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
      <fmt:message key="panel.block.image.title">
        <fmt:param>${image.name}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
  </head>
  <body class="panel index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
        <img src="${pageContext.request.contextPath}/resources/viewimage.binary?imageId=${image.id}"/>
        
        <div class="clearBoth"></div>
      </div>
      
    </div>
  </body>
</html>