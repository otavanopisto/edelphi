<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryOrderingQuestionContainer">

  <input type="hidden" name="order" value="${param.order}" />

  <div class="queryOrderingField">
    <c:if test="${param.optionsCount gt 0}">
      <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
        <c:set var="name" value="item.${vs.index}.name"></c:set>
        <c:set var="value" value="item.${vs.index}.value"></c:set>
        <c:set var="text" value="item.${vs.index}.text"></c:set>
        
        <div class="queryOrderingFieldItemContainer">
          <label class="queryOrderingFieldItemIndexNumber">${vs.index + 1}.</label>
          <label class="queryOrderingFieldItemText">${param[text]}</label>
  
          <input type="hidden" name="itemName" value="${param[name]}"/>
          
          <c:if test="${not vs.first}">
            <div class="queryOrderingFieldItemMoveUpButton"></div>
          </c:if>
          
          <c:if test="${not vs.last}">
            <div class="queryOrderingFieldItemMoveDownButton"></div>
          </c:if>
        </div>
      </c:forEach>
    </c:if>
  </div>
  
</div>