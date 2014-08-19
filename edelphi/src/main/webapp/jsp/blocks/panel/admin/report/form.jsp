<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
  pageContext.setAttribute("newLineChar", "\n");
%>

<c:set var="queryReportPage" value="${queryReportPages[param.pageNumber]}" />

<div class="block">
  <div class="blockContent">

    <!-- Title -->

    <h2>${queryReportPage.pageTitle}</h2>

    <!-- Fields -->

    <c:forEach var="field" items="${queryReportPage.fields}">
      <c:choose>
        <c:when test="${field.fieldType eq 'TEXT'}">
          <div>
            <b>${field.caption}</b>
          </div>
          <div>${field.value}</div>
          <p/>
        </c:when>
        <c:when test="${field.fieldType eq 'MEMO'}">
          <div>
            <b>${field.caption}</b>
          </div>
          <div>${fn:replace(fn:escapeXml(field.value), newLineChar, "<br/>")}</div>
          <p/>
        </c:when>
        <c:when test="${field.fieldType eq 'LIST'}">
          <ed:queryPageChart reportContext="${reportContext}" output="PNG" width="740" height="450" queryPageId="${queryReportPage.queryPageId}">
            <ed:param name="queryFieldId" value="${field.optionField.id}" />
            <ed:param name="dynamicSize" value="true" />
          </ed:queryPageChart>
        </c:when>
      </c:choose>
    </c:forEach>

    <!-- Comments -->

    <jsp:include page="/jsp/blocks/panel/admin/report/querycomments.jsp">
      <jsp:param name="pageNumber" value="${param.pageNumber}" />
    </jsp:include>

  </div>

</div>

