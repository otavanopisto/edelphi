<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryFormFieldContainer">
  
  <c:choose>
    <c:when test="${param.listType eq 'RADIO'}">
      <label>${param.caption}</label> 

      <c:if test="${param.optionsCount gt 0}">
        <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
          <c:set var="value" value="option.${vs.index}.value"></c:set>
          <c:set var="label" value="option.${vs.index}.label"></c:set>
          <c:set var="selected" value="option.${vs.index}.selected"></c:set>
    
          <div>
            <c:choose>
              <c:when test="${param[selected] eq '1'}">
                <input type="radio" name="${param.name}" value="${param[value]}" id="${param.id}-${param[value]}" checked="checked"/>
              </c:when>
              <c:otherwise>
                <input type="radio" name="${param.name}" value="${param[value]}" id="${param.id}-${param[value]}"/>
              </c:otherwise>
            </c:choose>
            
             <label for="${param.id}-${param[value]}">${param[label]}</label>
           </div>
           
        </c:forEach>
      </c:if>
    </c:when>
    <c:when test="${param.listType eq 'SELECT'}">
      <label for="${param.id}">${param.caption}</label> 
  
      <select name="${param.name}" id="${param.id}">
        <c:if test="${param.optionsCount gt 0}">
          <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
            <c:set var="value" value="option.${vs.index}.value"></c:set>
            <c:set var="label" value="option.${vs.index}.label"></c:set>
            <c:set var="selected" value="option.${vs.index}.selected"></c:set>
      
            <c:choose>
              <c:when test="${param[selected] eq '1'}">
                <option value="${param[value]}" selected="selected">${param[label]}</option>
              </c:when>
              <c:otherwise>
                <option value="${param[value]}">${param[label]}</option>
              </c:otherwise>
            </c:choose>
      
          </c:forEach>
        </c:if>
      </select>
    </c:when>
    
    <c:when test="${param.listType eq 'SLIDER'}">
      <label for="${param.id}">${param.caption}</label> 
      
      <div class="queryFormSliderTrack"></div>  
      <c:if test="${param.optionsCount gt 0}">
        <c:forEach begin="0" end="${param.optionsCount - 1}" varStatus="vs">
          <c:set var="value" value="option.${vs.index}.value"></c:set>
          <c:set var="label" value="option.${vs.index}.label"></c:set>
          <c:set var="selected" value="option.${vs.index}.selected"></c:set>

          <c:choose>
	        <c:when test="${param[selected] eq '1'}">
	          <c:set var="selectedValue" value="${param[value]}"/>
	        </c:when>
	      </c:choose>
	  
  	      <label class="queryFormSliderItemLabel">${param[label]}</label>
	      <input type="hidden" name="possibleValues" value="${param[value]}"/>
     
        </c:forEach>
      </c:if>
      
	  <input type="hidden" name="${param.name}" value="${empty(selectedValue) ? '' : selectedValue}" class="sliderValue"/>      
    </c:when>
    
  </c:choose>
  
</div>