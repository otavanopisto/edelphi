<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryTimeSerieQuestionContainer">
  <div class="queryTimeSerieQuestionFlotrContainer"></div>
  <c:if test="${param.fieldCount gt 0}">
    <c:forEach begin="0" end="${param.fieldCount - 1}" varStatus="vs">
      <c:set var="name" value="fieldName.${vs.index}"></c:set>
      <c:set var="value" value="fieldValue.${vs.index}"></c:set>
      <input type="hidden" name="${param[name]}" value="${param[value]}" class="queryTimeSerieQuestionInput queryTimeSerieQuestionInput_${vs.index}"/>
    </c:forEach>
  </c:if>
</div>