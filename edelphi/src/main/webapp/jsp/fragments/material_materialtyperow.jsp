<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${actions[param.editAction]}">
    <c:set var="editAccess" value="true"/>
  </c:when>
  <c:otherwise>
    <c:set var="editAccess" value="false"/>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${material.type eq 'FOLDER'}">
    <div class="materialFolderRow" id="resource_${material.id}">
      <input type="hidden" name="resourceId" value="${material.id}"/>            
      <div class="panelGenericTitle">${material.name}</div>
      <div class="materialFolderChildResources" id="materialFolderChildResources_${material.id}">
        <c:if test="${not empty(subMaterials[material.id])}">
          <c:forEach var="materialItem" items="${subMaterials[material.id]}">
            <c:set var="material" value="${materialItem}" scope="request"></c:set>
            <jsp:include page="/jsp/fragments/material_materialtyperow.jsp">
              <jsp:param name="showMeta" value="${param.showMeta}" />
            </jsp:include>
          </c:forEach>
        </c:if>
      </div>
    </div>
  </c:when>
  <c:when test="${material.type eq 'LOCAL_DOCUMENT'}">
    <jsp:include page="/jsp/fragments/material_materialrow.jsp">
      <jsp:param name="resourceId" value="${material.id}" />
      <jsp:param name="resourceName" value="${material.name}" />
      <jsp:param name="resourcePath" value="${pageContext.request.contextPath}${material.fullPath}" />
      <jsp:param name="resourceCreated" value="${material.created.time}" />
      <jsp:param name="resourceModified" value="${material.lastModified.time}" />
      <jsp:param name="resourceVisible" value="${material.visible}" />
      <jsp:param name="mayEdit" value="${editAccess}" />
      <jsp:param name="mayDelete" value="${editAccess}" />
      <jsp:param name="mayHide" value="${editAccess}" />
      <jsp:param name="showMeta" value="${param.showMeta}" />
      <jsp:param name="editLocale" value="block.materialListing.editLocalDocument" />
      <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalDocument" />
      <jsp:param name="hideLocale" value="block.materialListing.hideLocalDocument" />
      <jsp:param name="showLocale" value="block.materialListing.showLocalDocument"/>
      <jsp:param name="editLink" value="${pageContext.request.contextPath}/admin/editlocaldocument.page?localDocumentId=${material.id}&cat=${param.dashboardCategory}&lang=${dashboardLang}" />
      <jsp:param name="selected" value="${param.localDocumentId eq material.id}" />
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
      <jsp:param name="mayDelete" value="${editAccess}" />
      <jsp:param name="mayHide" value="${editAccess}" />
      <jsp:param name="showMeta" value="${param.showMeta}" />
      <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleDocument" />
      <jsp:param name="hideLocale" value="block.materialListing.hideGoogleDocument" />
      <jsp:param name="showLocale" value="block.materialListing.showGoogleDocument"/>
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
      <jsp:param name="mayEdit" value="${editAccess}" />
      <jsp:param name="mayDelete" value="${editAccess}" />
      <jsp:param name="mayHide" value="${editAccess}" />
      <jsp:param name="showMeta" value="${param.showMeta}" />
      <jsp:param name="editLocale" value="block.materialListing.editLocalImage" />
      <jsp:param name="deleteLocale" value="block.materialListing.deleteLocalImage" />
      <jsp:param name="hideLocale" value="block.materialListing.hideLocalImage" />
      <jsp:param name="showLocale" value="block.materialListing.showLocalImage"/>
      <jsp:param name="editLink" value="${pageContext.request.contextPath}/admin/editlocalimage.page?resourceId=${material.id}&cat=${param.dashboardCategory}&lang=${dashboardLang}" />
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
      <jsp:param name="mayDelete" value="${editAccess}" />
      <jsp:param name="mayHide" value="${editAccess}" />
      <jsp:param name="showMeta" value="${param.showMeta}" />
      <jsp:param name="deleteLocale" value="block.materialListing.deleteLinkedImage" />
      <jsp:param name="hideLocale" value="block.materialListing.hideLinkedImage" />
      <jsp:param name="showLocale" value="block.materialListing.showLinkedImage"/>
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
      <jsp:param name="mayDelete" value="${editAccess}" />
      <jsp:param name="mayHide" value="${editAccess}" />
      <jsp:param name="showMeta" value="${param.showMeta}" />
      <jsp:param name="deleteLocale" value="block.materialListing.deleteGoogleImage" />
      <jsp:param name="hideLocale" value="block.materialListing.hideGoogleImage" />
      <jsp:param name="showLocale" value="block.materialListing.showGoogleImage"/>
    </jsp:include>
  </c:when>
</c:choose> 
