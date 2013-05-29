<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="/jsp/supports/scripty2_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/jsonutils_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/locale_support.jsp"></jsp:include>

<c:choose>
  <c:when test="${blockControllerSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/blockcontroller.js"></script>
    <c:set scope="request" var="blockControllerSupportIncluded" value="true"/>
  </c:when>
</c:choose>