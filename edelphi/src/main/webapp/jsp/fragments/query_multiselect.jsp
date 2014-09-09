<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryMultiselectQuestionContainer">

  <c:if test="${param.optionsCount gt 0}">
    <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
      <c:set var="name" value="option.${vs.index}.name"></c:set>
      <c:set var="value" value="option.${vs.index}.value"></c:set>
      <c:set var="text" value="option.${vs.index}.text"></c:set>
      <c:set var="selected" value="option.${vs.index}.selected"></c:set>
      
      <div class="queryMultiselectListItemContainer">
        <div class="queryMultiselectListItemInputContainer">
          <c:choose>
            <c:when test="${param[selected] eq '1'}">
              <input class="queryMultiselectListItemInput" type="checkbox" name="${param[name]}" value="${param[value]}" checked="checked"/>
            </c:when>
            <c:otherwise>
              <input class="queryMultiselectListItemInput" type="checkbox" name="${param[name]}" value="${param[value]}"/>
            </c:otherwise>
          </c:choose>
        </div>
        <label class="queryMultiselectListItemLabel">${param[text]}</label>
      </div>
    </c:forEach>
  </c:if>
  
</div>