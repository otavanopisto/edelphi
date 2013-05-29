<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexOpenPanelsBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="index.block.openPanelsBlockTitle" name="titleLocale"/>
  </jsp:include>
  
  <div id="openPanelsBlockContent" class="blockContent">
    <c:choose>
      <c:when test="${empty(openPanels)}">
        <div class="openPanelsEmptyDescription">
          <fmt:message key="index.block.openPanelsEmptyDescription"/>
        </div>
      </c:when>
      <c:otherwise>
		    <c:forEach var="panel" items="${openPanels}">
		      <div class="openPanelContainer">
		        <div class="openPanelTitle"><a href="${pageContext.request.contextPath}/${panel.urlName}">${panel.name}</a></div>
		        <div class="genericMeta"></div>
		        <div class="openPanelAdditional"></div>
		      </div>  
		    </c:forEach>
      </c:otherwise>
    </c:choose>
    
  </div>

</div>