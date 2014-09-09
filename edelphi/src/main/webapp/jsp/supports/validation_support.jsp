<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${validationSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/pfvlib/pfvlib-uncompressed.js"></script>
    <script type="text/javascript">
      document.observe("dom:loaded", function(event) {
        initializeValidation();
      });
    </script>
    <c:set scope="request" var="validationSupportIncluded" value="true"/>
  </c:when>
</c:choose>