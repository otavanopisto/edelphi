<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
 <c:when test="${param.selected eq 'true'}">
   <c:set var="rowClasses">panelAdminQueryResultsRow panelAdminQueryResultsRowSelected</c:set>
 </c:when>
 <c:otherwise>
   <c:set var="rowClasses">panelAdminQueryResultsRow</c:set>
 </c:otherwise> 
</c:choose>

<div class="${rowClasses}">

  <div class="panelAdminGenericTitle"><a href="${pageContext.request.contextPath}/panel/admin/queryresults_queryreport.page?panelId=${param.panelId}&queryId=${query.id}">${param.queryName}</a></div>
  
  <c:if test="${param.selected eq 'true'}">
    <div class="panelAdminQueryResultsQueryPagesContainer">
      <c:forEach var="queryPage" items="${selectedQueryPages}">
        <div class="panelAdminQueryResultPageRow">
          <div class="panelAdminGenericTitle"><a href="${pageContext.request.contextPath}/panel/admin/queryresults_pagereport.page?panelId=${param.panelId}&queryId=${query.id}&pageId=${queryPage.id}">${queryPage.title}</a></div>
          
          <div class="contextualLinks">
            <jsp:include page="/jsp/fragments/contextual_link.jsp">
              <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/queryresults_pagereport.page?panelId=${param.panelId}&queryId=${query.id}&pageId=${queryPage.id}" />
              <jsp:param name="action" value="viewReport" />
              <jsp:param name="label" value="panelAdmin.block.queryResults.showReport" />
            </jsp:include>
            
			      <jsp:include page="/jsp/fragments/contextual_link.jsp">
			        <jsp:param name="href" value="#queryPageId:${queryPage.id};contextPath:${pageContext.request.contextPath};panelId:${param.panelId};queryId:${query.id};stampId:${activeStamp.id}" />
			        <jsp:param name="target" value="_blank"/> 
			        <jsp:param name="action" value="downloadOrExportPageReport" />
			        <jsp:param name="label" value="panelAdmin.block.queryResults.downloadOrExportReport" />
			      </jsp:include>

            <c:if test="${param.queryReplyCount gt 0 and activeStamp.id eq latestStamp.id}">
              <jsp:include page="/jsp/fragments/contextual_link.jsp">
                <jsp:param name="href" value="#queryPageId:${queryPage.id};replyCount:${param.queryReplyCount}" />
                <jsp:param name="target" value="_blank" />
                <jsp:param name="action" value="deletePageAnswers" />
                <jsp:param name="label" value="panelAdmin.block.queryResults.deleteQueryAnswers" />
              </jsp:include>
            </c:if>

					</div>
        </div>
      </c:forEach>
    </div>
  </c:if>
  
  <div class="contextualLinks">
    <jsp:include page="/jsp/fragments/contextual_link.jsp">
      <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/queryresults_queryreport.page?panelId=${param.panelId}&queryId=${query.id}" />
      <jsp:param name="action" value="viewReport" />
      <jsp:param name="label" value="panelAdmin.block.queryResults.showReport" />
    </jsp:include>
    
    <jsp:include page="/jsp/fragments/contextual_link.jsp">
      <jsp:param name="href" value="#contextPath:${pageContext.request.contextPath};panelId:${param.panelId};queryId:${query.id};stampId:${activeStamp.id}" />
      <jsp:param name="target" value="_blank"/> 
      <jsp:param name="action" value="downloadOrExportReport" />
      <jsp:param name="label" value="panelAdmin.block.queryResults.downloadOrExportReport" />
    </jsp:include>
    
    <c:if test="${param.queryReplyCount gt 0 and activeStamp.id eq latestStamp.id}">
      <jsp:include page="/jsp/fragments/contextual_link.jsp">
        <jsp:param name="href" value="#queryId:${query.id};stampId:${activeStamp.id};replyCount:${param.queryReplyCount}" />
        <jsp:param name="target" value="_blank"/> 
        <jsp:param name="action" value="deleteAllAnswers" />
        <jsp:param name="label" value="panelAdmin.block.queryResults.deleteQueryAnswers" />
      </jsp:include>
      
    </c:if>
  </div>
  
  <div class="panelAdminGenericMeta">
    <fmt:message key="panelAdmin.block.queryResults.queryListQueryCreatedModified">
      <fmt:param>
        <fmt:formatDate value="${query.created}"/>
       </fmt:param>
      <fmt:param>
        <fmt:formatDate value="${query.lastModified}"/>
       </fmt:param>
    </fmt:message>
  </div>
  
</div>
