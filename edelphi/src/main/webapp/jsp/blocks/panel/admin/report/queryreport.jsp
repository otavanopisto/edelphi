<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="pageNumber" value="0"/>
<c:forEach var="page" items="${queryReportPages}">
  <div class="queryReportPage">
    <input type="hidden" name="queryPageId" value="${page.queryPageId}"/>
    <jsp:include page="${page.jspFile}">
      <jsp:param name="pageNumber" value="${pageNumber}"/>
    </jsp:include>
  </div>
  <c:set var="pageNumber" value="${pageNumber + 1}"/>
</c:forEach>