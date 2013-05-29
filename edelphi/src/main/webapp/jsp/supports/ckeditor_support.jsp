<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${ckeditorSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/config/ckeditor.js"></script>
    <script type="text/javascript">
      CKEDITOR.config.contentsCss = THEMEPATH + '/css/ckcontents.css';
    </script>
    <c:set scope="request" var="ckeditorSupportIncluded" value="true"/>
  </c:when>
</c:choose>