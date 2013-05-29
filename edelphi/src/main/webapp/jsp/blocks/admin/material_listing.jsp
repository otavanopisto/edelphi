<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block materialsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.dashboard.materialsTitle" name="titleLocale"/>
  </jsp:include>
  
  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">

    <jsp:param name="items" value="CREATE,CREATEFOLDER"/>
    <jsp:param name="item.CREATEFOLDER.tooltipLocale" value="admin.dashboard.materialCreateFolder"/>
    <jsp:param name="item.CREATEFOLDER.href" value="#parentFolderId:${param.parentFolderId}"/>

    <jsp:param name="item.CREATE.tooltipLocale" value="admin.dashboard.materialCreateLocalDocument"/>
    <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/admin/createlocaldocument.page?cat=materials&lang=${dashboardLang}"/>
  </jsp:include>
  
  <div class="blockContent materialsBlockList sortableMaterialList">
    <input type="hidden" name="materialsBlockListParentFolderId" value="${param.parentFolderId}"/>

    <c:forEach var="materialItem" items="${materials}" >
      <c:set var="material" value="${materialItem}" scope="request"></c:set>
      <c:set var="subMaterials" value="${materialTrees}" scope="request"></c:set>
      
      <jsp:include page="/jsp/fragments/material_materialtyperow.jsp">
        <jsp:param value="materials" name="dashboardCategory"/>
        <jsp:param value="MANAGE_DELFOI_MATERIALS" name="editAction"/>
        <jsp:param value="${param.parentFolderId}" name="parentFolderId"/>
      </jsp:include>
    </c:forEach>
  </div>
  
  <c:if test="${param.showAllLink eq 'true'}">
    <div class="indexAdminDashboardMaterialsShowAllLink">
      <a href="${pageContext.request.contextPath}/admin/managematerials.page?cat=materials&lang=${dashboardLang}">
        <fmt:message key="admin.dashboard.materialsShowAll">
          <fmt:param>${materialCount}</fmt:param>
        </fmt:message>
      </a>
    </div>
  </c:if>
  
</div>