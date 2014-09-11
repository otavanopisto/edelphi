<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${flotrSupportIncluded != true}">
    <jsp:include page="base64_support.jsp"></jsp:include>
    <jsp:include page="canvas2image_support.jsp"></jsp:include>
    <jsp:include page="canvastext_support.jsp"></jsp:include>
    <jsp:include page="excanvas_support.jsp"></jsp:include>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/flotr/flotr.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/config/flotr.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui//flotr_valuelabels.js"></script>
    <c:set scope="request" var="flotrSupportIncluded" value="true"/>
  </c:when>
</c:choose>