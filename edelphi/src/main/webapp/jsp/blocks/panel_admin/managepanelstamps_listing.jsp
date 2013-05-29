<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panels.admin.panelStamps.stampListTitle" name="titleLocale" />
  </jsp:include>
  
  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
    <jsp:param name="items" value="CREATE"/>
    <jsp:param name="item.CREATE.tooltipLocale" value="panels.admin.panelStamps.createStampTooltip"/>
  </jsp:include>
  
  <div id="panelAdminManagePanelStampsListingBlockContent" class="blockContent">
  
    <c:forEach var="panelStamp" items="${panelStamps}" varStatus="vs">
      <c:if test="${!vs.first}">
        <div class="managePanelStamps_panelStamp" id="managePanelStamps_panelStamp_${panelStamp.id}">
        
          <div class="managePanelStamps_panelStamp_name">${panelStamp.name}</div>
          <div class="managePanelStamps_panelStamp_created"><fmt:message key="panels.admin.panelStamps.stampListCreated" />: <fmt:formatDate value="${panelStamp.stampTime}" pattern="d.M.yyyy"/></div>
          <div class="managePanelStamps_panelStamp_desc">${panelStamp.description}</div>
        </div>
      </c:if>    
    </c:forEach>
  </div>  

</div>