<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${scripty2SupportIncluded != true}">    
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/scripty2/s2.js"></script>
    <c:set scope="request" var="scripty2SupportIncluded" value="true"/>
  </c:when>
</c:choose>