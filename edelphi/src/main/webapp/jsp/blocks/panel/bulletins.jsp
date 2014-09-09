<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.block.bulletinsTitle" name="titleLocale"/>
  </jsp:include>
  

  <c:set var="editAccess" value="false"/>

  <div id="panelBulletinsBlockContent" class="blockContent">
  
    <c:choose>
	    <c:when test="${panelActions['MANAGE_PANEL_BULLETINS']}">
	      <c:set var="editAccess" value="true"/>
	
	      <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
	        <jsp:param name="items" value="CREATE" />
	        <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.managePanelBulletins.createTooltip" />
	        <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createpanelbulletin.page?panelId=${panel.id}" />
	      </jsp:include>
	
	    </c:when>
	    <c:otherwise>
	      <c:set var="editAccess" value="false"/>
	    </c:otherwise>
	  </c:choose>
  
    <c:choose>
      <c:when test="${empty(bulletins)}">
        <div class="bulletinsEmptyDescription">
          <fmt:message key="panel.block.bulletinsEmptyDescription"/>
        </div>
      </c:when>
      <c:otherwise>
    
		    <c:forEach items="${bulletins}" var="bulletin">
		      <div class="panelBulletinRow">
		        <div class="panelGenericTitle"><a href="${pageContext.request.contextPath}/panel/viewbulletin.page?panelId=${panel.id}&bulletinId=${bulletin.id}">${bulletin.title}</a></div>
		        <div class="panelGenericDescription">${bulletin.summary}</div>
		        <div class="panelGenericMeta">
		        <fmt:message key="panel.block.bulletinsCreatedModified">
		            <fmt:param>
		              <fmt:formatDate value="${bulletin.created}" />
		            </fmt:param>
		            <fmt:param>
		              <fmt:formatDate value="${bulletin.lastModified}" />
		            </fmt:param>
		          </fmt:message>
		        </div>
		        <div class="contextualLinks">
		          <c:if test="${editAccess eq 'true'}">
		            <jsp:include page="/jsp/fragments/contextual_link.jsp">
		              <jsp:param name="onclick" value="" />
		              <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/editpanelbulletin.page?panelId=${panel.id}&bulletinId=${bulletin.id}" />
		              <jsp:param name="action" value="edit" />
		              <jsp:param name="label" value="panel.block.bulletinsContextualLinkTitle.editUpdate" />
		            </jsp:include>
		            
		            <jsp:include page="/jsp/fragments/contextual_link.jsp">
		              <jsp:param name="onclick" value="" />
		              <jsp:param name="href" value="#bulletinId:${bulletin.id}" />
		              <jsp:param name="action" value="delete" />
		              <jsp:param name="label" value="panel.block.bulletinsContextualLinkTitle.deleteUpdate" />
		            </jsp:include>
		          </c:if>
		        </div>
		      </div>  
    </c:forEach>
      
      </c:otherwise>
    </c:choose>
  
    
    
    <%/**
    <div class="panelSystemUpdateRow">
      <div class="panelGenericTitle">Järjestelmän apinoima paneelin päivityksen jatko juudani juttu</div>
      <div class="panelGenericMeta">Maaliskuu 3, 2011 klo. 13:09</div>
      <div class="contextualLinks">
        <jsp:include page="/jsp/fragments/contextual_link.jsp">
          <jsp:param name="onclick" value="" />
          <jsp:param name="href" value="" />
          <jsp:param name="action" value="edit" />
          <jsp:param name="label" value="panel.block.updatesContextualLinkTitle.editUpdate" />
        </jsp:include>
        
        <jsp:include page="/jsp/fragments/contextual_link.jsp">
          <jsp:param name="onclick" value="" />
          <jsp:param name="href" value="" />
          <jsp:param name="action" value="delete" />
          <jsp:param name="label" value="panel.block.updatesContextualLinkTitle.deleteUpdate" />
        </jsp:include>
      </div>
    </div>
    **/%>
  </div>

</div>