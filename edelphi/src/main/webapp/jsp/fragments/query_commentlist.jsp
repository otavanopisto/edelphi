<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="queryPageId" value="${param['queryPageId'] + 0}"/>
<c:set var="queryPageCommentable" value="${param['queryPageCommentable']}"/>

<c:if test="${empty queryPageId}">
  <c:set var="queryPageId" value="${param.queryPageId + 0}"/>
  <c:set var="queryPageCommentable" value="${param.queryPageCommentable}"/>
</c:if>

<c:if test="${fn:length(commentAnswers) gt 0}">
  <c:set var="printCommentAnswers" value="true"/>
</c:if>

<c:if test="${fn:length(queryPageComments[queryPageId]) gt 0}">
  <div class="queryCommentList" id="queryCommentList">
    <h2 class="querySubTitle queryCommentListSubTitle">
      <fmt:message key="panel.block.query.commentListTitle"></fmt:message>
      <c:if test="${!param.reportMode eq true}">
        (${queryPageCommentCount})
      </c:if>
      <div class="queryCommentsShowHideToggle hideIcon">
        <span class="hideCommentsTextContainer"><fmt:message key="panel.block.query.commentHideLabel"></fmt:message></span>
        <span class="showCommentsTextContainer"><fmt:message key="panel.block.query.commentShowLabel"></fmt:message></span>
      </div>
      <div class="queryCommentsSortToggle">
        <span class="sortCommentsTextContainer"><fmt:message key="panel.block.query.commentSortLabel"></fmt:message></span>
        <div class="queryCommentsSortActionsContainer">
          <div class="commentsSortArrowTop"></div>
          <div class="commentsSortSubTitle">Ajan mukaan</div>
          <div class="commentsSortActionTitle">Uusin ylimpänä</div>
          <div class="commentsSortActionTitle">Vanhin ylimpänä</div>
          <div class="commentsSortSubTitle">Asteikon arvojen mukaan</div>
          <div class="commentsSortActionTitle">+++ ylimpänä</div>
          <div class="commentsSortActionTitle">--- ylimpänä</div>
        </div>
      </div>
      
    </h2>
    
    <div class="queryCommentsContainer">
      <c:forEach var="comment" items="${queryPageComments[queryPageId]}">
        <c:set var="commentFiltered" value="true" />
        <c:choose>
          <c:when test="${param.reportMode eq true && !empty queryPageReplys}">
            <c:forEach var="reply" items="${queryPageReplys[queryPageId]}">
              <c:if test="${reply.id eq comment.queryReply.id}">
                <c:set var="commentFiltered" value="false" />
              </c:if>
            </c:forEach>
          </c:when>
          <c:otherwise>
            <c:set var="commentFiltered" value="false" />
          </c:otherwise>
        </c:choose>
        <jsp:include page="/jsp/fragments/query_comment.jsp">
          <jsp:param value="${param.reportMode}" name="reportMode"/>
          <jsp:param value="${printCommentAnswers}" name="printCommentAnswers"/>
          <jsp:param value="${comment.id}" name="commentId"/>
          <jsp:param value="${comment.comment}" name="commentText"/>
          <jsp:param value="${comment.hidden}" name="commentHidden"/>
          <jsp:param value="${commentFiltered}" name="commentFiltered"/>
          <jsp:param value="${comment.created.time}" name="commentCreated"/>
          <jsp:param value="${comment.lastModified.time}" name="commentModified"/>
          <jsp:param value="${queryPageId}" name="queryPageId"/>
          <jsp:param value="${queryPageCommentable}" name="queryPageCommentable"/>
        </jsp:include>
      </c:forEach>
    </div>
  </div>
</c:if>