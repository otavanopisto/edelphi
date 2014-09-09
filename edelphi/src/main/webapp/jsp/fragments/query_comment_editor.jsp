<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryCommentEditor" id="queryNewCommentThread">
  <h2 class="querySubTitle"><fmt:message key="panel.block.query.commentEditorTitle"></fmt:message></h2>

  <c:set var="disabled">false</c:set>
  <c:if test="${fn:length(queryPageCommentChildren[param['userCommentId'] + 0]) gt 0}">
    <c:set var="disabled">true</c:set>
    <c:set var="tooltip"><fmt:message key="panel.block.query.commentEditorDisabledTip"/></c:set>
  </c:if>
    
  <jsp:include page="/jsp/fragments/formfield_memo.jsp">
    <jsp:param name="name" value="comment" />
    <jsp:param name="classes" value="queryComment" />
    <jsp:param name="value" value="${param['userCommentContent']}" />
    <jsp:param name="disabled" value="${disabled}" />
    <jsp:param name="title" value="${tooltip}" />
  </jsp:include>

  <input type="hidden" name="queryPageId" value="${queryPageId}"/>
  <input type="hidden" name="newRepliesCount" value="0" id="newRepliesCount"/>

</div>