<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block materialsBlock">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="index.materials.materialsTitle" name="titleLocale"/>
  </jsp:include>
  
  <div class="blockContent materialsBlockList sortableMaterialList">
    <c:forEach var="materialItem" items="${materials}" >
      <c:set var="material" value="${materialItem}" scope="request"></c:set>
      <c:set var="subMaterials" value="${materialTrees}" scope="request"></c:set>
      
      <jsp:include page="/jsp/blocks/index/material_materialtyperow.jsp">
        <jsp:param value="${param.parentFolderId}" name="parentFolderId"/>
        <jsp:param name="showMeta" value="false" />
      </jsp:include>
    </c:forEach>
  </div>
  
</div>