<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="queryPageId" value="${param['queryPageId'] + 0}"/>

<c:if test="${!empty queryPageThesises[queryPageId]}">
   <c:if test="${!empty queryPageThesises[queryPageId].text}">
     <div class="queryReportThesis">${queryPageThesises[queryPageId].text}</div>
   </c:if>
   <c:if test="${!empty queryPageThesises[queryPageId].description}">
     <div class="queryReportDescription">${queryPageThesises[queryPageId].description}</div>
   </c:if>
</c:if>
