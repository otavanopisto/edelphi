<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexCreatePanelBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param name="titleLocale" value="index.block.createPanelBlockTitle"/>
    <jsp:param name="helpText" value=""/>
  </jsp:include>
  
  <c:set var="visibility">
    <c:if test="${!actions['CREATE_PANEL']}">style="display: none"</c:if>
  </c:set>
  
  <div id="createPanelBlockContent" class="blockContent" ${visibility}>

      <a class="createPanelBlockCreatePanelLink"> <!--  href="${pageContext.request.contextPath}/panel/createpanel.json?name=New%20Panel&description=New%20Desc" -->
        <fmt:message key="index.block.createPanelBlockCreatePanelLink" />
      </a>
      
      <div class="createPanelBlock_createPanelDialogOverlay" style="display: none;">
      </div>

      <div class="createPanelBlock_createPanelPageContainer" style="display: none;">
        <div class="createPanelBlock_createPanelCloseModalButton"></div>
        <div class="createPanelBlock_createPanelPageContent"> </div>
      </div>

  </div>

</div>