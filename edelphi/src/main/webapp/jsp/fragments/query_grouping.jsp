<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryGroupingQuestionContainer">

  <input type="hidden" id="groupingFieldValue" name="groupingFieldValue" value="${fn:escapeXml(param.groupingFieldValue)}"/>

  <div class="queryGroupingItemContainer">
    <c:if test="${param.optionCount gt 0}">
      <c:forEach begin="0" end="${param.optionCount - 1}" varStatus="vs">
        <c:set var="name" value="option.${vs.index}.name"></c:set>
        <c:set var="text" value="option.${vs.index}.text"></c:set>
      
        <div class="queryGroupingItem">
          <label class="queryMultiselectListItemLabel">${param[text]}</label>
          <input type="hidden" name="queryGroupingItemId" value="${param[name]}"/>
        </div>
      </c:forEach>
    </c:if>
  </div>

  <div class="queryGroupingGroupContainer">
    <c:if test="${param.groupCount gt 0}">
      <c:forEach begin="0" end="${param.groupCount - 1}" varStatus="vs">
        <c:set var="name" value="group.${vs.index}.name"></c:set>
        <c:set var="text" value="group.${vs.index}.text"></c:set>
        <c:set var="optionCount" value="group.${vs.index}.optionCount"></c:set>
        
        <div class="queryGroupingGroup">
          <label class="queryGroupingGroupLabel">${param[text]}</label>
          
          <input type="hidden" name="queryGroupingGroupId" value="${param[name]}"/>
          
          <div class="queryGroupingGroupItemContainer">
            <c:if test="${param[optionCount] gt 0}">
              <c:forEach begin="0" end="${param[optionCount] - 1}" varStatus="selItem">
                <div class="queryGroupingItem">
                  <c:set var="optionName" value="group.${vs.index}.option.${selItem.index}.name"></c:set>
                  <c:set var="optionText" value="group.${vs.index}.option.${selItem.index}.text"></c:set>
      
                  <label class="queryMultiselectListItemLabel">${param[optionText]}</label>
                  <div class="queryMultiselectListItemRemove"></div>
                  <input type="hidden" name="queryGroupingItemId" value="${param[optionName]}"/>
                </div>
              </c:forEach>
            </c:if>
          </div>
        </div>
      </c:forEach>
    </c:if>
  </div>
  
  <div class="clearBoth"></div>
  
</div>