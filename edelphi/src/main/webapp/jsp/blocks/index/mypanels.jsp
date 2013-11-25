<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexMyPanelsBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param name="titleLocale" value="index.block.myPanelsBlockTitle"/>
    <jsp:param name="helpText" value=""/>
  </jsp:include>
  
  <div id="myPanelsBlockContent" class="blockContent">
    
    <c:choose>
      <c:when test="${empty(myPanels)}">
        <div class="myPanelsEmptyDescription">
          <fmt:message key="index.block.myPanelsEmptyDescription"/>
        </div>
      </c:when>
      <c:otherwise>
	      <c:forEach var="panel" items="${myPanels}">
		      <div class="myPanelsContainer">
		        <div class="myPanelsTitle"><a href="${pageContext.request.contextPath}${panel.fullPath}">${panel.name}</a></div>
		        <div class="genericMeta"><fmt:message key="index.block.myPanelsBlockPanelCreated" /> <fmt:formatDate pattern="d.M.yyyy" value="${panel.created}" /></div>
		        <div class="myPanelsAdditional"></div>
		      </div>  
		    </c:forEach>
      </c:otherwise>
    </c:choose>
  </div>

</div>

