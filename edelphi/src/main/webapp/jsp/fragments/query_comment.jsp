<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<% pageContext.setAttribute("newLineChar", "\n"); %>

<c:set var="showComment" value="false"/>
<c:set var="commentClasses" value="queryComment"/>

<c:choose> 
  <c:when test="${param.commentHidden eq true}">
    <c:choose>
      <c:when test="${(param.reportMode eq true)}">
        <c:set var="showComment" value="false"/>
      </c:when>
      <c:when test="${panelActions['MANAGE_QUERY_COMMENTS']}">
        <c:set var="showComment" value="true"/>
		<c:set var="commentClasses" value="queryComment queryCommentHidden"/>
      </c:when>
    </c:choose>
  </c:when>
  <c:otherwise>
    <c:set var="showComment" value="true"/>
  </c:otherwise>
</c:choose>

<c:if test="${showComment eq true}">

  <jsp:useBean id="created" class="java.util.Date" />
  <jsp:setProperty name="created" property="time" value="${param.commentCreated}" />
  <c:set var="commentDate"><fmt:formatDate value="${created}" type="both"/></c:set>
    
  <div class="${commentClasses}">
    <a id="comment.${param.commentId}"/>
	<div class="queryCommentHeader">
      <div class="queryCommentDate"><fmt:message key="query.comment.commentDate"/> ${commentDate}</div>
      <c:if test="${(param.reportMode ne true)}">
        <div class="queryCommentNewComment"><a href="#" class="queryCommentNewCommentLink"><fmt:message key="query.comment.commentAnswerLink"/></a></div>
        
        <c:choose>
		  <c:when test="${panelActions['MANAGE_QUERY_COMMENTS']}">
	        <div class="queryCommentShowComment"><a href="#" class="queryCommentShowCommentLink"><fmt:message key="query.comment.commentShowLink"/></a></div>
	        <div class="queryCommentHideComment"><a href="#" class="queryCommentHideCommentLink"><fmt:message key="query.comment.commentHideLink"/></a></div>
	        <div class="queryCommentEditComment"><a href="#" class="queryCommentEditCommentLink"><fmt:message key="query.comment.commentEditLink"/></a></div>
	        <div class="queryCommentDeleteComment"><a href="#" class="queryCommentDeleteCommentLink"><fmt:message key="query.comment.commentDeleteLink"/></a></div>
	      </c:when>
	    </c:choose>
      </c:if>
    </div>
    
    <c:if test="${(param.reportMode ne true)}">
	  <c:if test="${param.commentCreated ne param.commentModified}">
		<jsp:useBean id="modified" class="java.util.Date" />
		<jsp:setProperty name="modified" property="time" value="${param.commentModified}" />
	  	<div class="queryCommentModified">
	  	  <fmt:message key="query.comment.commentModified">
	  	    <fmt:param><fmt:formatDate value="${modified}" type="both"/></fmt:param>
	  	  </fmt:message>
	    </div>
	  </c:if>
	</c:if>
	
    <input type="hidden" name="commentId" value="${param.commentId}"/>  
    <input type="hidden" name="queryPageId" value="${param.queryPageId}"/>
	
    <c:if test="${(param.printCommentAnswers eq true)}">
      <c:if test="${!empty(commentAnswers[param.commentId + 0])}">
        <c:forEach var="entry" items="${commentAnswers[param.commentId + 0]}">
          <div class="queryCommentText"><b>${fn:escapeXml(entry.key)}:</b> ${fn:escapeXml(entry.value)}</div>
        </c:forEach>
      </c:if>
    </c:if>
  
  <c:choose>
    <c:when test="${param.commentFiltered}">
      <div class="queryCommentText"><i>&lt; <fmt:message key="query.comment.commentFiltered" /> &gt;</i></div>
    </c:when>
    <c:otherwise>
      <div class="queryCommentText">${fn:replace(fn:escapeXml(param.commentText), newLineChar, "<br/>")}</div>
    </c:otherwise>
  </c:choose>
	
	<div class="queryCommentChildren" id="queryCommentChildren.${param.commentId}">
	  <c:forEach var="childComment" items="${queryPageCommentChildren[param.commentId + 0]}">
	    <jsp:include page="/jsp/fragments/query_comment.jsp">
		  <jsp:param value="${childComment.id}" name="commentId"/>
		  <jsp:param value="${childComment.comment}" name="commentText"/>
		  <jsp:param value="${childComment.hidden}" name="commentHidden"/>
		  <jsp:param value="${param.reportMode}" name="reportMode"/>
		  <jsp:param value="${param.commentHidden or param.parentHidden}" name="parentHidden"/>
		  <jsp:param value="${childComment.created.time}" name="commentCreated"/>
      	  <jsp:param value="${childComment.lastModified.time}" name="commentModified"/>
		  <jsp:param value="${childComment.queryPage.id}" name="queryPageId"/>
		  <jsp:param value="${param.queryPageCommentable}" name="queryPageCommentable"/>
        </jsp:include>
	  </c:forEach>
    </div>
  </div>
</c:if>

