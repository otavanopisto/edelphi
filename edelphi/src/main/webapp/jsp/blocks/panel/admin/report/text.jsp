<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="queryReportPage" value="${queryReportPages[param.pageNumber]}" />

<div class="block">
  <div class="blockContent">

    <!-- Title -->

    <h2>${queryReportPage.pageTitle}</h2>

    <!-- Thesis -->

    <jsp:include page="/jsp/fragments/query_report_page_thesis.jsp">
      <jsp:param name="description" value="${queryReportPage.description}" />
    </jsp:include>

    <!-- Comments -->

    <jsp:include page="/jsp/blocks/panel/admin/report/querycomments.jsp">
      <jsp:param name="pageNumber" value="${param.pageNumber}"/>
    </jsp:include>

  </div>

</div>

