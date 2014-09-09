<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${canvas2imageSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/canvas2image/canvas2image.js"></script>
    <c:set scope="request" var="canvas2imageSupportIncluded" value="true"/>
  </c:when>
</c:choose>