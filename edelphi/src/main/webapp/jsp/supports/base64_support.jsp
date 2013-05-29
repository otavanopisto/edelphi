<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${base64SupportIncluded != true}">
    <!--[if IE]>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/base64/base64.js"></script>
    -->
    <c:set scope="request" var="base64SupportIncluded" value="true"/>
  </c:when>
</c:choose>