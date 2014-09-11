<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${jsHashSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/jshash-2.2/md5.js"></script>
    <c:set scope="request" var="jsHashSupportIncluded" value="true"/>
  </c:when>
</c:choose>