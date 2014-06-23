<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${param.thesis != ''}">
  <div class="queryReportThesis">${param.thesis}</div>
</c:if>
<c:if test="${param.description != ''}">
  <div class="queryReportDescription">${param.description}</div>
</c:if>
