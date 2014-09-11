<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryScaleSliderQuestionContainer">
  
  <label class="queryScaleSliderLabel">${param['label']}</label>
  
  <c:if test="${param.optionsCount gt 0}">
    <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
      <c:set var="value" value="option.${vs.index}.value"></c:set>
      <c:set var="text" value="option.${vs.index}.text"></c:set>
      <c:set var="selected" value="option.${vs.index}.selected"></c:set>
      
      <c:choose>
        <c:when test="${param[selected] eq '1'}">
          <c:set var="selectedValue" value="${param[value]}"/>
        </c:when>
      </c:choose>
  
      <label class="queryScaleSliderItemLabel">${param[text]}</label>
      
      <input type="hidden" name="possibleValues" value="${param[value]}"/>
    </c:forEach>
  </c:if>
  
  <div class="queryScaleSliderTrack"></div>
  <input type="hidden" name="${param.name}" value="${empty(selectedValue) ? '' : selectedValue}" class="sliderValue"/>
  
</div>