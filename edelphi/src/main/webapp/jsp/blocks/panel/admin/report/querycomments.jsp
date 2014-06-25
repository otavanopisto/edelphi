<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
  pageContext.setAttribute("newLineChar", "\n");
%>
<c:set var="queryReportPage" value="${queryReportPages[param.pageNumber]}" />
<div class="queryCommentsContainer">
  <c:forEach var="comment" items="${queryReportPage.comments}">

    <div class="queryComment">
      <div class="queryCommentHeader">
        <div class="queryCommentDate">
          <fmt:message key="query.comment.commentDate" />
          <fmt:formatDate value="${comment.date}" type="both" />
        </div>
      </div>

      <div class="queryCommentText">
        <c:forEach var="answer" items="${comment.answers}">
          <div>
            <b>${fn:escapeXml(answer.key)}:</b> ${fn:escapeXml(answer.value)}
          </div>
        </c:forEach>
        <c:choose>
          <c:when test="${comment.filtered}">
            <i>&lt; <fmt:message key="query.comment.commentFiltered" /> &gt;
            </i>
          </c:when>
          <c:otherwise>
            <span>${fn:replace(fn:escapeXml(comment.comment), newLineChar, "<br/>")}</span>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- TODO Proper recursion -->

      <div class="queryCommentChildren">
        <c:forEach var="reply" items="${comment.replies}">
          <div class="queryComment">
            <div class="queryCommentHeader">
              <div class="queryCommentDate">
                <fmt:message key="query.comment.commentDate" />
                <fmt:formatDate value="${comment.date}" type="both" />
              </div>
            </div>
            <div class="queryCommentText">
              <c:choose>
                <c:when test="${comment.filtered}">
                  <i>&lt; <fmt:message key="query.comment.commentFiltered" /> &gt;
                  </i>
                </c:when>
                <c:otherwise>
                  <span>${fn:replace(fn:escapeXml(comment.comment), newLineChar, "<br/>")}</span>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>

  </c:forEach>
</div>
