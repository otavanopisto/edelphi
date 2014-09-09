<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block materialsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.block.materialsTitle" name="titleLocale"/>
  </jsp:include>
  
  <div class="blockContent materialsBlockList">
  
    <c:choose>
      <c:when test="${panelActions['MANAGE_PANEL_MATERIALS']}">
        <c:set var="editAccess" value="true"/>

        <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
          <jsp:param name="items" value="CREATE,GDOCSIMPORT"/>
      
          <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.dashboard.materialCreateLocalDocument"/>
          <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createlocaldocument.page?panelId=${panel.id}"/>
      
          <jsp:param name="item.GDOCSIMPORT.tooltipLocale" value="panel.admin.dashboard.materialImportGoogleDocuments"/>
          <jsp:param name="item.GDOCSIMPORT.href" value="${pageContext.request.contextPath}/panel/admin/importmaterialsgdocs.page?panelId=${panel.id}"/>
        </jsp:include>

      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false"/>
      </c:otherwise>
    </c:choose>
  
    <c:choose>
      <c:when test="${empty(materials)}">
        <div class="materialsEmptyDescription">
          <fmt:message key="panel.block.materialsEmptyDescription"/>
        </div>
      </c:when>
      <c:otherwise>
  
		    <c:forEach var="material" items="${materials}">
		      <c:choose>
		        <c:when test="${material.type eq 'LOCAL_DOCUMENT'}">
		          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
		            <jsp:param name="resourceId" value="${material.id}" />
		            <jsp:param name="resourceName" value="${material.name}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
		            <jsp:param name="resourceCreated" value="${material.created.time}" />
		            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
		            <jsp:param name="resourceVisible" value="${material.visible}" />
		            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
		            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="editLocale" value="block.materialListing.editLocalDocument" />
		            <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalDocument" />
		            <jsp:param name="editLink" value="${pageContext.request.contextPath}/panel/admin/editlocaldocument.page?panelId=${panel.id}&localDocumentId=${material.id}" />
		          </jsp:include>
		        </c:when>
		        <c:when test="${material.type eq 'GOOGLE_DOCUMENT'}">
		          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
		            <jsp:param name="resourceId" value="${material.id}" />
		            <jsp:param name="resourceName" value="${material.name}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
		            <jsp:param name="resourceCreated" value="${material.created.time}" />
		            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
		            <jsp:param name="resourceVisible" value="${material.visible}" />
		            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
		            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleDocument" />
		          </jsp:include>
		        </c:when>
		        <c:when test="${material.type eq 'LOCAL_IMAGE'}">
		          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
		            <jsp:param name="resourceId" value="${material.id}" />
		            <jsp:param name="resourceName" value="${material.name}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
		            <jsp:param name="resourceCreated" value="${material.created.time}" />
		            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
		            <jsp:param name="resourceVisible" value="${material.visible}" />
		            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
		            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
		            <jsp:param name="mayEdit" value="${editAccess}" />
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="editLocale" value="block.materialListing.editLocalImage" />
		            <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalImage" />
		            <jsp:param name="editLink" value="${pageContext.request.contextPath}/panel/admin/editlocalimage.page?panelId=${panel.id}&resourceId=${material.id}" />
		          </jsp:include>
		        </c:when>
		        <c:when test="${material.type eq 'LINKED_IMAGE'}">
		          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
		            <jsp:param name="resourceId" value="${material.id}" />
		            <jsp:param name="resourceName" value="${material.name}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
		            <jsp:param name="resourceCreated" value="${material.created.time}" />
		            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
		            <jsp:param name="resourceVisible" value="${material.visible}" />
		            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
		            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="deleteLocale" value="block.materialListing.deleteLinkedImage" />
		          </jsp:include>
		        </c:when>
		        <c:when test="${material.type eq 'GOOGLE_IMAGE'}">
		          <jsp:include page="/jsp/fragments/material_materialrow.jsp">
		            <jsp:param name="resourceId" value="${material.id}" />
		            <jsp:param name="resourceName" value="${material.name}" />
		            <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
		            <jsp:param name="resourceCreated" value="${material.created.time}" />
		            <jsp:param name="resourceModified" value="${material.lastModified.time}" />
		            <jsp:param name="resourceVisible" value="${material.visible}" />
		            <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
		            <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
		            <jsp:param name="mayDelete" value="${editAccess}" />
		            <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleImage" />
		          </jsp:include>
		        </c:when>
		      </c:choose> 
		      
		    </c:forEach>
    
      </c:otherwise>
    </c:choose>
    
  </div>

</div>