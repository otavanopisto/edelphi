<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
 <c:when test="${param.selected eq 'true'}">
   <c:set var="rowClasses">panelAdminQueryRow queryAdminQueryRowSelected</c:set>
 </c:when>
 <c:otherwise>
   <c:set var="rowClasses">panelAdminQueryRow</c:set>
 </c:otherwise> 
</c:choose>

<c:choose>
  <c:when test="${param.queryState eq 'EDIT'}">
    <c:set var="rowClasses" value="${rowClasses} panelAdminQueryRowStateEdit"/>
  </c:when>
</c:choose>

<div class="${rowClasses}">

  <jsp:useBean id="created" class="java.util.Date" />
  <jsp:useBean id="modified" class="java.util.Date" />
  
  <jsp:setProperty name="created" property="time" value="${param.queryCreated}" />
  <jsp:setProperty name="modified" property="time" value="${param.queryModified}" />

  <div class="panelAdminGenericTitle"><a href="${param.resourcePath}" target="_blank">${param.queryName}</a></div>
  <div class="panelAdminGenericMeta">
    <fmt:message key="panel.admin.dashboard.queryCreatedModified">
      <fmt:param>
        <fmt:formatDate value="${created}"/>
       </fmt:param>
      <fmt:param>
        <fmt:formatDate value="${modified}"/>
       </fmt:param>
    </fmt:message>
  </div>
  
  <div class="contextualLinks">
    <c:if test="${param.mayEdit eq 'true'}">
      <c:choose>
        <c:when test="${param.queryVisible eq 'true'}">
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#queryId:${param.queryId}" />
            <jsp:param name="action" value="show" />
            <jsp:param name="hidden" value="true" />
            <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.showQuery" />
          </jsp:include>
        
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#queryId:${param.queryId}" />
            <jsp:param name="action" value="hide" />
            <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.hideQuery" />
          </jsp:include>
        </c:when>
        <c:otherwise>
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#queryId:${param.queryId}" />
            <jsp:param name="action" value="show" />
            <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.showQuery" />
          </jsp:include>
        
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#queryId:${param.queryId}" />
            <jsp:param name="action" value="hide" />
            <jsp:param name="hidden" value="true" />
            <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.hideQuery" />
          </jsp:include>
        </c:otherwise>
      </c:choose>
      
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="#queryId:${param.queryId}" />
        <jsp:param name="action" value="copy"/>
        <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.copyQuery" />
      </jsp:include>
  
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/editquery.page?panelId=${param.panelId}&queryId=${param.queryId}" />
        <jsp:param name="action" value="edit" />
        <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.editQuery" />
      </jsp:include>
    </c:if>
    
    <c:if test="${param.mayDelete eq 'true'}">
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="#queryId:${param.queryId}" />
        <jsp:param name="action" value="delete" />
        <jsp:param name="label" value="panel.block.queriesContextualLinkTitle.deleteQuery" />
      </jsp:include>
    </c:if>
  </div>
</div>
