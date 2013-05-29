<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
  <c:when test="${param.queryState eq 'EDIT'}">
    <c:set var="panelRowClasses" value="panelQueryRow panelQueryRowStateEdit"/>
  </c:when>
  <c:otherwise>
    <c:set var="panelRowClasses" value="panelQueryRow"/>
  </c:otherwise>
</c:choose>

<div class="${panelRowClasses}">
  <jsp:useBean id="created" class="java.util.Date" />
  <jsp:setProperty name="created" property="time" value="${param.queryCreated}" />
  
  <c:choose>
    <c:when test="${!empty(param.resourcePath)}">
      <div class="panelGenericTitle"><a href="${param.resourcePath}">${param.queryName}</a></div>
    </c:when>
    <c:otherwise>
      <div class="panelGenericTitle">${param.queryName}</div>
    </c:otherwise>
  </c:choose>
  
  <div class="panelGenericDescription">${param.queryDescription}</div>
  <div class="panelGenericMeta">
    <div class="panelQueryMetaCreated">
      <fmt:message key="panel.block.queries.createdTitle">
        <fmt:param>
          <fmt:formatDate value="${created}" />
        </fmt:param>
      </fmt:message>
    </div>
     
    <c:choose>
      <c:when test="${!empty(param.timeToAnswer)}">
        <div class="panelQueryMetaTimeToAnswer">
          <fmt:message key="panel.block.queries.timeToAnswerTitle">
            <fmt:param>${param.timeToAnswer}</fmt:param>
          </fmt:message>
        </div>
      </c:when>
    </c:choose>
    
    <c:choose>
      <c:when test="${param.answerButton eq 'true'}">
        <a class="panelQueryAnswerButton" href="${param.resourcePath}"> 
           <fmt:message key="${param.answerButtonLocale}" /> 
        </a>
      </c:when>
    </c:choose>
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
        <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/editquery.page?queryId=${param.queryId}&panelId=${panel.id}" />
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
