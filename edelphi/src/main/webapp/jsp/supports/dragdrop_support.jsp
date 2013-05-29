<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${dragdropSupportIncluded != true}">
    <jsp:include page="scripty2_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/dragdrop/dragdrop.js"></script>
    <c:set scope="request" var="dragdropSupportIncluded" value="true"/>
  </c:when>
</c:choose>