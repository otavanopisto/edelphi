<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="stampSelectorOverlay" id="stampSelectorOverlay">
  <div class="stampSelectorOverlayBackground"></div>
	<div class="stampSelectorOverlayContainer">
  	<div class="stampSelectorOverlayHeader" id="stampSelectorOverlayHeader">
      <h2><fmt:message key="panels.admin.panelStampsOverlay.stampOverlayTitle" /></h2>
      <div class="stampSelectorOverLayDown" id="stampSelectorOverLayButton">
	      <span class="stampOverlayButtonTooltip">
	        <span class="stampOverlayButtonTooltipText" id="stampOverlayButtonTooltipText"><fmt:message key="panels.admin.panelStampsOverlay.stampOverlayDownTooltip" /></span>
	        <span class="stampOverlayButtonTooltipArrow"></span>
	      </span>
      </div>
    </div>
	  <div class="stampTimelineSpacerContainer">
	    <!-- Spacer -->
	  </div>
	  <c:forEach var="stamp" items="${stamps}" varStatus="vs">
	    <c:choose>
	      <c:when test="${stamp.id eq activeStamp.id}">
	        <!--  current stamp -->
					<div class="stampTimelineContainer">
					  <span class="stampTimelineIcon current-stamp"></span>
					  <c:choose>
              <c:when test="${vs.last}">
							  <span class="stampTimelineCreationDateWrapper">
                  <span class="stampTimelineCreationPresent">${stamp.name}</span>
                </span>
					    </c:when>
					    <c:otherwise>
					      <span class="stampTimelineCreationDateWrapper">
                  <span class="stampTimelineCreationDate"><fmt:formatDate value="${stamp.stampTime}" pattern="dd.MM."/></span><br/>
                  <span class="stampTimelineCreationYear"><fmt:formatDate value="${stamp.stampTime}" pattern="yyyy"/></span>
                </span>
		            <span class="stampTimeLineTooltip">
		              <span class="stampTimeLineTooltipText">
		                <span class="stampTimeLineName">${stamp.name}</span>
		                <span class="stampTimeLineDesc">${stamp.description}</span>
		              </span>
		              <span class="stampTimeLineTooltipArrow"></span>
		            </span>                
					    </c:otherwise>  
					  </c:choose>
					</div>
	      </c:when>
	      <c:when test="${vs.last}">
	        <!--  present -->
					<div class="stampTimelineContainer">
					  <a href="${pageContext.request.contextPath}/panel/setactivestamp.page?panelId=${panel.id}" title="${stamp.description}">
					    <span class="stampTimelineIcon present-stamp"></span>
					    <span class="stampTimelineCreationDateWrapper">
                <span class="stampTimelineCreationPresent">${stamp.name}</span>
              </span>
					  </a>
					</div>	        
	      </c:when>
	      <c:otherwise>
	        <!--  activatable stamp -->
					<div class="stampTimelineContainer">
					  <a href="${pageContext.request.contextPath}/panel/setactivestamp.page?panelId=${panel.id}&stampId=${stamp.id}" title="${stamp.description}">
					    <span class="stampTimelineIcon activatable-stamp"></span>
					    <span class="stampTimelineCreationDateWrapper">
                <span class="stampTimelineCreationDate"><fmt:formatDate value="${stamp.stampTime}" pattern="dd.MM."/></span><br/>
                <span class="stampTimelineCreationYear"><fmt:formatDate value="${stamp.stampTime}" pattern="yyyy"/></span>
              </span>
					    <span class="stampTimeLineTooltip">
					      <span class="stampTimeLineTooltipText">
					        <span class="stampTimeLineName">${stamp.name}</span>
                  <span class="stampTimeLineDesc">${stamp.description}</span>
					      </span>
					      <span class="stampTimeLineTooltipArrow"></span>
					    </span>
					  </a>
					</div>	        
	      </c:otherwise>
	    </c:choose>
	  </c:forEach>
	</div>
</div>