<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${eventsSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/fnievents/fnievents.js"></script>
    <c:set scope="request" var="eventsSupportIncluded" value="true"/>
  </c:when>
</c:choose>