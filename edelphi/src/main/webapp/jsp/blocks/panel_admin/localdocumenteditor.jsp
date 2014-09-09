<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminDocumentEditorBlock" class="block">
  
  <!-- 
    TODO: New document title
   -->

  <c:set var="documentTitle">
    <fmt:message key="panelAdmin.block.localDocumentEditor.title"/>
  </c:set>

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="${documentTitle}" name="titleText"/>
  </jsp:include>
  
  <div id="panelAdminDocumentEditorBlockContent" class="blockContent">
    <form id="panelAdminDocumentEditorForm" action="${pageContext.request.contextPath}/resources/savelocaldocument.json">
      <c:choose>
        <c:when test="${localDocument ne null}">
          <jsp:include page="/jsp/fragments/formfield_text.jsp">
            <jsp:param name="name" value="name"/>
            <jsp:param name="value" value="${localDocument.name}"/>
            <jsp:param name="classes" value="required" />
          </jsp:include>
        </c:when>
        <c:otherwise>
          <c:set var="newDocumentName">
            <fmt:message key="panelAdmin.block.localDocumentEditor.newDocumentName"/>
          </c:set>
        
          <jsp:include page="/jsp/fragments/formfield_text.jsp">
            <jsp:param name="name" value="name"/>
            <jsp:param name="value" value="${newDocumentName}"/>
            <jsp:param name="classes" value="required" />
          </jsp:include>
        </c:otherwise>
      </c:choose>

      <div class="panelAdminDocumentEditorActionContainer"> 
        <a href="javascript:void(null);" id="panelAdminDocumentEditorCreatePageLink"><fmt:message key="panelAdmin.block.localDocumentEditor.createPageLink"/></a>
      </div>
      
      <div class="panelAdminDocumentEditorBodyWrapper">
      
        <div class="panelAdminDocumentEditorPagesContainer">
          <div class="panelAdminDocumentEditorPages">
          </div>
        </div>
  
        <div class="panelAdminDocumentEditorContainer">
          <jsp:include page="/jsp/fragments/formfield_text.jsp">
            <jsp:param name="labelLocale" value="panelAdmin.block.localDocumentEditor.pageTitle"/>
            <jsp:param name="name" value="title"/>
            <jsp:param name="classes" value="required" />
          </jsp:include>

          <jsp:include page="/jsp/fragments/formfield_memo.jsp">
            <jsp:param name="labelLocale" value="panelAdmin.block.localDocumentEditor.pageContent"/>
            <jsp:param name="name" value="content"/>
          </jsp:include>
        </div>
        
        <div class="clearBoth"></div>
      
      </div>
        
      <div class="panelAdminDocumentEditorSubmitContainer">
        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="labelLocale" value="panelAdmin.block.localDocumentEditor.save"/>
          <jsp:param name="name" value="save"/>
        </jsp:include>
      </div>
        
      <input type="hidden" name="localDocumentId" value="${param.localDocumentId}"/>
      <input type="hidden" name="parentFolderId" value="${param.parentFolderId}"/>
    </form>
  
  </div>

</div>