<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${param.selected eq 'true'}">
    <c:set var="rowClasses">materialRow materialRowSelected</c:set>
  </c:when>
  <c:otherwise>
    <c:set var="rowClasses">materialRow</c:set>
  </c:otherwise> 
</c:choose>

<div class="${rowClasses}" id="resource_${param.resourceId}">
  <jsp:useBean id="created" class="java.util.Date" />
  <jsp:useBean id="modified" class="java.util.Date" />

  <jsp:setProperty name="created" property="time" value="${param.resourceCreated}" />
  <jsp:setProperty name="modified" property="time" value="${param.resourceModified}" />

  <input type="hidden" name="resourceId" value="${param.resourceId}"/>            

  <div class="panelGenericTitle"><a href="${param.resourcePath}">${param.resourceName}</a></div>
  
  <c:if test="${param.showMeta ne 'false'}">
    <div class="panelGenericMeta">
      <fmt:message key="block.materialListing.materialCreatedModified">
        <fmt:param>
          <fmt:formatDate value="${created}"/>
         </fmt:param>
        <fmt:param>
          <fmt:formatDate value="${modified}"/>
         </fmt:param>
      </fmt:message>
    </div>
  </c:if>
  
  <div class="contextualLinks">
    <c:if test="${param.mayEdit eq 'true'}">
      <c:choose>
        <c:when test="${param.resourceVisible eq 'true'}">
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#resourceId:${param.resourceId}" />
            <jsp:param name="action" value="show" />
            <jsp:param name="hidden" value="true" />
            <jsp:param name="label" value="${param.showLocale}" />
          </jsp:include>
        
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#resourceId:${param.resourceId}" />
            <jsp:param name="action" value="hide" />
            <jsp:param name="label" value="${param.hideLocale}" />
          </jsp:include>
        </c:when>
        <c:otherwise>
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#resourceId:${param.resourceId}" />
            <jsp:param name="action" value="show" />
            <jsp:param name="label" value="${param.showLocale}" />
          </jsp:include>
        
          <jsp:include page="/jsp/fragments/contextual_link.jsp">
            <jsp:param name="href" value="#resourceId:${param.resourceId}" />
            <jsp:param name="action" value="hide" />
            <jsp:param name="hidden" value="true" />
            <jsp:param name="label" value="${param.hideLocale}" />
          </jsp:include>
        </c:otherwise>
      </c:choose>
  
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="${param.editLink}" />
        <jsp:param name="action" value="edit" />
        <jsp:param name="label" value="${param.editLocale}" />
      </jsp:include>
    </c:if>
    
    <c:if test="${param.mayDelete eq 'true'}">
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="#resourceId:${param.resourceId}" />
        <jsp:param name="action" value="delete" />
        <jsp:param name="label" value="${param.deleteLocale}" />
      </jsp:include>
    </c:if>
  </div>
</div>
